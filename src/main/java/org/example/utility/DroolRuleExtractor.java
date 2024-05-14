package org.example.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class DroolRuleExtractor {

    public List<String[]> extractRules(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        List<String[]> rules = new ArrayList<>();

        // Regex to extract rule name and content
        Pattern pattern = Pattern.compile("rule\\s+\"([^\"]+)\"\\s*(.*?)end", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String ruleName = matcher.group(1).trim();
            String ruleContent = matcher.group(2).trim();
            rules.add(new String[]{ruleName, ruleContent});
        }

        return rules;
    }
}