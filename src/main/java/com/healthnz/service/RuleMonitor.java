package com.healthnz.service;

import com.healthnz.dao.MuleFlowDAO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Log4j2
@Component
public class RuleMonitor {

    private static final long INITIAL_DELAY = 0L;
    private static final long PERIOD = 100L; // Check every 100 seconds
    private static final long MAX_RUNTIME = 60L; // Run for 1 minute

    @Value("${drools.flow.directory}")
    private String flowDirectoryPath;

    private final MuleFlowDAO muleFlowDAO;
    private final DroolsProcessor droolsProcessor;
    private final ScheduledExecutorService executor;

    public RuleMonitor(MuleFlowDAO muleFlowDAO, DroolsProcessor droolsProcessor) {
        this.muleFlowDAO = muleFlowDAO;
        this.droolsProcessor = droolsProcessor;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        Runnable task = this::checkDirectory;

        executor.scheduleAtFixedRate(task, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
        executor.schedule(this::shutdown, MAX_RUNTIME, TimeUnit.SECONDS);
    }

    private void shutdown() {
        log.info("Executor service shutting down.");
        executor.shutdownNow();
    }

    private void checkDirectory() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(flowDirectoryPath), "*.xml")) {
            for (Path entry : stream) {
                processXmlFile(entry);
            }
        } catch (IOException e) {
            log.error("Error processing directory: {}", flowDirectoryPath, e);
        }
    }

    private void processXmlFile(Path xmlFilePath) {
        Pattern flowPattern = Pattern.compile("<flow name=\"([^\"]*)\">");
        Pattern rulesDefinitionPattern = Pattern.compile("rulesDefinition=\"([^\"]*)\"");

        try {
            String content = new String(Files.readAllBytes(xmlFilePath), "UTF-8");

            Matcher flowMatcher = flowPattern.matcher(content);
            Matcher rulesMatcher = rulesDefinitionPattern.matcher(content);

            String flowName = null;
            if (flowMatcher.find()) {
                flowName = flowMatcher.group(1);
                muleFlowDAO.insertFlowData(xmlFilePath.getFileName().toString(), flowName);
            }

            while (rulesMatcher.find()) {
                String rulesDefinition = rulesMatcher.group(1);
                if (flowName != null) {
                    droolsProcessor.processDroolFile(new File(flowDirectoryPath, rulesDefinition), xmlFilePath.getFileName().toString());
                }
            }

            boolean isDeleted = Files.deleteIfExists(xmlFilePath);
            if (isDeleted) {
                log.info("XML file deleted successfully: {}", xmlFilePath.getFileName());
            } else {
                log.info("No need to delete XML file or deletion failed: {}", xmlFilePath.getFileName());
            }

        } catch (IOException e) {
            log.error("Error reading file: " + xmlFilePath + "; " + e.getMessage());
        }
    }
}
