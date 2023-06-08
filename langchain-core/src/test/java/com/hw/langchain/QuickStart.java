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

package com.hw.langchain;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.chains.sql.database.base.SQLDatabaseChain;
import com.hw.langchain.chains.sql.database.base.SQLDatabaseSequentialChain;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.sql.database.SQLDatabase;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

/**
 * <a href="https://python.langchain.com/en/latest/getting_started/getting_started.html#">LangChain Quickstart Guide</a>
 *
 * QuickStart
 * @author HamaWhite
 */
@UtilityClass
public class QuickStart {

    private void llm() {
        OpenAI llm = OpenAI.builder()
                .temperature(0.9f)
                .build()
                .init();

        String text = "What would be a good company name for a company that makes colorful socks?";
        System.out.println(llm.call(text));
    }

    private void promptTemplate() {
        PromptTemplate prompt = new PromptTemplate(List.of("product"),
                "What is a good name for a company that makes {product}?");

        System.out.println(prompt.format(Map.of("product", "colorful socks")));
    }

    private void llmChain() {
        OpenAI llm = OpenAI.builder()
                .temperature(0.9f)
                .build()
                .init();

        PromptTemplate prompt = new PromptTemplate(List.of("product"),
                "What is a good name for a company that makes {product}?");

        Chain chain = new LLMChain(llm, prompt);
        System.out.println(chain.run("colorful socks"));
    }

    private void sqlChain() {
        SQLDatabase database = SQLDatabase.fromUri("jdbc:mysql://127.0.0.1:3306/demo", "root", "123456");

        BaseLanguageModel llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        Chain chain = SQLDatabaseChain.fromLLM(llm, database);
        System.out.println(chain.run("How many students are there?"));
    }

    private void sqlSequentialChain() {
        SQLDatabase database = SQLDatabase.fromUri("jdbc:mysql://127.0.0.1:3306/demo", "root", "123456");

        BaseLanguageModel llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        Chain chain = SQLDatabaseSequentialChain.fromLLM(llm, database);
        System.out.println(chain.run("How many students are there?"));
    }

    public static void main(String[] args) {
        llm();
        promptTemplate();
        llmChain();
        sqlChain();
        sqlSequentialChain();
    }
}
