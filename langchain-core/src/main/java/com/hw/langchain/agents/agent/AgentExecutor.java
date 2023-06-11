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

import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.tools.base.BaseTool;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Consists of an agent using tools.
 *
 * @author HamaWhite
 */
public class AgentExecutor extends Chain {

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

    @Override
    public List<String> inputKeys() {
        return null;
    }

    @Override
    public List<String> outputKeys() {
        return null;
    }

    /**
     * Run text through and get agent response.
     */
    @Override
    public Map<String, String> _call(Map<String, ?> inputs) {
        // Construct a mapping of tool name to tool for easy lookup
        Map<String, BaseTool> nameToToolMap = tools.stream().collect(Collectors.toMap(BaseTool::getName, tool -> tool));

        // Let's start tracking the number of iterations and time elapsed
        int iterations = 0;
        double timeElapsed = 0.0;
        Instant startTime = Instant.now();

        // We now enter the agent loop (until it returns something).
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
