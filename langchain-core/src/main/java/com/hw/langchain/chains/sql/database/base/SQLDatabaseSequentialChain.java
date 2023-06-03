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

import static com.hw.langchain.chains.sql.database.prompt.Prompt.DECIDER_PROMPT;
import static com.hw.langchain.chains.sql.database.prompt.Prompt.PROMPT;

/**
 * Chain for querying SQL database that is a sequential chain.
 * <pre>
 * The chain is as follows:
 * 1. Based on the query, determine which tables to use.
 * 2. Based on those tables, call the normal SQL database chain.
 *
 * This is useful in cases where the number of tables in the database is large.
 * </pre>
 *
 * @author: HamaWhite
 */
public class SQLDatabaseSequentialChain extends Chain {

    private SQLDatabaseChain sqlChain;

    private LLMChain deciderChain;

    private String inputKey = "query";

    private String outputKey = "result";

    private boolean returnIntermediateSteps = false;

    /**
     * Load the necessary chains.
     */
    public static SQLDatabaseSequentialChain fromLLM(BaseLanguageModel llm,
            SQLDatabase database,
            BasePromptTemplate queryPrompt,
            BasePromptTemplate deciderPrompt,
            Map<String, Object> kwargs) {
        SQLDatabaseChain sqlChain = SQLDatabaseChain.fromLLM(llm, database, queryPrompt, kwargs);
        LLMChain deciderChain = new LLMChain(llm, deciderPrompt, "table_names");

        return null;
    }

    public static SQLDatabaseSequentialChain fromLLM(BaseLanguageModel llm, SQLDatabase database,
            Map<String, Object> kwargs) {
        return fromLLM(llm, database, PROMPT, DECIDER_PROMPT, kwargs);
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
