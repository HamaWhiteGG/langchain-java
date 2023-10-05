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

package com.hw.langchain.examples.chains;

import com.hw.langchain.chains.sql.database.base.SQLDatabaseChain;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.sql.database.SQLDatabase;

import static com.hw.langchain.examples.utils.PrintUtils.println;

/**
 * This example demonstrates the use of the SQLDatabaseChain for answering questions over a SQL database.
 * For more usage examples, please refer to SQLDatabaseChainTest in langchain-core module.
 * <p>
 * Note: The database script is in scripts/mysql/schema.sql and scripts/mysql/data.sql.
 *
 * @author HamaWhite
 */
public class SqlChainExample {

    public static void main(String[] args) {
        var database = SQLDatabase.fromUri("jdbc:mysql://127.0.0.1:3306/demo", "root", "root@123456");

        var llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        var chain = SQLDatabaseChain.fromLLM(llm, database);
        var result = chain.run("How many students are there?");
        println(result);

        result = chain.run("Who got zero score? Show me her parent's contact information.");
        println(result);
    }
}
