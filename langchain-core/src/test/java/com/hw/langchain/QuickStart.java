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

import com.hw.langchain.agents.agent.types.AgentType;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.chains.sql.database.base.SQLDatabaseChain;
import com.hw.langchain.chains.sql.database.base.SQLDatabaseSequentialChain;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.sql.database.SQLDatabase;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.agents.initialize.Initialize.initializeAgent;
import static com.hw.langchain.agents.load.tools.LoadTools.loadTools;

/**
 * <a href="https://python.langchain.com/en/latest/getting_started/getting_started.html#">LangChain Quickstart Guide</a>
 * <p>
 * QuickStart
 *
 * @author HamaWhite
 */
@UtilityClass
public class QuickStart {

    private void llm() {
        var llm = OpenAI.builder()
                .temperature(0.9f)
                .build()
                .init();

        String text = "What would be a good company name for a company that makes colorful socks?";
        System.out.println(llm.call(text));
    }

    private void promptTemplate() {
        var prompt = new PromptTemplate(List.of("product"),
                "What is a good name for a company that makes {product}?");

        System.out.println(prompt.format(Map.of("product", "colorful socks")));
    }

    private void llmChain() {
        var llm = OpenAI.builder()
                .temperature(0.9f)
                .build()
                .init();

        var prompt = new PromptTemplate(List.of("product"),
                "What is a good name for a company that makes {product}?");

        var chain = new LLMChain(llm, prompt);
        System.out.println(chain.run("colorful socks"));
    }

    private void sqlChain() {
        var database = SQLDatabase.fromUri("jdbc:mysql://127.0.0.1:3306/demo", "root", "123456");

        var llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        var chain = SQLDatabaseChain.fromLLM(llm, database);
        System.out.println(chain.run("How many students are there?"));
    }

    private void sqlSequentialChain() {
        var database = SQLDatabase.fromUri("jdbc:mysql://127.0.0.1:3306/demo", "root", "123456");

        var llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        var chain = SQLDatabaseSequentialChain.fromLLM(llm, database);
        System.out.println(chain.run("How many students are there?"));
    }

    private void agent() {
        var llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        // load some tools to use.
        var tools = loadTools(List.of("serpapi", "llm-math"), llm);

        // initialize an agent with the tools, the language model, and the type of agent
        var agent = initializeAgent(tools, llm, AgentType.ZERO_SHOT_REACT_DESCRIPTION);

        // let's test it out!
        String text =
                "What was the high temperature in SF yesterday in Fahrenheit? What is that number raised to the .023 power?";
        System.out.println(agent.run(text));
    }

    public static void main(String[] args) {
        llm();
        promptTemplate();
        llmChain();
        sqlChain();
        sqlSequentialChain();
        agent();
    }
}
