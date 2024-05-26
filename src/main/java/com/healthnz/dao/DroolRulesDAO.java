package com.healthnz.dao;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class DroolRulesDAO extends BaseDAO {

    /**
     * Inserts Drool rules into the database.
     *
     * @param fileId the file ID associated with the rules
     * @param rules  the list of rules to insert
     */
    public void insertDroolRule(int fileId, List<String[]> rules) {
        String sql = "INSERT INTO drool_rules (\"fileid\", \"rulename\", \"rulecontent\") VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
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

            log.info("Rows inserted into DroolRules: {}", rowsInserted.length);

        } catch (SQLException e) {
            log.error("Error inserting DroolRule: {}", e.getMessage(), e);
        }
    }
}