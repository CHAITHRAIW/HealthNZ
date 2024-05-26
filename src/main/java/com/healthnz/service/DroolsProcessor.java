package com.healthnz.service;

import com.healthnz.dao.DroolFileDAO;
import com.healthnz.dao.DroolRulesDAO;
import com.healthnz.dao.MuleFlowDAO;
import com.healthnz.utility.DroolRuleExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DroolsProcessor {
    private final DroolRuleExtractor droolRuleExtractor;
    private final DroolFileDAO droolFileDao;
    private final DroolRulesDAO droolRulesDAO;
    private final MuleFlowDAO muleFlowDAO;
    private static final long INITIAL_DELAY = 0L;
    private static final long PERIOD = 100L; // Check every 100 seconds
    private static final long MAX_RUNTIME = 60L; // Run for 1 minute

    @Value("${drools.rules.directory}")
    private String rulesDirectoryPath;

    private final ScheduledExecutorService executor;

    public DroolsProcessor(DroolRuleExtractor droolRuleExtractor, DroolFileDAO droolFileDao, DroolRulesDAO droolRulesDAO, MuleFlowDAO muleFlowDAO) {
        this.droolRuleExtractor = droolRuleExtractor;
        this.droolFileDao = droolFileDao;
        this.droolRulesDAO = droolRulesDAO;
        this.muleFlowDAO = muleFlowDAO;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        Runnable task = () -> checkDirectory(rulesDirectoryPath);

        executor.scheduleAtFixedRate(task, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
        executor.schedule(this::shutdown, MAX_RUNTIME, TimeUnit.SECONDS);
    }

    private void shutdown() {
        log.info("Executor service shutting down.");
        executor.shutdownNow();
    }

    private void checkDirectory(String directoryPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.drl")) {
            for (Path entry : stream) {
                processDroolFile(entry.toFile(), entry.getFileName().toString());
            }
        } catch (IOException e) {
            log.error("Error processing directory: {}", directoryPath, e);
        }
    }

    public void processDroolFile(File droolFile, String xmlFileName) {
        log.info("Processing DRL file: {}", droolFile.getName());
        try {
            if (muleFlowDAO.xmlFileNameExists(xmlFileName)) {
                List<String[]> rules = droolRuleExtractor.extractRules(droolFile.getAbsolutePath());
                droolFileDao.insertDroolFile(droolFile.getName(), xmlFileName);

                int fileId = droolFileDao.getFileIdByFileName(droolFile.getName());
                droolRulesDAO.insertDroolRule(fileId, rules);

              /* boolean isDeleted = Files.deleteIfExists(droolFile.toPath());
                if (isDeleted) {
                    log.info("DRL file deleted successfully: {}", droolFile.getName());
                } else {
                    log.info("No need to delete DRL file or file deletion failed: {}", droolFile.getName());
                }*/
            } else {
              /*  log.warn("Referenced XML file not found in mule_flow: {}", xmlFileName);
                boolean isDeleted = Files.deleteIfExists(droolFile.toPath());
                if (isDeleted) {
                    log.info("DRL file deleted successfully: {}", droolFile.getName());
                }*/
            }
        } catch (IOException e) {
            log.error("Error processing Drool file: {}", droolFile.getName(), e);
        }
    }
}
