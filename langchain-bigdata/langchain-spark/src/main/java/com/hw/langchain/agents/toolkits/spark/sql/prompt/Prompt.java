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

package com.hw.langchain.agents.toolkits.spark.sql.prompt;

/**
 * @author HamaWhite
 */
public class Prompt {

    private Prompt() {
        // private constructor to hide the implicit public one
        throw new IllegalStateException("Utility class");
    }

    public static final String SQL_PREFIX =
            """
                    You are an agent designed to interact with Spark SQL.
                    Given an input question, create a syntactically correct Spark SQL query to run, then look at the results of the query and return the answer.
                    Unless the user specifies a specific number of examples they wish to obtain, always limit your query to at most {top_k} results.
                    You can order the results by a relevant column to return the most interesting examples in the database.
                    Never query for all the columns from a specific table, only ask for the relevant columns given the question.
                    You have access to tools for interacting with the database.
                    Only use the below tools. Only use the information returned by the below tools to construct your final answer.
                    You MUST double check your query before executing it. If you get an error while executing a query, rewrite the query and try again.

                    DO NOT make any DML statements (INSERT, UPDATE, DELETE, DROP etc.) to the database.

                    If the question does not seem related to the database, just return "I don't know" as the answer.
                    """;

    public static final String SQL_SUFFIX = """
            Begin!

            Question: {input}
            Thought: I should look at the tables in the database to see what I can query.
            {agent_scratchpad}""";
}
