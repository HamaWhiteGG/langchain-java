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

import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.schema.AgentAction;
import com.hw.langchain.schema.AgentResult;
import com.hw.langchain.tools.base.BaseTool;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for calling the language model and deciding the action.
 * <p>
 * This is driven by an LLMChain. The prompt in the LLMChain MUST include
 * a variable called "agent_scratchpad" where the agent can put its
 * intermediary work.
 *
 * @author HamaWhite
 */
public abstract class Agent extends BaseSingleActionAgent {

    private LLMChain llmChain;

    private List<String> allowedTools;

    private AgentOutputParser outputParser;

    public Agent(LLMChain llmChain, List<String> allowedTools, AgentOutputParser outputParser) {
        this.llmChain = llmChain;
        this.outputParser = outputParser;
        this.allowedTools = allowedTools;
    }

    public List<String> stop() {
        return List.of(
                "\n" + observationPrefix().trim(),
                "\n\t" + observationPrefix().trim());
    }

    /**
     * Construct the scratchpad that lets the agent continue its thought process.
     *
     * @param intermediateSteps Steps the LLM has taken to date, along with observations
     * @return str or List[BaseMessage]
     */
    public String constructScratchpad(List<Pair<AgentAction, String>> intermediateSteps) {
        StringBuilder thoughts = new StringBuilder();
        for (Pair<AgentAction, String> step : intermediateSteps) {
            thoughts.append(step.getKey().getLog());
            thoughts.append("\n").append(observationPrefix()).append(step.getValue());
            thoughts.append("\n").append(llmPrefix());
        }
        return thoughts.toString();
    }

    /**
     * Validate that appropriate tools are passed in.
     */
    public static void validateTools(List<BaseTool> tools) {
    }

    @Override
    public List<String> inputKeys() {
        return llmChain.inputKeys().stream()
                .filter(key -> !key.equals("agent_scratchpad"))
                .toList();
    }

    /**
     * Prefix to append the observation with.
     */
    public abstract String observationPrefix();

    /**
     * Prefix to append the LLM call with.
     */
    public abstract String llmPrefix();

    @Override
    public AgentResult plan(List<Pair<AgentAction, String>> intermediateSteps, Map<String, ?> kwargs) {
        var fullInputs = getFullInputs(intermediateSteps, kwargs);
        String fullOutput = llmChain.predict(fullInputs);
        return outputParser.parse(fullOutput);
    }

    /**
     * Create the full inputs for the LLMChain from intermediate steps.
     */
    public Map<String, ?> getFullInputs(List<Pair<AgentAction, String>> intermediateSteps, Map<String, ?> kwargs) {
        String thoughts = constructScratchpad(intermediateSteps);
        var newInputs = Map.of("agent_scratchpad", thoughts, "stop", stop());
        Map<String, Object> fullInputs = new HashMap<>(kwargs);
        fullInputs.putAll(newInputs);
        return fullInputs;
    }

    public Map<String, Object> toolRunLoggingKwargs() {
        Map<String, Object> map = new HashMap<>();
        map.put("llm_prefix", llmPrefix());
        map.put("observation_prefix", observationPrefix());
        return map;
    }

}
