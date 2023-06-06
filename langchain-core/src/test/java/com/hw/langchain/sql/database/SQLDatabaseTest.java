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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @description: SQLDatabaseTest
 * @author: HamaWhite
 */
class SQLDatabaseTest extends BasicDatabaseTest {

    @Test
    void testGetDialect() {
        assertThat(database.getDialect()).isEqualTo("h2");
    }

    @Test
    void testGetUsableTableNames() {
        var actual = database.getUsableTableNames();
        var expected = List.of("students", "parents");
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void testGetTableDdl() {
        var actual = database.getTableDdl("students");
        var expected = """

                CREATE TABLE students (
                	id INTEGER(32),
                	name CHARACTER VARYING(64),
                	score INTEGER(32) COMMENT 'math score',
                	teacher_note CHARACTER VARYING(256)
                ) COMMENT 'student score table'

                """;
        assertEquals(expected, actual);
    }

    @Test
    void testGetSampleRows() {
        var actual = database.getSampleRows("students");
        var expected = """
                3 rows from students table:
                id	name	score	teacher_note
                1	Alex	100	Alex did perfectly every day in the class.
                2	Alice	70	Alice needs a lot of improvements.
                3	Jack	75	Event it is not the best, Jack has already improved.""";
        assertEquals(expected, actual);
    }

    @Test
    void testGetTableInfo() {
        var actual = database.getTableInfo(null);
        var expected = """

                CREATE TABLE parents (
                	id INTEGER(32),
                	student_name CHARACTER VARYING(64),
                	parent_name CHARACTER VARYING(64),
                	parent_mobile CHARACTER VARYING(16)
                )

                /*
                3 rows from parents table:
                id	student_name	parent_name	parent_mobile
                1	Alex	Barry	088121
                2	Alice	Jessica	088122
                3	Jack	Simon	088123
                */


                CREATE TABLE students (
                	id INTEGER(32),
                	name CHARACTER VARYING(64),
                	score INTEGER(32) COMMENT 'math score',
                	teacher_note CHARACTER VARYING(256)
                ) COMMENT 'student score table'

                /*
                3 rows from students table:
                id	name	score	teacher_note
                1	Alex	100	Alex did perfectly every day in the class.
                2	Alice	70	Alice needs a lot of improvements.
                3	Jack	75	Event it is not the best, Jack has already improved.
                */""";
        assertEquals(expected, actual);
    }
}