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

    public SQLDatabase(String url, String username, String password, List<String> includeTables,
            List<String> ignoreTables, int sampleRowsInTableInfo) throws SQLException {
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
        if (connection != null) {
            connection.close();
        }
    }
}
