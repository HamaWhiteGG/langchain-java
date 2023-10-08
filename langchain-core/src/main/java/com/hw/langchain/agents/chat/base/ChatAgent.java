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

package com.hw.langchain.agents.chat.base;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.hw.langchain.agents.agent.Agent;
import com.hw.langchain.agents.agent.AgentOutputParser;
import com.hw.langchain.agents.chat.output.parser.ChatOutputParser;
import com.hw.langchain.agents.initialize.Initialize;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.prompts.chat.BaseMessagePromptTemplate;
import com.hw.langchain.prompts.chat.ChatPromptTemplate;
import com.hw.langchain.prompts.chat.HumanMessagePromptTemplate;
import com.hw.langchain.prompts.chat.SystemMessagePromptTemplate;
import com.hw.langchain.schema.AgentAction;
import com.hw.langchain.tools.base.BaseTool;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hw.langchain.agents.chat.prompt.Prompt.*;
import static com.hw.langchain.agents.utils.Utils.validateToolsSingleInput;
import static com.hw.langchain.prompts.utils.FormatUtils.formatTemplate;

/**
 * @author HamaWhite
 */
public class ChatAgent extends Agent {

    public ChatAgent(LLMChain llmChain, List<String> allowedTools, AgentOutputParser outputParser) {
        super(llmChain, allowedTools, outputParser);
    }

    @Override
    public String observationPrefix() {
        return "Observation: ";
    }

    @Override
    public String llmPrefix() {
        return "Thought:";
    }

    @Override
    public String constructScratchpad(List<Pair<AgentAction, String>> intermediateSteps) {
        Object agentScratchpad = super.constructScratchpad(intermediateSteps);
        if (!(agentScratchpad instanceof String)) {
            throw new IllegalArgumentException("agent_scratchpad should be of type String.");
        }
        String scratchpad = agentScratchpad.toString();
        if (!scratchpad.isEmpty()) {
            return "This was your previous work (but I haven't seen any of it! I only see what "
                    + "you return as the final answer):\n" + scratchpad;
        } else {
            return scratchpad;
        }
    }

    private static AgentOutputParser getDefaultOutputParser(Map<String, Object> kwargs) {
        return new ChatOutputParser();
    }

    public static void validateTools(List<BaseTool> tools) {
        validateToolsSingleInput(ChatAgent.class.getSimpleName(), tools);
    }

    @Override
    public List<String> stop() {
        return Lists.newArrayList("Observation:");
    }

    public static BasePromptTemplate createPrompt(List<BaseTool> tools, String systemMessagePrefix,
            String systemMessageSuffix, String humanMessage, String formatInstructions, List<String> inputVariables) {
        String toolNames = tools.stream().map(BaseTool::getName).collect(Collectors.joining(", "));
        String toolStrings =
                tools.stream().map(tool -> tool.getName() + ": " + tool.getDescription()).collect(Collectors.joining("\n"));

        formatInstructions = formatTemplate(formatInstructions, MapUtil.of("tool_names", toolNames));
        String template =
                String.join("\n\n", systemMessagePrefix, toolStrings, formatInstructions, systemMessageSuffix);

        List<BaseMessagePromptTemplate> messages = Lists.newArrayList(
                SystemMessagePromptTemplate.fromTemplate(template),
                HumanMessagePromptTemplate.fromTemplate(humanMessage));
        if (inputVariables == null) {
            inputVariables = Lists.newArrayList("input", "agent_scratchpad");
        }
        return new ChatPromptTemplate(inputVariables, messages);
    }

    /**
     * Construct an agent from an LLM and tools.
     * This method will be called by the {@link Initialize#initializeAgent} using MethodUtils.invokeStaticMethod.
     */
    public static Agent fromLlmAndTools(BaseLanguageModel llm, List<BaseTool> tools, Map<String, Object> kwargs) {
        return fromLlmAndTools(llm, tools, null, SYSTEM_MESSAGE_PREFIX, SYSTEM_MESSAGE_SUFFIX, HUMAN_MESSAGE,
                FORMAT_INSTRUCTIONS, null, kwargs);
    }

    /**
     * Construct an agent from an LLM and tools.
     */
    public static Agent fromLlmAndTools(BaseLanguageModel llm, List<BaseTool> tools, AgentOutputParser outputParser,
            String systemMessagePrefix, String systemMessageSuffix, String humanMessage, String formatInstructions,
            List<String> inputVariables, Map<String, Object> kwargs) {
        validateTools(tools);

        BasePromptTemplate prompt = createPrompt(tools, systemMessagePrefix, systemMessageSuffix, humanMessage, formatInstructions,
                inputVariables);
        LLMChain llmChain = new LLMChain(llm, prompt);

        List<String> toolNames = tools.stream().map(BaseTool::getName).collect(Collectors.toList());
        outputParser = (outputParser != null) ? outputParser : getDefaultOutputParser(kwargs);

        return new ChatAgent(llmChain, toolNames, outputParser);
    }
}
