package org.example;

import lombok.extern.log4j.Log4j2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class RuleCounterToCSV {

    public static void main(String[] args) {
        String directoryPath = "src/main/resources/drls";
        String csvFilePath = "src/main/resources/RuleCounter.csv";
        Map<String, Integer> ruleCounts = countAllRuleOccurrences(directoryPath);
        saveResultsToCSV(csvFilePath, ruleCounts);
        log.info("Rule counts have been saved to " + csvFilePath);
    }

    public static Map<String, Integer> countAllRuleOccurrences(String directoryPath) {
        File dir = new File(directoryPath);
        Map<String, Integer> ruleCounts = new HashMap<>();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".drl"));
            if (files != null) {
                for (File file : files) {
                    updateRuleCountsFromFile(file, ruleCounts);
                }
            }
        }
        return ruleCounts;
    }

    private static void updateRuleCountsFromFile(File file, Map<String, Integer> ruleCounts) {
        Pattern pattern = Pattern.compile("rule\\s+\"([^\"]+)\"");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String ruleName = matcher.group(1);
                    ruleCounts.put(ruleName, ruleCounts.getOrDefault(ruleName, 0) + 1);
                }
            }
        } catch (IOException e) {
            log.error("Error reading file: " + file.getPath());
            e.printStackTrace();
        }
    }

    private static void saveResultsToCSV(String csvFilePath, Map<String, Integer> ruleCounts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            writer.write("Rule Name,Count\n"); // CSV header
            for (Map.Entry<String, Integer> entry : ruleCounts.entrySet()) {
                writer.write("\"" + entry.getKey() + "\"," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
           log.error("Error writing to CSV file: " + csvFilePath);
            e.printStackTrace();
        }
    }
}