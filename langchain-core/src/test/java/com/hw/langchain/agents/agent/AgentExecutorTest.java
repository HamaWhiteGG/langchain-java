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

package com.hw.langchain.agents.agent;

import com.hw.langchain.agents.agent.types.AgentType;
import com.hw.langchain.llms.openai.OpenAI;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.hw.langchain.agents.initialize.Initialize.initializeAgent;
import static com.hw.langchain.agents.load.tools.LoadTools.loadTools;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class AgentExecutorTest {

    @Test
    void testAgents() {
        // First, let's load the language model we're going to use to control the agent.
        var llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        // Next, let's load some tools to use. Note that the `llm-math` tool uses an LLM, so we need to pass that in.
        var tools = loadTools(List.of("serpapi", "llm-math"), llm);

        // Finally, let's initialize an agent with the tools, the language model, and the type of agent we want to use.
        var agent = initializeAgent(tools, llm, AgentType.ZERO_SHOT_REACT_DESCRIPTION);

        // Now let's test it out!
        agent.run(
                "What was the high temperature in SF yesterday in Fahrenheit? What is that number raised to the .023 power?");
    }
}
