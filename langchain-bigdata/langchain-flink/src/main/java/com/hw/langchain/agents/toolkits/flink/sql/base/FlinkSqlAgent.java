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

package com.hw.langchain.agents.toolkits.flink.sql.base;

import com.hw.langchain.agents.agent.Agent;
import com.hw.langchain.agents.agent.AgentExecutor;
import com.hw.langchain.agents.mrkl.base.ZeroShotAgent;
import com.hw.langchain.agents.toolkits.flink.sql.toolkit.FlinkSqlToolkit;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.tools.base.BaseTool;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.agents.mrkl.prompt.Prompt.FORMAT_INSTRUCTIONS;
import static com.hw.langchain.agents.toolkits.flink.sql.prompt.Prompt.SQL_PREFIX;
import static com.hw.langchain.agents.toolkits.flink.sql.prompt.Prompt.SQL_SUFFIX;
import static com.hw.langchain.prompts.utils.FormatUtils.formatTemplate;

/**
 * @author HamaWhite
 */
public class FlinkSqlAgent {

    private FlinkSqlAgent() {
        // private constructor to hide the implicit public one
        throw new IllegalStateException("Utility class");
    }

    /**
     * Construct a Flink SQL agent from an LLM and tools.
     */
    public static AgentExecutor createFlinkSqlAgent(BaseLanguageModel llm, FlinkSqlToolkit toolkit) {
        return createFlinkSqlAgent(llm, toolkit, SQL_PREFIX, SQL_SUFFIX, FORMAT_INSTRUCTIONS, null, 10, 15, null,
                "force");
    }

    /**
     * Construct a Flink SQL agent from an LLM and tools.
     */
    @SuppressWarnings("all")
    public static AgentExecutor createFlinkSqlAgent(
            BaseLanguageModel llm,
            FlinkSqlToolkit toolkit,
            String prefix,
            String suffix,
            String formatInstructions,
            List<String> inputVariables,
            int topK,
            Integer maxIterations,
            Float maxExecutionTime,
            String earlyStoppingMethod) {
        List<BaseTool> tools = toolkit.getTools();
        prefix = formatTemplate(prefix, Map.of("top_k", topK));

        PromptTemplate prompt = ZeroShotAgent.createPrompt(tools, prefix, suffix, formatInstructions, inputVariables);
        LLMChain llmChain = new LLMChain(llm, prompt);

        List<String> toolNames = tools.stream().map(BaseTool::getName).toList();
        Agent agent = new ZeroShotAgent(llmChain, toolNames);

        return AgentExecutor.builder()
                .agent(agent)
                .tools(tools)
                .maxIterations(maxIterations)
                .maxExecutionTime(maxExecutionTime)
                .earlyStoppingMethod(earlyStoppingMethod)
                .build();
    }
}
