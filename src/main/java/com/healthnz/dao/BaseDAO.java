package com.healthnz.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAO {

    @Autowired
    private DataSource dataSource;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Gets a connection to the database.
     *
     * @return a Connection object
     * @throws SQLException if a database access error occurs
     */
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
