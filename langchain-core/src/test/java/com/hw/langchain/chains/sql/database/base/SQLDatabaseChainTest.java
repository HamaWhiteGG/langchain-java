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

package com.hw.langchain.chains.sql.database.base;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.sql.database.BasicDatabaseTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <a href="https://github.com/sugarforever/LangChain-SQL-Chain/blob/main/Chat_with_SQL_Database.ipynb">LangChain-SQL-Chain</a>
 *
 * SQLDatabaseChainTest
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class SQLDatabaseChainTest extends BasicDatabaseTest {

    protected static BaseLanguageModel llm;
    protected static SQLDatabaseChain chain;

    @BeforeAll
    public static void setup() {
        BasicDatabaseTest.setup();

        llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        chain = SQLDatabaseChain.fromLLM(llm, database);
    }

    @Test
    void testFirstRun() {
        String actual = chain.run("How many students are there?");
        String expected = "There are 6 students.";
        assertEquals(expected, actual);
    }

    @Test
    void testSecondRun() {
        String actual = chain.run("What are the students names?");
        String expected = "The students names are Alex, Alice, Jack, Ophelia, and Zack.";
        // SELECT `name` FROM `students` LIMIT 5;
        assertEquals(expected, actual);
    }

    @Test
    void testThirdRun() {
        String actual = chain.run("What's the average score of them?");
        String expected = "The average score of them is 66.66666666666667.";
        assertEquals(expected, actual);
    }

    @Test
    void testFourthRun() {
        String actual = chain.run("What's the average score of them, excluding the zero score?");
        String expected = "The average score of them, excluding the zero score, is 80.0.";
        assertEquals(expected, actual);
    }

    @Test
    void testFifthRun() {
        String actual = chain.run("Who got zero score? Why?");
        String expected = "Ophelia got zero score because unfortunately, she missed the test.";
        assertEquals(expected, actual);
    }

    @Test
    void testSixthRun() {
        String actual = chain.run("Who got zero score? Show me her parent's contact information.");
        String expected =
                "The parent of the student who got zero score is Tracy and their contact information is 088124.";
        assertEquals(expected, actual);
    }
}