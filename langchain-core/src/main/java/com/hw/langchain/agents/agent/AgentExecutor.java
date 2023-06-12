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

import com.hw.langchain.agents.tools.InvalidTool;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.schema.AgentAction;
import com.hw.langchain.schema.AgentFinish;
import com.hw.langchain.schema.AgentResult;
import com.hw.langchain.schema.OutputParserException;
import com.hw.langchain.tools.base.BaseTool;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Consists of an agent using tools.
 *
 * @author HamaWhite
 */
public class AgentExecutor extends Chain {

    private static final Logger LOG = LoggerFactory.getLogger(AgentExecutor.class);

    private BaseSingleActionAgent agent;

    private List<BaseTool> tools;

    private boolean returnIntermediateSteps;

    private Integer maxIterations = 15;

    private Float maxExecutionTime;

    private String earlyStoppingMethod = "force";

    private Object handleParsingErrors = false;

    /**
     * Create from agent and tools.
     */
    public static AgentExecutor fromAgentAndTools(BaseSingleActionAgent agent, List<BaseTool> tools,
            Map<String, Object> kwargs) {
        return new AgentExecutor(agent, tools, kwargs);
    }

    private AgentExecutor(BaseSingleActionAgent agent, List<BaseTool> tools, Map<String, Object> kwargs) {
        this.agent = agent;
        this.tools = tools;
    }

    @Override
    public String chainType() {
        return null;
    }

    /**
     * Return the input keys.
     */
    @Override
    public List<String> inputKeys() {
        return agent.inputKeys();
    }

    /**
     * Return the singular output key.
     */
    @Override
    public List<String> outputKeys() {
        return agent.returnValues();
    }

    /**
     * Take a single step in the thought-action-observation loop.
     * Override this to take control of how the agent makes and acts on choices.
     *
     * @return AgentFinish or List[Pair[AgentAction, str]]
     */
    public Object takeNextStep(Map<String, BaseTool> nameToToolMap, Map<String, ?> inputs,
            List<Pair<AgentAction, String>> intermediateSteps) {
        AgentResult output = null;
        try {
            // Call the LLM to see what to do.
            output = agent.plan(intermediateSteps, inputs);
        } catch (OutputParserException e) {
            LOG.error("Error parsing output", e);
        }
        if (output instanceof AgentFinish) {
            return output;
        }
        List<AgentAction> actions;
        if (output instanceof AgentAction) {
            actions = List.of((AgentAction) output);
        } else {
            actions = (List<AgentAction>) output;
        }
        List<Pair<AgentAction, String>> result = new ArrayList<>();
        for (AgentAction agentAction : actions) {
            String observation;
            if (nameToToolMap.containsKey(agentAction.getTool())) {
                BaseTool tool = nameToToolMap.get(agentAction.getTool());
                boolean returnDirect = tool.isReturnDirect();
                Map<String, Object> toolRunKwargs = agent.toolRunLoggingKwargs();
                if (returnDirect) {
                    toolRunKwargs.put("llm_prefix", "");
                }
                // We then call the tool on the tool input to get an observation
                observation = tool.run(agentAction.getToolInput(), toolRunKwargs).toString();
            } else {
                Map<String, Object> toolRunKwargs = agent.toolRunLoggingKwargs();
                observation = new InvalidTool().run(agentAction.getTool(), toolRunKwargs).toString();
            }
            result.add(Pair.of(agentAction, observation));
        }
        return result;
    }

    private String runTool(AgentAction agentAction, Map<String, BaseTool> nameToToolMap) {
        if (nameToToolMap.containsKey(agentAction.getTool())) {
            BaseTool tool = nameToToolMap.get(agentAction.getTool());
            Map<String, Object> toolRunKwargs = agent.toolRunLoggingKwargs();
            if (tool.isReturnDirect()) {
                toolRunKwargs.put("llm_prefix", "");
            }
            // We then call the tool on the tool input to get an observation
        }

        return null;
    }

    /**
     * Run text through and get agent response.
     */
    @Override
    public Map<String, String> _call(Map<String, ?> inputs) {
        // Construct a mapping of tool name to tool for easy lookup
        Map<String, BaseTool> nameToToolMap = tools.stream().collect(Collectors.toMap(BaseTool::getName, tool -> tool));

        List<Pair<AgentAction, String>> intermediateSteps = new ArrayList<>();
        // Let's start tracking the number of iterations and time elapsed
        int iterations = 0;
        double timeElapsed = 0.0;
        Instant startTime = Instant.now();

        // We now enter the agent loop (until it returns something).
        while (shouldContinue(iterations, timeElapsed)) {
            Object nextStepOutput = takeNextStep(nameToToolMap, inputs, intermediateSteps);

        }
        return null;
    }

    private boolean shouldContinue(int iterations, double timeElapsed) {
        if (maxIterations != null && iterations >= maxIterations) {
            return false;
        }
        if (maxExecutionTime != null && timeElapsed >= maxExecutionTime) {
            return false;
        }

        return true;
    }
}
