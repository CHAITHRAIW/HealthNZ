package org.example.utility;

import java.sql.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DroolRulesDAO {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "mypassword";
    private static final Logger log = LoggerFactory.getLogger(DroolRulesDAO.class);

    public void insertDroolRule(int fileId, List<String[]> rules) {

        String sql = "INSERT INTO drool_rules (\"FileID\", \"RuleName\", \"RuleContent\") VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // Disable auto-commit for batch processing

            for (String[] rule : rules) {
                stmt.setInt(1, fileId); // FileID
                stmt.setString(2, rule[0]); // RuleName
                stmt.setString(3, rule[1]); // RuleContent
                stmt.addBatch(); // Add to batch
            }

            int[] rowsInserted = stmt.executeBatch(); // Execute batch

            conn.commit(); // Commit transaction

            log.info("Rows inserted into DroolRules: " + rowsInserted.length);

        } catch (SQLException e) {
            log.error("Error inserting DroolRule: " + e.getMessage());
        }
    }

}