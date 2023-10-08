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

import com.google.common.collect.Lists;
import com.hw.langchain.agents.tools.InvalidTool;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.schema.AgentAction;
import com.hw.langchain.schema.AgentFinish;
import com.hw.langchain.schema.AgentResult;
import com.hw.langchain.tools.base.BaseTool;

import lombok.var;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Consists of an agent using tools.
 *
 * @author HamaWhite
 */
@Builder
public class AgentExecutor extends Chain {

    private static final Logger LOG = LoggerFactory.getLogger(AgentExecutor.class);

    private BaseSingleActionAgent agent;

    private List<BaseTool> tools;

    private boolean returnIntermediateSteps;

    @Builder.Default
    private Integer maxIterations = 15;

    private Float maxExecutionTime;

    @Builder.Default
    private String earlyStoppingMethod = "force";

    @Builder.Default
    private Object handleParsingErrors = false;

    /**
     * Create from agent and tools.
     */
    public static AgentExecutor fromAgentAndTools(BaseSingleActionAgent agent, List<BaseTool> tools) {
        return AgentExecutor.builder().agent(agent).tools(tools).build();
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

    public Map<String, String> processOutput(AgentFinish output, List<Pair<AgentAction, String>> intermediateSteps) {
        Map<String, String> finalOutput = output.getReturnValues();
        if (returnIntermediateSteps) {
            finalOutput.put("intermediate_steps", intermediateSteps.toString());
        }
        return finalOutput;
    }

    /**
     * Take a single step in the thought-action-observation loop.
     * Override this to take control of how the agent makes and acts on choices.
     *
     * @return AgentFinish or List<Pair<AgentAction, String>>
     */
    public Object takeNextStep(Map<String, BaseTool> nameToToolMap, Map<String, Object> inputs,
            List<Pair<AgentAction, String>> intermediateSteps) {
        // Call the LLM to see what to do.
        AgentResult output = agent.plan(intermediateSteps, inputs);
        if (output instanceof AgentFinish) {
            return output;
        } else if (output instanceof AgentAction) {
            AgentAction agentAction = (AgentAction)output;
            String observation;
            if (nameToToolMap.containsKey(agentAction.getTool())) {
                BaseTool tool = nameToToolMap.get(agentAction.getTool());
                boolean returnDirect = tool.isReturnDirect();
                var toolRunKwargs = agent.toolRunLoggingKwargs();
                // add input args for tools
                toolRunKwargs.putAll(inputs);
                if (returnDirect) {
                    toolRunKwargs.put("llm_prefix", "");
                }
                // We then call the tool on the tool input to get an observation
                observation = tool.run(agentAction.getToolInput(), toolRunKwargs).toString();
                LOG.info("Observation: {}", observation);
            } else {
                Map<String, Object> toolRunKwargs = agent.toolRunLoggingKwargs();
                observation = new InvalidTool().run(agentAction.getTool(), toolRunKwargs).toString();
            }
            return Lists.newArrayList(Pair.of(agentAction, observation));
        }
        return null;
    }

    /**
     * Run text through and get agent response.
     */
    @Override
    protected Map<String, String> innerCall(Map<String, Object> inputs) {
        // Construct a mapping of tool name to tool for easy lookup
        Map<String, BaseTool> nameToToolMap = tools.stream().collect(Collectors.toMap(BaseTool::getName, tool -> tool));

        List<Pair<AgentAction, String>> intermediateSteps = new ArrayList<>();
        // Let's start tracking the number of iterations and time elapsed
        int iterations = 0;
        double timeElapsed = 0.0;
        long startTime = System.currentTimeMillis();

        // We now enter the agent loop (until it returns something).
        while (shouldContinue(iterations, timeElapsed)) {
            var nextStepOutput = takeNextStep(nameToToolMap, inputs, intermediateSteps);
            LOG.debug("NextStepOutput: {}", nextStepOutput);
            if (nextStepOutput instanceof AgentFinish) {
                AgentFinish agentFinish = (AgentFinish) nextStepOutput;
                return processOutput(agentFinish, intermediateSteps);
            }

            @SuppressWarnings("unchecked")
            List<Pair<AgentAction, String>> nextOutput = (List<Pair<AgentAction, String>>) nextStepOutput;
            intermediateSteps.addAll(nextOutput);

            if (nextOutput.size() == 1) {
                Pair<AgentAction, String> nextStepAction = nextOutput.get(0);
                // See if tool should return directly
                AgentFinish toolReturn = getToolReturn(nextStepAction);
                if (toolReturn != null) {
                    return processOutput(toolReturn, intermediateSteps);
                }
            }
            iterations++;
            // Convert to seconds
            timeElapsed = (System.currentTimeMillis() - startTime) / 1000.0;
        }
        AgentFinish output = agent.returnStoppedResponse(earlyStoppingMethod, intermediateSteps, inputs);
        return processOutput(output, intermediateSteps);
    }

    private boolean shouldContinue(int iterations, double timeElapsed) {
        if (maxIterations != null && iterations >= maxIterations) {
            return false;
        }
        return maxExecutionTime == null || timeElapsed < maxExecutionTime;
    }

    /**
     * Check if the tool is a returning tool.
     */
    public AgentFinish getToolReturn(Pair<AgentAction, String> nextStepOutput) {
        AgentAction agentAction = nextStepOutput.getKey();
        String observation = nextStepOutput.getValue();

        Map<String, BaseTool> nameToToolMap = tools.stream().collect(Collectors.toMap(BaseTool::getName, tool -> tool));
        // Invalid tools won't be in the map, so we return False.
        if (nameToToolMap.containsKey(agentAction.getTool())) {
            BaseTool tool = nameToToolMap.get(agentAction.getTool());
            if (tool.isReturnDirect()) {
                Map<String, String> returnValues = new HashMap<>();
                returnValues.put(agent.returnValues().get(0), observation);
                return new AgentFinish(returnValues, "");
            }
        }
        return null;
    }
}
