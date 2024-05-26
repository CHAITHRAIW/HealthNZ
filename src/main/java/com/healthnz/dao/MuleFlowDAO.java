package com.healthnz.dao;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MuleFlowDAO extends BaseDAO {

    /**
     * Inserts a Mule flow data record into the database.
     *
     * @param xmlFileName  the name of the XML file
     * @param muleFlowName the name of the Mule flow
     */
    public void insertFlowData(String xmlFileName, String muleFlowName) {
        String insertSQL = "INSERT INTO public.mule_flow (\"xmlfilename\", \"muleflowname\") VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            stmt.setString(1, xmlFileName);
            stmt.setString(2, muleFlowName);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                log.info("Row inserted into mule_flow");
            }

        } catch (SQLException e) {
            log.error("Error inserting MuleFlow: {}", e.getMessage(), e);
        }
    }
    /**
     * Checks if a given xmlFileName exists in the mule_flow table.
     *
     * @param xmlFileName the name of the XML file
     * @return true if the xmlFileName exists, false otherwise
     */
    public boolean xmlFileNameExists(String xmlFileName) {
        String sql = "SELECT 1 FROM public.mule_flow WHERE \"xmlfilename\" = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, xmlFileName);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            log.error("Error checking if xmlFileName exists: {}", e.getMessage(), e);
        }

        return false; // Return false if the xmlFileName is not found or in case of an error
    }
}
