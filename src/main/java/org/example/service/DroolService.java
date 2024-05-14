package org.example.service;

import org.example.DroolFilesRepository;
import org.example.DroolRulesRepository;
import org.example.model.DroolFiles;
import org.example.model.DroolRules;
import org.example.utility.DroolRuleExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DroolService {

    @Autowired
    private DroolFilesRepository droolFilesRepository;
    @Autowired
    private DroolRulesRepository droolRulesRepository;
    private final DroolRuleExtractor droolRuleExtractor = new DroolRuleExtractor();

    public void storeDroolFileAndRules() throws IOException {
        Path drlsDirectoryPath = Paths.get("src/main/resources/drls");

        List<Path> drlFiles = Files.walk(drlsDirectoryPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".drl")).toList();

        // Iterate over each .drl file, extract rules, and save them to the database
        for (Path drlFile : drlFiles) {
            List<String[]> extractedRules = droolRuleExtractor.extractRules(drlFile.toString());
            saveRulesToDatabase(drlFile.getFileName().toString(), extractedRules);
        }
    }
    private void saveRulesToDatabase(String fileName, List<String[]> rules) {
        DroolFiles file = new DroolFiles();
        file.setFileName(fileName);
        file = droolFilesRepository.save(file);

        for (String[] rule : rules) {
            DroolRules droolRule = new DroolRules();
            droolRule.setFileId(file.getFileId());
            droolRule.setRuleName(rule[0]);
            droolRule.setRuleContent(rule[1]);
            droolRulesRepository.save(droolRule);
        }
    }
}