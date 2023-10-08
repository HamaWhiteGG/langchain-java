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
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.sql.database.SQLDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hw.langchain.chains.sql.database.prompt.Prompt.SQL_PROMPTS;

/**
 * Chain for interacting with SQL Database.
 *
 * @author HamaWhite
 */
@Data
public class SQLDatabaseChain extends Chain {

    private static final Logger LOG = LoggerFactory.getLogger(SQLDatabaseChain.class);

    private LLMChain llmChain;

    /**
     * SQL Database to connect to.
     */
    private SQLDatabase database;

    /**
     * Number of results to return from the query
     */
    private int topK = 5;

    private String inputKey = "query";

    private String outputKey = "result";

    /**
     * Whether or not to return the intermediate steps along with the final answer.
     */
    private boolean returnIntermediateSteps;

    /**
     * Whether or not to return the result of querying the SQL table directly.
     */
    private boolean returnDirect;

    /**
     * Whether or not the query checker tool should be used to attempt to fix the initial SQL from the LLM.
     */
    private boolean useQueryChecker;

    /**
     * The prompt template that should be used by the query checker
     */
    private BasePromptTemplate queryCheckerPrompt;

    public SQLDatabaseChain(LLMChain llmChain, SQLDatabase database) {
        this.llmChain = llmChain;
        this.database = database;
    }

    public static SQLDatabaseChain fromLLM(BaseLanguageModel llm, SQLDatabase database) {
        BasePromptTemplate prompt = SQL_PROMPTS.get(database.getDialect());
        return fromLLM(llm, database, prompt);
    }

    public static SQLDatabaseChain fromLLM(BaseLanguageModel llm, SQLDatabase database, BasePromptTemplate prompt) {
        LLMChain llmChain = new LLMChain(llm, prompt);
        return new SQLDatabaseChain(llmChain, database);
    }

    @Override
    public String chainType() {
        return "sql_database_chain";
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
        String inputText = inputs.get(this.inputKey) + "\nSQLQuery:";
        // If not present, then defaults to null which is all tables.
        List<String> tableNamesToUse = (List<String>) inputs.get("table_names_to_use");
        String tableInfo = database.getTableInfo(tableNamesToUse);

        Map<String, Object> llmInputs = new HashMap<>();
        llmInputs.put("input", inputText);
        llmInputs.put("top_k", topK);
        llmInputs.put("dialect", database.getDialect());
        llmInputs.put("table_info", tableInfo);
        llmInputs.put("stop", ListUtil.of("\nSQLResult:"));

        String sqlCmd = llmChain.predict(llmInputs);
        LOG.info("SQL command:\n {}", sqlCmd);

        String result = database.run(sqlCmd, false);
        LOG.info("SQLResult: \n{}", result);

        /*
         * If return direct, we just set the final result equal to the result of the sql query result, otherwise try to
         * get a human readable final answer
         */
        String finalResult;
        if (returnDirect) {
            finalResult = result;
        } else {
            inputText += String.format("%s\nSQLResult: %s\nAnswer:", sqlCmd, result);
            llmInputs.put("input", inputText);
            finalResult = llmChain.predict(llmInputs).trim();
        }
        LOG.info("Final Result: \n{}", finalResult);
        return MapUtil.of(outputKey, finalResult);
    }
}
