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

import cn.hutool.core.collection.ListUtil;
import lombok.var;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SQLDatabaseTest
 * @author HamaWhite
 */
class SQLDatabaseTest extends BasicDatabaseTest {

    @Test
    void testGetDialect() {
        assertThat(database.getDialect()).isEqualTo("h2");
    }

    @Test
    void testGetUsableTableNames() {
        var actual = database.getUsableTableNames();
        var expected = ListUtil.of("students", "parents");
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void testGetTableDdl() {
        var actual = database.getTableDdl("students");
        var expected = "" +
                "" +
                "                CREATE TABLE students (\n" +
                "                \tid INTEGER(32),\n" +
                "                \tname CHARACTER VARYING(64),\n" +
                "                \tscore INTEGER(32) COMMENT 'math score',\n" +
                "                \tteacher_note CHARACTER VARYING(256)\n" +
                "                ) COMMENT 'student score table'\n" +
                "" +
                "";
        assertEquals(expected, actual);
    }

    @Test
    void testGetSampleRows() {
        var actual = database.getSampleRows("students");
        var expected = "" +
                "3 rows from students table:\n" +
                "id\tname\tscore\tteacher_note\n" +
                "1\tAlex\t100\tAlex did perfectly every day in the class.\n" +
                "2\tAlice\t70\tAlice needs a lot of improvements.\n" +
                "3\tJack\t75\tEvent it is not the best, Jack has already improved." +
                "";
        assertEquals(expected, actual);
    }

    @Test
    void testGetTableInfo() {
        var actual = database.getTableInfo(null);
        var expected = "" +
                "\n" +
                "                CREATE TABLE parents (\n" +
                "                \tid INTEGER(32),\n" +
                "                \tstudent_name CHARACTER VARYING(64),\n" +
                "                \tparent_name CHARACTER VARYING(64),\n" +
                "                \tparent_mobile CHARACTER VARYING(16)\n" +
                "                )\n" +
                "\n" +
                "                /*\n" +
                "                3 rows from parents table:\n" +
                "                id\tstudent_name\tparent_name\tparent_mobile\n" +
                "                1\tAlex\tBarry\t088121\n" +
                "                2\tAlice\tJessica\t088122\n" +
                "                3\tJack\tSimon\t088123\n" +
                "                */\n" +
                "\n" +
                "\n" +
                "                CREATE TABLE students (\n" +
                "                \tid INTEGER(32),\n" +
                "                \tname CHARACTER VARYING(64),\n" +
                "                \tscore INTEGER(32) COMMENT 'math score',\n" +
                "                \tteacher_note CHARACTER VARYING(256)\n" +
                "                ) COMMENT 'student score table'\n" +
                "\n" +
                "                /*\n" +
                "                3 rows from students table:\n" +
                "                id\tname\tscore\tteacher_note\n" +
                "                1\tAlex\t100\tAlex did perfectly every day in the class.\n" +
                "                2\tAlice\t70\tAlice needs a lot of improvements.\n" +
                "                3\tJack\t75\tEvent it is not the best, Jack has already improved.\n" +
                "                */" +
                "";
        assertEquals(expected, actual);
    }
}