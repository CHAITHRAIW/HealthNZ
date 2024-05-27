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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DroolsProcessor {
    private final DroolRuleExtractor droolRuleExtractor;
    private final DroolFileDAO droolFileDao;
    private final DroolRulesDAO droolRulesDAO;
    private final MuleFlowDAO muleFlowDAO;

    @Value("${drools.rules.directory:C:\\Temp\\Drools\\Rules}")
    private String rulesDirectoryPath;

    private final List<DroolFileDetail> droolFilesToProcess = new ArrayList<>();

    public DroolsProcessor(DroolRuleExtractor droolRuleExtractor, DroolFileDAO droolFileDao, DroolRulesDAO droolRulesDAO, MuleFlowDAO muleFlowDAO) {
        this.droolRuleExtractor = droolRuleExtractor;
        this.droolFileDao = droolFileDao;
        this.droolRulesDAO = droolRulesDAO;
        this.muleFlowDAO = muleFlowDAO;
    }

    public void start() {
        processQueuedDroolFiles();
    }

    public void addFileToProcess(DroolFileDetail droolFileDetail) {
        droolFilesToProcess.add(droolFileDetail);
        log.info("Added file to process queue: {} from XML: {}", droolFileDetail.getDroolFile().getAbsolutePath(), droolFileDetail.getXmlFileName());
    }

    private void processQueuedDroolFiles() {
        for (DroolFileDetail droolFileDetail : droolFilesToProcess) {
            processDroolFile(droolFileDetail);
        }
    }

    public void processDroolFile(DroolFileDetail droolFileDetail) {
        File droolFile = droolFileDetail.getDroolFile();
        String xmlFileName = droolFileDetail.getXmlFileName();
        try {
            String absolutePath = droolFile.getAbsolutePath();
            log.info("Drool file path before processing: {}", absolutePath);

          /*  if (muleFlowDAO.xmlFileNameExists(xmlFileName)) {*/
                log.info("Processing DRL file: {}", droolFile.getName());
                List<String[]> rules = droolRuleExtractor.extractRules(absolutePath);
                droolFileDao.insertDroolFile(droolFile.getName(), xmlFileName);

                int fileId = droolFileDao.getFileIdByFileName(droolFile.getName());
                log.debug("File ID retrieved: {}", fileId);
                droolRulesDAO.insertDroolRule(fileId, rules);
          /*  } else {
                log.warn("Referenced XML file not found in mule_flow: {}", xmlFileName);
            }*/
        } catch (IOException e) {
            log.error("Error processing Drool file: {}", droolFile.getAbsolutePath(), e);
        }
    }
}
