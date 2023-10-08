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

package com.hw.langchain.agents.initialize;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.agents.agent.AgentExecutor;
import com.hw.langchain.agents.agent.BaseSingleActionAgent;
import com.hw.langchain.agents.agent.types.AgentType;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.tools.base.BaseTool;

import org.apache.commons.lang3.reflect.MethodUtils;

import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hw.langchain.agents.loading.Loading.loadAgent;
import static com.hw.langchain.agents.types.Types.AGENT_TO_CLASS;

/**
 * Load agent.
 *
 * @author HamaWhite
 */
public class Initialize {

    private Initialize() {
        // private constructor to hide the implicit public one
        throw new IllegalStateException("Utility class");
    }

    public static AgentExecutor initializeAgent(List<BaseTool> tools, BaseLanguageModel llm, AgentType agent) {
        return initializeAgent(tools, llm, agent, null, MapUtil.empty(), MapUtil.empty());
    }

    /**
     * Load an agent executor given tools and LLM.
     *
     * @param tools       List of tools this agent has access to.
     * @param llm         Language model to use as the agent.
     * @param agent       Agent type to use. If None and agent_path is also None, will default to AgentType.ZERO_SHOT_REACT_DESCRIPTION.
     * @param agentPath   Path to serialized agent to use.
     * @param agentKwargs Additional key word arguments to pass to the underlying agent
     * @param kwargs      Additional key word arguments passed to the agent executor
     * @return An agent executor
     */
    @SneakyThrows({InvocationTargetException.class, NoSuchMethodException.class, IllegalAccessException.class})
    public static AgentExecutor initializeAgent(List<BaseTool> tools, BaseLanguageModel llm, AgentType agent,
            String agentPath, Map<String, Object> agentKwargs, Map<String, Object> kwargs) {
        BaseSingleActionAgent agentObj;
        if (agent == null && agentPath == null) {
            agent = AgentType.ZERO_SHOT_REACT_DESCRIPTION;
        }
        if (agent != null && agentPath != null) {
            throw new IllegalArgumentException(
                    "Both `agent` and `agentPath` are specified, but at most only one should be.");
        }
        if (agent != null) {
            if (!AGENT_TO_CLASS.containsKey(agent)) {
                throw new IllegalArgumentException(
                        "Got unknown agent type: " + agent + ". Valid types are: " + AGENT_TO_CLASS.keySet() + ".");
            }
            Class<? extends BaseSingleActionAgent> clazz = AGENT_TO_CLASS.get(agent);
            agentKwargs = agentKwargs != null ? agentKwargs : MapUtil.empty();
            agentObj = (BaseSingleActionAgent) MethodUtils.invokeStaticMethod(clazz, "fromLLMAndTools",
                    llm, tools, agentKwargs);
        } else if (agentPath != null) {
            agentObj = loadAgent(agentPath, MapBuilder.create(new HashMap<String, Object>())
                    .put("llm", llm)
                    .put("tools", tools).map());
        } else {
            throw new IllegalArgumentException(
                    "Somehow both `agent` and `agentPath` are null, this should never happen.");
        }
        return AgentExecutor.fromAgentAndTools(agentObj, tools);
    }
}
