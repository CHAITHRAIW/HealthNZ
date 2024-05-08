package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RuleFinder {
    static String flowXmlFilePath = "src/main/resources/flowXmls";
    static String drlDirectoryPath = "src/main/resources/drls";
    public static void main(String[] args) {
        /* Save the flow name and respective Drool file name */

        String outputFilePath = "src/main/resources/DroolFileNameAndFlowName.txt";
        extractFlowNameAndRuleDefinition(flowXmlFilePath, outputFilePath);

        /* Count the number of Occurrences of the Rules across the Drool files */
        String csvFilePath = "src/main/resources/RuleCounter.csv";
        Map<String, Integer> ruleCounts = countAllRuleOccurrences(drlDirectoryPath);
        saveResultsToCSV(csvFilePath, ruleCounts);
        log.info("Rule counts have been saved to " + csvFilePath);
    }

      public static void extractFlowNameAndRuleDefinition(String directoryPath, String outputFilePath) {
        // Define the patterns to extract the flow name and rulesDefinition
        Pattern flowPattern = Pattern.compile("<flow name=\"([^\"]*)\">");
        Pattern rulesDefinitionPattern = Pattern.compile("rulesDefinition=\"([^\"]*)\"");
        Pattern rulesNamePattern = Pattern.compile("rule\\s+\"([^\"]+)\"");

          // Prepare to write to the output file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Walk through all files in the directory
            try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
                paths.filter(Files::isRegularFile) // Filter to include only files
                        .filter(path -> path.toString().endsWith(".xml")) // Filter to include only XML files
                        .forEach(path -> {
                            try {
                                // Read all lines from the XML file
                                String content = new String(Files.readAllBytes(path), "UTF-8");

                                // Create matchers to find matches of the patterns
                                Matcher flowMatcher = flowPattern.matcher(content);
                                Matcher rulesMatcher = rulesDefinitionPattern.matcher(content);

                                // Write the XML file name to the output file
                                writer.write("XML File Name: " + path.getFileName());
                                writer.newLine();

                                // Check for flow name match
                                if (flowMatcher.find()) {
                                    String flowName = flowMatcher.group(1);
                                    writer.write("Flow Name: " + flowName);
                                } else {
                                    writer.write("No flow name found.");
                                }
                                writer.newLine();

                                // Check for rulesDefinition matches
                                boolean foundRule = false;
                                while (rulesMatcher.find()) {
                                    foundRule = true;
                                    String rulesDefinition = rulesMatcher.group(1);
                                    writer.write("Rules Definition: " + rulesDefinition);
                                    writer.newLine();

                                    // Open the file and read lines
                                    try (BufferedReader reader = new BufferedReader(new FileReader(drlDirectoryPath+"/"+rulesDefinition)))
                                    {
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            // Match the pattern in the current line
                                            Matcher matcher = rulesNamePattern.matcher(line);
                                            while (matcher.find()) {
                                                // Print the rule definition found
                                                String ruleName = matcher.group(1);
                                                writer.write("Rules: " + ruleName);
                                                writer.newLine();
                                            }
                                        }
                                    } catch (IOException e) {
                                        log.error("Error reading file: " + drlDirectoryPath+"/"+rulesDefinition);
                                        e.printStackTrace();
                                    }

                                }
                                if (!foundRule) {
                                    writer.write("No rules definition found.");
                                    writer.newLine();
                                }
                                writer.newLine();

                            } catch (IOException e) {
                                log.error("Error reading file: " + path + "; " + e.getMessage());
                            }
                        });
            }
        } catch (IOException e) {
            log.error("Error processing the directory: " + e.getMessage());
        }
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
        // Sort the map by values in descending order
        List<Map.Entry<String, Integer>> sortedEntries = ruleCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).toList();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            writer.write("Rule Name,Count\n"); // CSV header
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                writer.write("\"" + entry.getKey() + "\"," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            log.error("Error writing to CSV file: " + csvFilePath);
            e.printStackTrace();
        }
    }
}