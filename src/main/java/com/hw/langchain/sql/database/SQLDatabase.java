/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hw.langchain.sql.database;

import org.apache.commons.collections4.CollectionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: SQLAlchemy wrapper around a database.
 * @author: HamaWhite
 */
public class SQLDatabase {

    private Connection connection;

    private Set<String> includeTables;

    private Set<String> ignoreTables;

    private int sampleRowsInTableInfo = 3;

    public SQLDatabase(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public SQLDatabase(String url, String username, String password, Set<String> includeTables,
            Set<String> ignoreTables, int sampleRowsInTableInfo) throws SQLException {
        this(url, username, password);
        if (CollectionUtils.isNotEmpty(includeTables) && CollectionUtils.isNotEmpty(ignoreTables)) {
            throw new IllegalArgumentException("Cannot specify both includeTables and ignoreTables");
        }
        this.connection = DriverManager.getConnection(url, username, password);
        this.includeTables = includeTables;
        this.ignoreTables = ignoreTables;
        this.sampleRowsInTableInfo = sampleRowsInTableInfo;
    }

    /**
     * Dialect will convert to lowercase
     */
    public String getDialect() throws SQLException {
        return connection.getMetaData()
                .getDatabaseProductName()
                .toLowerCase();
    }

    /**
     * Get names of tables available.
     */
    public Set<String> getUsableTableNames() throws SQLException {
        if (CollectionUtils.isNotEmpty(includeTables)) {
            return includeTables;
        }

        Set<String> allTables = new HashSet<>();
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet =
                metaData.getTables(connection.getCatalog(), connection.getSchema(), null, new String[]{"TABLE"})) {
            while (resultSet.next()) {
                allTables.add(resultSet.getString("TABLE_NAME"));
            }
        }
        if (CollectionUtils.isNotEmpty(ignoreTables)) {
            allTables.removeAll(ignoreTables);
        }
        return allTables;
    }

    /**
     * Execute a SQL command and return a string representing the results.
     *
     * <p>If the statement returns rows, a string of the results is returned.
     * <p>If the statement returns no rows, an empty string is returned.
     */
    public String run(String command) throws SQLException {
        List<String> resultList = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            if (stmt.execute(command)) {
                ResultSet rs = stmt.getResultSet();
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    List<Object> rowList = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowList.add(rs.getObject(i));
                    }
                    String row = rowList.stream()
                            .map(e -> String.format("'%s'", e.toString()))
                            .collect(Collectors.joining(", ", "(", ")"));
                    resultList.add(row);
                }
            } else {
                int updateCount = stmt.getUpdateCount();
                resultList.add("Update Count: " + updateCount);
            }
        }
        return resultList.toString();
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
