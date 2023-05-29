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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @description: SQLDatabaseTest
 * @author: HamaWhite
 */
class SQLDatabaseTest {

    private static SQLDatabase database;

    @BeforeAll
    public static void setup() throws SQLException {
        System.out.println(System.getProperty("user.dir"));
        database = new SQLDatabase("jdbc:h2:mem:demo;DATABASE_TO_UPPER=false", "root", "123456");

        database.run("RUNSCRIPT FROM '../scripts/h2/schema.sql'");
        database.run("RUNSCRIPT FROM '../scripts/h2/data.sql'");
    }

    @AfterAll
    static void cleanup() throws SQLException {
        database.close();
    }

    @Test
    void testGetDialect() throws SQLException {
        assertThat(database.getDialect()).isEqualTo("h2");
    }

    @Test
    void getUsableTableNames() throws SQLException {
        assertThat(database.getUsableTableNames()).isEqualTo(Set.of("students", "parents"));
    }
}