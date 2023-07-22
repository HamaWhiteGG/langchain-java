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

package com.hw.langchain.tools.spark.sql.tool;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.utilities.spark.sql.SparkSql;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.tools.spark.sql.prompt.Prompt.QUERY_CHECKER;

/**
 * Use an LLM to check if a query is correct.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public class QueryCheckerTool extends BaseSparkSqlTool {

    private final BaseLanguageModel llm;

    private LLMChain llmChain;

    private static final String NAME = "query_checker_sql_db";
    private static final String DESCRIPTION = """
            Use this tool to double check if your query is correct before executing it.
            Always use this tool before executing a query with query_sql_db!
            """;

    public QueryCheckerTool(SparkSql db, BaseLanguageModel llm) {
        super(db, NAME, DESCRIPTION);
        this.llm = llm;

        initializeLlmChain();
    }

    private void initializeLlmChain() {
        llmChain = new LLMChain(llm, new PromptTemplate(List.of("query"), QUERY_CHECKER));
    }

    /**
     * Use the LLM to check the query.
     */
    @Override
    public Object innerRun(String query, Map<String, Object> kwargs) {
        return llmChain.predict(Map.of("query", query));
    }
}
