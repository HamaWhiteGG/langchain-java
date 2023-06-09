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

import java.util.List;
import java.util.Map;

/**
 * Consists of an agent using tools.
 *
 * @author HamaWhite
 */
public class AgentExecutor extends Chain {

    /**
     * Create from agent and tools.
     */
    public static AgentExecutor fromAgentAndTools(BaseSingleActionAgent agent, List<BaseTool> tools,
            Map<String, Object> kwargs) {
        return new AgentExecutor(agent, tools, kwargs);
    }

    private AgentExecutor(BaseSingleActionAgent agent, List<BaseTool> tools, Map<String, Object> kwargs) {
        // Constructor implementation
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

    @Override
    public Map<String, String> _call(Map<String, Object> inputs) {
        return null;
    }
}
