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

@Log4j2
@Component
public class RuleMonitor {

    @Value("${drools.flow.directory:C:\\Temp\\Drools\\Flows}")
    private String flowDirectoryPath;

    @Value("${drools.rules.directory:C:\\Temp\\Drools\\Rules}")
    private String rulesDirectoryPath;

    private final MuleFlowDAO muleFlowDAO;
    private final DroolsProcessor droolsProcessor;

    public RuleMonitor(MuleFlowDAO muleFlowDAO, DroolsProcessor droolsProcessor) {
        this.muleFlowDAO = muleFlowDAO;
        this.droolsProcessor = droolsProcessor;
    }

    public void start() {
        checkDirectoryAndProcessFiles(flowDirectoryPath);
    }

    private void checkDirectoryAndProcessFiles(String directoryPath) {
        log.info("Checking directory: {}", directoryPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.xml")) {
            for (Path entry : stream) {
                processXmlFile(entry);
            }
        } catch (IOException e) {
            log.error("Error processing directory: {}", directoryPath, e);
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
                    File droolFile = new File(rulesDirectoryPath, rulesDefinition);
                    log.info("Queuing file for processing: {}", droolFile.getAbsolutePath());
                    droolsProcessor.addFileToProcess(new DroolFileDetail(droolFile, xmlFilePath.getFileName().toString()));
                }
            }

            boolean isDeleted = Files.deleteIfExists(xmlFilePath);
            if (isDeleted) {
                log.info("XML file deleted successfully: {}", xmlFilePath.getFileName());
            } else {
                log.info("No need to delete XML file or deletion failed: {}", xmlFilePath.getFileName());
            }
            droolsProcessor.start();

        } catch (IOException e) {
            log.error("Error reading file: " + xmlFilePath + "; " + e.getMessage());
        }
    }
}
