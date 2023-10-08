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
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.schema.AgentAction;
import com.hw.langchain.schema.AgentFinish;
import com.hw.langchain.schema.AgentResult;
import com.hw.langchain.tools.base.BaseTool;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Agent class.
 *
 * @author HamaWhite
 */
public abstract class BaseSingleActionAgent {

    /**
     * Return the input keys.
     */
    public abstract List<String> inputKeys();

    public List<String> returnValues() {
        return Lists.newArrayList("output");
    }

    /**
     * Given input, decided what to do.
     *
     * @param intermediateSteps Steps the LLM has taken to date, along with observations
     * @param kwargs            User inputs.
     * @return Action specifying what tool to use.
     */
    public abstract AgentResult plan(List<Pair<AgentAction, String>> intermediateSteps, Map<String, Object> kwargs);

    public static BaseSingleActionAgent fromLlmAndTools(
            BaseLanguageModel llm,
            List<BaseTool> tools,
            Map<String, Object> kwargs) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return response when agent has been stopped due to max iterations.
     */
    public AgentFinish returnStoppedResponse(String earlyStoppingMethod,
            List<Pair<AgentAction, String>> intermediateSteps, Map<String, ?> kwargs) {
        if (earlyStoppingMethod.equals("force")) {
            // `force` just returns a constant string
            Map<String, String> returnValues = new HashMap<>();
            returnValues.put("output", "Agent stopped due to iteration limit or time limit.");
            return new AgentFinish(returnValues, "");
        } else {
            throw new IllegalArgumentException(
                    String.format("Got unsupported early_stopping_method `%s`", earlyStoppingMethod));
        }
    }

    public Map<String, Object> toolRunLoggingKwargs() {
        return new HashMap<>();
    }
}
