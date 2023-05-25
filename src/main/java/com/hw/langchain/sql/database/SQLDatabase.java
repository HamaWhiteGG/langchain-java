package com.hw.langchain.sql.database;

import org.apache.commons.collections4.CollectionUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * @description: SQLAlchemy wrapper around a database.
 * @author: HamaWhite
 */
public class SQLDatabase {

    private Connection connection;

    private List<String> includeTables;

    private List<String> ignoreTables;

    private int sampleRowsInTableInfo = 3;

    public SQLDatabase(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public SQLDatabase(String url, String username, String password, List<String> includeTables, List<String> ignoreTables, int sampleRowsInTableInfo) throws SQLException {
        this(url, username, password);
        if (CollectionUtils.isNotEmpty(includeTables) && CollectionUtils.isNotEmpty(ignoreTables)) {
            throw new IllegalArgumentException("Cannot specify both includeTables and ignoreTables");
        }
        this.connection = DriverManager.getConnection(url, username, password);
        this.includeTables = includeTables;
        this.ignoreTables = ignoreTables;
        this.sampleRowsInTableInfo = sampleRowsInTableInfo;
    }

    public String getDialect() throws SQLException {
        return connection.getMetaData().getDatabaseProductName();
    }


    public void close() throws SQLException {
        if(connection!=null) {
            connection.close();
        }
    }
}
