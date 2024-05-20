package org.example.utility;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.sql.*;

@Log4j2
@Component
public class DroolFileDAO {
     static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
     static final String DB_USER = "postgres";
     static final String DB_PASSWORD = "mypassword";

      public void insertDroolFile(String fileName) {
          String sql = "INSERT INTO drool_files (\"FileName\") VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fileName);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                log.info("Row inserted into drool_files");
            }

        } catch (SQLException e) {
            log.error("Error inserting DroolFile: " + e.getMessage());
        }
    }

    public int getFileIdByFileName(String fileName) {

        String sql = "SELECT \"FileID\" FROM public.drool_files WHERE \"FileName\" = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fileName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("FileID");
                }
            }

        } catch (SQLException e) {
            log.error("Error retrieving FileID: " + e.getMessage());
        }

        return -1; // Return -1 if the file is not found
    }
}
