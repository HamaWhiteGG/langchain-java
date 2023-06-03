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
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.sql.database.SQLDatabase;

import java.util.List;
import java.util.Map;

/**
 * @description: Chain for interacting with SQL Database.
 * @author: HamaWhite
 */
public class SQLDatabaseChain extends Chain {

    private LLMChain llmChain;

    /**
     * SQL Database to connect to.
     */
    private SQLDatabase database;

    /**
     * Number of results to return from the query
     */
    private int topK;

    private String inputKey = "query";

    private String outputKey = "result";

    /**
     * Whether or not to return the intermediate steps along with the final answer.
     */
    private boolean returnIntermediateSteps = false;

    /**
     * Whether or not to return the result of querying the SQL table directly.
     */
    private boolean returnDirect = false;

    /**
     * Whether or not the query checker tool should be used to attempt to fix the initial SQL from the LLM.
     */
    private boolean useQueryChecker = false;

    /**
     * The prompt template that should be used by the query checker
     */
    private BasePromptTemplate queryCheckerPrompt;

    public SQLDatabaseChain(LLMChain llmChain, SQLDatabase database) {
        this.llmChain = llmChain;
        this.database = database;
    }

    public static SQLDatabaseChain fromLLM(BaseLanguageModel llm, SQLDatabase database, BasePromptTemplate prompt,
            Map<String, Object> kwargs) {
        // if (prompt == null) {
        // prompt = SQL_PROMPTS.getOrDefault(database.getDialect(), PROMPT);
        // }
        // LLMChain llmChain = new LLMChain(llm, prompt);
        // return new SQLDatabaseChain(llmChain, database, kwargs);
        return null;
    }

    @Override
    public List<String> inputKeys() {
        return null;
    }

    @Override
    public List<String> outputKeys() {
        return null;
    }

    @Override
    public Map<String, String> _call(Map<String, ?> inputs) {
        return null;
    }
}
