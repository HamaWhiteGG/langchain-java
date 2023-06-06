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

/**
 * @description: BasicDatabaseTest
 * @author: HamaWhite
 */
public abstract class BasicDatabaseTest {

    protected static SQLDatabase database;

    @BeforeAll
    public static void setup() {
        database = new SQLDatabase("jdbc:h2:mem:demo;DATABASE_TO_UPPER=false", "root", "123456");

        database.run("RUNSCRIPT FROM '../scripts/h2/schema.sql'", false);
        database.run("RUNSCRIPT FROM '../scripts/h2/data.sql'", false);
    }

    @AfterAll
    static void cleanup() {
        database.close();
    }
}
