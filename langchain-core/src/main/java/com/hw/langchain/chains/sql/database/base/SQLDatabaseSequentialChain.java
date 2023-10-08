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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.sql.database.SQLDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
 * @author HamaWhite
 */
public class SQLDatabaseSequentialChain extends Chain {

    private static final Logger LOG = LoggerFactory.getLogger(SQLDatabaseSequentialChain.class);

    private SQLDatabaseChain sqlChain;

    private LLMChain deciderChain;

    private String inputKey = "query";

    private String outputKey = "result";

    public SQLDatabaseSequentialChain(SQLDatabaseChain sqlChain, LLMChain deciderChain) {
        this.sqlChain = sqlChain;
        this.deciderChain = deciderChain;
    }

    /**
     * Load the necessary chains.
     */
    public static SQLDatabaseSequentialChain fromLLM(BaseLanguageModel llm,
            SQLDatabase database,
            BasePromptTemplate queryPrompt,
            BasePromptTemplate deciderPrompt) {
        SQLDatabaseChain sqlChain = SQLDatabaseChain.fromLLM(llm, database, queryPrompt);
        LLMChain deciderChain = new LLMChain(llm, deciderPrompt, "table_names");
        return new SQLDatabaseSequentialChain(sqlChain, deciderChain);
    }

    public static SQLDatabaseSequentialChain fromLLM(BaseLanguageModel llm, SQLDatabase database) {
        return fromLLM(llm, database, PROMPT, DECIDER_PROMPT);
    }

    @Override
    public String chainType() {
        return "sql_database_sequential_chain";
    }

    /**
     * Return the singular input key.
     */
    @Override
    public List<String> inputKeys() {
        return ListUtil.of(inputKey);
    }

    /**
     * Return the singular output key.
     */
    @Override
    public List<String> outputKeys() {
        return ListUtil.of(outputKey);
    }

    @Override
    protected Map<String, String> innerCall(Map<String, Object> inputs) {
        List<String> tableNameList = sqlChain.getDatabase().getUsableTableNames();
        String tableNames = String.join(", ", tableNameList);
        Map<String, Object> llmInputs = MapBuilder.create(new HashMap<String, Object>())
                .put("query", inputs.get(inputKey))
                .put("table_names", tableNames).map();

        List<String> lowerCasedTableNames = tableNameList.stream().map(String::toLowerCase).collect(Collectors.toList());

        List<String> tableNamesFromChain = deciderChain.predictAndParse(llmInputs);

        List<String> tableNamesToUse = new ArrayList<>();
        for (String name : tableNamesFromChain) {
            if (lowerCasedTableNames.contains(name.toLowerCase())) {
                tableNamesToUse.add(name);
            }
        }
        LOG.info("Table names to use: {}", tableNamesToUse);
        Map<String, Object> newInputs = MapBuilder.create(new HashMap<String, Object>())
                .put(sqlChain.getInputKey(), inputs.get(inputKey))
                .put("table_names_to_use", tableNamesToUse).map();
        return sqlChain.call(newInputs, true);
    }
}
