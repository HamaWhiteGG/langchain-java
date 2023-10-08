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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.agents.agent.Agent;
import com.hw.langchain.agents.agent.AgentOutputParser;
import com.hw.langchain.agents.mrkl.output.parser.MRKLOutputParser;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.tools.base.BaseTool;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hw.langchain.agents.mrkl.prompt.Prompt.*;
import static com.hw.langchain.prompts.utils.FormatUtils.formatTemplate;

/**
 * Agent for the MRKL chain.
 *
 * @author HamaWhite
 */
public class ZeroShotAgent extends Agent {

    public ZeroShotAgent(LLMChain llmChain, List<String> allowedTools) {
        this(llmChain, allowedTools, new MRKLOutputParser());
    }

    public ZeroShotAgent(LLMChain llmChain, List<String> allowedTools, AgentOutputParser outputParser) {
        super(llmChain, allowedTools, outputParser);
    }

    private static AgentOutputParser getDefaultOutputParser(Map<String, Object> kwargs) {
        return new MRKLOutputParser();
    }

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
    public static PromptTemplate createPrompt(List<BaseTool> tools, String prefix, String suffix,
            String formatInstructions, List<String> inputVariables) {
        String toolStrings =
                tools.stream().map(tool -> tool.getName() + ": " + tool.getDescription()).collect(Collectors.joining("\n"));
        String toolNames = tools.stream().map(BaseTool::getName).collect(Collectors.joining(", "));

        formatInstructions = formatTemplate(formatInstructions, MapUtil.of("tool_names", toolNames));
        String template = String.join("\n\n", prefix, toolStrings, formatInstructions, suffix);

        if (inputVariables == null) {
            inputVariables = ListUtil.of("input", "agent_scratchpad");
        }
        return new PromptTemplate(inputVariables, template);
    }

    /**
     * Construct an agent from an LLM and tools.
     */
    public static Agent fromLlmAndTools(BaseLanguageModel llm, List<BaseTool> tools, Map<String, Object> kwargs) {
        return fromLlmAndTools(llm, tools, null, PREFIX, SUFFIX, FORMAT_INSTRUCTIONS, null, kwargs);
    }

    /**
     * Construct an agent from an LLM and tools.
     */
    public static Agent fromLlmAndTools(BaseLanguageModel llm, List<BaseTool> tools, AgentOutputParser outputParser,
            String prefix, String suffix, String formatInstructions, List<String> inputVariables,
            Map<String, Object> kwargs) {
        validateTools(tools);
        PromptTemplate prompt = createPrompt(tools, prefix, suffix, formatInstructions, inputVariables);
        LLMChain llmChain = new LLMChain(llm, prompt);

        List<String> toolNames = tools.stream().map(BaseTool::getName).collect(Collectors.toList());
        outputParser = (outputParser != null) ? outputParser : getDefaultOutputParser(kwargs);

        return new ZeroShotAgent(llmChain, toolNames, outputParser);
    }

    public static void validateTools(List<BaseTool> tools) {
        for (BaseTool tool : tools) {
            checkArgument(tool.getDescription() != null,
                    "Got a tool %s without a description. For this agent, a description must always be provided.",
                    tool.getName());
        }
    }

    @Override
    public String observationPrefix() {
        return "Observation: ";
    }

    @Override
    public String llmPrefix() {
        return "Thought:";
    }

}
