package com.healthnz.dao;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DroolFileDAO extends BaseDAO {

    /**
     * Inserts a Drool file record into the database.
     *
     * @param flowFileName the name of the flow file
     * @param drlFileName  the name of the DRL file
     */
    public void insertDroolFile(String drlFileName , String flowFileName) {
        String sql = "INSERT INTO drool_files (\"filename\", \"xmlfilename\") VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, drlFileName);
            stmt.setString(2, flowFileName);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                log.info("Row inserted into drool_files");
            }

        } catch (SQLException e) {
            log.error("Error inserting DroolFile: {}", e.getMessage(), e);
        }
    }

    /**
     * Retrieves the file ID for a given file name from the database.
     *
     * @param filename the name of the file
     * @return the file ID, or -1 if the file is not found
     */
    public int getFileIdByFileName(String filename) {
        String sql = "SELECT \"fileid\" FROM public.drool_files WHERE \"filename\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filename);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("fileid");
                }
            }

        } catch (SQLException e) {
            log.error("Error retrieving FileID: {}", e.getMessage(), e);
        }

        return -1; // Return -1 if the file is not found
    }
}
