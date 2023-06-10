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

package com.hw.langchain.agents.mrkl.base;

import com.hw.langchain.agents.agent.Agent;
import com.hw.langchain.agents.agent.AgentOutputParser;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.tools.base.BaseTool;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hw.langchain.agents.mrkl.prompt.Prompt.*;

/**
 * Agent for the MRKL chain.
 *
 * @author HamaWhite
 */
public class ZeroShotAgent extends Agent {

    /**
     * Create prompt in the style of the zero shot agent.
     *
     * @param tools              List of tools the agent will have access to, used to format the prompt.
     * @param prefix             String to put before the list of tools.
     * @param suffix             String to put after the list of tools.
     * @param formatInstructions Format instructions for the prompt.
     * @param inputVariables     List of input variables the final prompt will expect.
     * @return A PromptTemplate with the template assembled from the pieces here.
     */
    public static PromptTemplate createPrompt(List<BaseTool> tools, String prefix, String suffix, String formatInstructions, List<String> inputVariables) {
        String toolStrings = tools.stream()
                .map(tool -> tool.getName() + ": " + tool.getDescription())
                .collect(Collectors.joining("\n"));

        String toolNames = String.join(", ", tools.stream().map(BaseTool::getName).toList());
        String formattedInstructions = formatInstructions.replace("{tool_names}", toolNames);
        String template = String.join("\n\n", prefix, toolStrings, formattedInstructions, suffix);

        if (inputVariables == null) {
            inputVariables = List.of("input", "agent_scratchpad");
        }
        return new PromptTemplate(inputVariables, template);
    }

    /**
     * Construct an agent from an LLM and tools.
     */
    public static Agent fromLLMAndTools(BaseLanguageModel llm, List<BaseTool> tools, Map<String, Object> kwargs) {
        return fromLLMAndTools(llm, tools, null, PREFIX, SUFFIX, FORMAT_INSTRUCTIONS, null, kwargs);
    }

    public static Agent fromLLMAndTools(BaseLanguageModel llm, List<BaseTool> tools, AgentOutputParser outputParser,
                                        String prefix, String suffix, String formatInstructions, List<String> inputVariables, Map<String, Object> kwargs) {
        validateTools(tools);
        PromptTemplate prompt = createPrompt(tools, prefix, suffix, formatInstructions, inputVariables);
        Chain llmChain = new LLMChain(llm, prompt);


        return new ZeroShotAgent();
    }


    public static void validateTools(List<BaseTool> tools) {
        for (BaseTool tool : tools) {
            checkArgument(tool.getDescription() != null,
                    "Got a tool %s without a description. For this agent, a description must always be provided.",
                    tool.getName());
        }
    }

}
