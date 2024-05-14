package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.opencsv.CSVWriter;
import lombok.extern.log4j.Log4j2;

import java.io.FileWriter;
import java.io.IOException;
@Log4j2
public class SqlToCsv {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "mypassword";
        String filePath = "RuleExecutionCount.csv";

       // SQL query to execute
        String sql = "SELECT " +
                "DR.rule_name, " +
                "DF.file_name, " +
                "DR.rule_content, " +
                "COUNT(*) AS ExecutionCount " +
                "FROM " +
                "healthnzrules_schema.drool_rules AS DR " + // Updated table name to lowercase and added AS for clarity
                "JOIN " +
                "healthnzrules_schema.drool_files AS DF ON DR.file_id = DF.file_id " + // Updated table name to lowercase and added AS for clarity
                "GROUP BY " +
                "DR.rule_name, DR.rule_content, DF.file_name " +
                "ORDER BY " +
                "DR.rule_name, DF.file_name;";


        // Connect to the database and execute the query
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery();
             CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            // Writing data to the CSV file
            writer.writeAll(rs, true); // true to include column names as header
            log.info("Data has been exported to CSV successfully.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}