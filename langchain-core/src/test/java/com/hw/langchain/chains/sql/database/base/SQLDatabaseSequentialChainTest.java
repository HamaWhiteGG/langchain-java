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

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class SQLDatabaseSequentialChainTest extends BasicDatabaseTest {

    protected static BaseLanguageModel llm;
    protected static SQLDatabaseSequentialChain chain;

    @BeforeAll
    public static void setup() {
        BasicDatabaseTest.setup();

        llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        chain = SQLDatabaseSequentialChain.fromLLM(llm, database);
    }

    @Test
    void testSQLDatabaseSequentialChain() {
        String actual = chain.run("who got zero score? why");
        String expected = "Ophelia got zero score because unfortunately, she missed the test.";
        assertEquals(expected, actual);
    }
}