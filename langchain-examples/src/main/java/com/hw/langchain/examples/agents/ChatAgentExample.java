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

package com.hw.langchain.examples.agents;

import com.hw.langchain.agents.agent.types.AgentType;
import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.llms.openai.OpenAI;

import java.util.List;

import static com.hw.langchain.agents.initialize.Initialize.initializeAgent;
import static com.hw.langchain.agents.load.tools.LoadTools.loadTools;

/**
 * @author HamaWhite
 */
public class ChatAgentExample {

    public static void main(String[] args) {
        var chat = ChatOpenAI.builder().temperature(0).model("gpt-4").build().init();

        // Note that the 'llm-math' tool uses an LLM, so we need to pass that in.
        var llm = OpenAI.builder().temperature(0).build().init();
        var tools = loadTools(List.of("serpapi", "llm-math"), llm);

        // let's initialize an agent with the tools, the language model, and the type of agent we want to use.
        var agent = initializeAgent(tools, chat, AgentType.CHAT_ZERO_SHOT_REACT_DESCRIPTION);

        // var query = "Who is Olivia Wilde's boyfriend? What is his current age raised to the 0.23 power?";
        var query = "How many countries and regions participated in the 2023 Hangzhou Asian Games?" +
                "What is that number raised to the .023 power?";

        agent.run(query);
    }
}
