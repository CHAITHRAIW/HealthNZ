package com.healthnz.utility;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

@Log4j2
@Component
public class DroolRuleExtractor {

    public List<String[]> extractRules(String fullFilePath) throws IOException {

        String content = Files.readString(Paths.get(fullFilePath));
        List<String[]> rules = new ArrayList<>();

        // Regex to extract rule name and content
        Pattern pattern = Pattern.compile("rule\\s+\"([^\"]+)\"\\s*(.*?)end", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String ruleName = matcher.group(1).trim();
            String ruleContent = matcher.group(2).trim();
            rules.add(new String[]{ruleName, ruleContent});
        }

        // Log the extracted rules and the file path
        if (!rules.isEmpty()) {
            log.info("Extracted {} rules from file: {}", rules.size(), fullFilePath);
            rules.forEach(rule -> log.info("Rule Name: {}, Rule Content: {}", rule[0], rule[1]));
        } else {
            log.info("No rules extracted from file: {}", fullFilePath);
        }

        return rules;
    }
}