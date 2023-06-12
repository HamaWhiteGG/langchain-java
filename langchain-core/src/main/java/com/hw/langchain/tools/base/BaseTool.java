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

package com.hw.langchain.tools.base;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface LangChain tools must implement.
 *
 * @author HamaWhite
 */
@Data
public abstract class BaseTool {

    /**
     * The unique name of the tool that clearly communicates its purpose.
     */
    public String name;

    /**
     * Used to tell the model how/when/why to use the tool.
     * You can provide few-shot examples as a part of the description.
     */
    public String description;

    /**
     * Whether to return the tool's output directly. Setting this to true means
     * that after the tool is called, the AgentExecutor will stop looping.
     */
    public boolean returnDirect = false;

    public BaseTool(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Use the tool.
     */
    public abstract Object _run(String args, Map<String, Object> kwargs);

    /**
     * For backwards compatibility, if run_input is a string,
     * pass as a positional argument.
     *
     * @param toolInput String or Map<String, Object>
     * @return
     */
    public Pair<Object[], Map<String, Object>> toArgsAndKwargs(Object toolInput) {
        if (toolInput instanceof String) {
            return Pair.of(new Object[]{toolInput}, new HashMap<>());
        } else {
            return Pair.of(new Object[]{}, (Map<String, Object>) toolInput);
        }
    }

    /**
     * Run the tool.
     *
     * @param toolInput String or Map<String, Object>
     * @param kwargs
     * @return
     */
    public Object run(Object toolInput, Map<String, Object> kwargs) {
        Pair<Object[], Map<String, Object>> pair = toArgsAndKwargs(toolInput);
        String args = pair.getKey()[0].toString();
        return _run(args, pair.getValue());
    }
}
