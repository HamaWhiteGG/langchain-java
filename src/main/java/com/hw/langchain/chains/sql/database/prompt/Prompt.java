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

package com.hw.langchain.chains.sql.database.prompt;

import com.hw.langchain.output.parsers.list.CommaSeparatedListOutputParser;
import com.hw.langchain.prompts.prompt.PromptTemplate;

import java.util.List;
import java.util.Map;

/**
 * @description: Prompt
 * @author: HamaWhite
 */
public class Prompt {

    private static String PROMPT_SUFFIX = """
            Only use the following tables:
            {table_info}

            Question: {input}
            """;

    private static String _DEFAULT_TEMPLATE =
            """
                    Given an input question, first create a syntactically correct {dialect} query to run, then look at the results of the query and return the answer. Unless the user specifies in his question a specific number of examples he wishes to obtain, always limit your query to at most {top_k} results. You can order the results by a relevant column to return the most interesting examples in the database.

                    Never query for all the columns from a specific table, only ask for a the few relevant columns given the question.

                    Pay attention to use only the column names that you can see in the schema description. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.

                    Use the following format:

                    Question: Question here
                    SQLQuery: SQL Query to run
                    SQLResult: Result of the SQLQuery
                    Answer: Final answer here

                    """;

    public static PromptTemplate PROMPT = new PromptTemplate(List.of("input", "table_info", "dialect", "top_k"),
            _DEFAULT_TEMPLATE + PROMPT_SUFFIX);

    private static String _DECIDER_TEMPLATE =
            """
                    Given the below input question and list of potential tables, output a comma separated list of the table names that may be necessary to answer this question.

                    Question: {query}

                    Table Names: {table_names}

                    Relevant Table Names:
                    """;

    public static PromptTemplate DECIDER_PROMPT = new PromptTemplate(List.of("query", "table_names"),
            _DECIDER_TEMPLATE,
            new CommaSeparatedListOutputParser());

    private static String _mysql_prompt =
            """
                    You are a MySQL expert. Given an input question, first create a syntactically correct MySQL query to run, then look at the results of the query and return the answer to the input question.
                    Unless the user specifies in the question a specific number of examples to obtain, query for at most {top_k} results using the LIMIT clause as per MySQL. You can order the results to return the most informative data in the database.
                    Never query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in backticks (`) to denote them as delimited identifiers.
                    Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.
                    Pay attention to use CURDATE() function to get the current date, if the question involves "today".

                    Use the following format:

                    Question: Question here
                    SQLQuery: SQL Query to run
                    SQLResult: Result of the SQLQuery
                    Answer: Final answer here

                    """;

    public static PromptTemplate MYSQL_PROMPT =
            new PromptTemplate(List.of("input", "table_info", "top_k"), _mysql_prompt + PROMPT_SUFFIX);

    private static String _mariadb_prompt =
            """
                    You are a MariaDB expert. Given an input question, first create a syntactically correct MariaDB query to run, then look at the results of the query and return the answer to the input question.
                    Unless the user specifies in the question a specific number of examples to obtain, query for at most {top_k} results using the LIMIT clause as per MariaDB. You can order the results to return the most informative data in the database.
                    Never query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in backticks (`) to denote them as delimited identifiers.
                    Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.
                    Pay attention to use CURDATE() function to get the current date, if the question involves "today".

                    Use the following format:

                    Question: Question here
                    SQLQuery: SQL Query to run
                    SQLResult: Result of the SQLQuery
                    Answer: Final answer here

                    """;

    public static PromptTemplate MARIADB_PROMPT =
            new PromptTemplate(List.of("input", "table_info", "top_k"), _mariadb_prompt + PROMPT_SUFFIX);

    private static String _oracle_prompt =
            """
                    You are an Oracle SQL expert. Given an input question, first create a syntactically correct Oracle SQL query to run, then look at the results of the query and return the answer to the input question.
                    Unless the user specifies in the question a specific number of examples to obtain, query for at most {top_k} results using the FETCH FIRST n ROWS ONLY clause as per Oracle SQL. You can order the results to return the most informative data in the database.
                    Never query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in double quotes (") to denote them as delimited identifiers.
                    Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.
                    Pay attention to use TRUNC(SYSDATE) function to get the current date, if the question involves "today".

                    Use the following format:

                    Question: Question here
                    SQLQuery: SQL Query to run
                    SQLResult: Result of the SQLQuery
                    Answer: Final answer here

                    """;

    public static PromptTemplate ORACLE_PROMPT =
            new PromptTemplate(List.of("input", "table_info", "top_k"), _oracle_prompt + PROMPT_SUFFIX);

    private static String _postgres_prompt =
            """
                    You are a PostgreSQL expert. Given an input question, first create a syntactically correct PostgreSQL query to run, then look at the results of the query and return the answer to the input question.
                    Unless the user specifies in the question a specific number of examples to obtain, query for at most {top_k} results using the LIMIT clause as per PostgreSQL. You can order the results to return the most informative data in the database.
                    Never query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in double quotes (") to denote them as delimited identifiers.
                    Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.
                    Pay attention to use CURRENT_DATE function to get the current date, if the question involves "today".

                    Use the following format:

                    Question: Question here
                    SQLQuery: SQL Query to run
                    SQLResult: Result of the SQLQuery
                    Answer: Final answer here

                    """;

    public static PromptTemplate POSTGRES_PROMPT =
            new PromptTemplate(List.of("input", "table_info", "top_k"), _postgres_prompt + PROMPT_SUFFIX);

    private static String _sqlite_prompt =
            """
                    You are a SQLite expert. Given an input question, first create a syntactically correct SQLite query to run, then look at the results of the query and return the answer to the input question.
                    Unless the user specifies in the question a specific number of examples to obtain, query for at most {top_k} results using the LIMIT clause as per SQLite. You can order the results to return the most informative data in the database.
                    Never query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in double quotes (") to denote them as delimited identifiers.
                    Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.
                    Pay attention to use date('now') function to get the current date, if the question involves "today".

                    Use the following format:

                    Question: Question here
                    SQLQuery: SQL Query to run
                    SQLResult: Result of the SQLQuery
                    Answer: Final answer here

                    """;

    public static PromptTemplate SQLITE_PROMPT =
            new PromptTemplate(List.of("input", "table_info", "top_k"), _sqlite_prompt + PROMPT_SUFFIX);

    public static final Map<String, PromptTemplate> SQL_PROMPTS = Map.of(
            "mysql", MYSQL_PROMPT,
            "mariadb", MARIADB_PROMPT,
            "oracle", ORACLE_PROMPT,
            "postgresql", POSTGRES_PROMPT,
            "sqlite", SQLITE_PROMPT);
}
