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

import java.util.*;
import java.util.function.Function;

/**
 * Tool that takes in function or coroutine directly.
 *
 * @author HamaWhite
 */
public class Tool extends BaseTool {

    /**
     * The function to run when the tool is called.
     */
    private Function<String, String> func;

    public Tool(String name, String description, Function<String, String> func) {
        super(name, description);
        this.func = func;
    }

    /**
     * The tool's input arguments.
     */
    public Map<String, Object> args() {
        // For backwards compatibility, if the function signature is ambiguous,
        // assume it takes a single string input.
        return Map.of("tool_input", Map.of("type", "string"));
    }

    public Pair<Object[], Map<String, Object>> toArgsAndKwargs(Object toolInput) {
        Pair<Object[], Map<String, Object>> pair = super.toArgsAndKwargs(toolInput);
        Object[] args = pair.getKey();
        Map<String, Object> kwargs = pair.getValue();

        List<Object> allArgs = new ArrayList<>(Arrays.asList(args));
        allArgs.addAll(kwargs.values());

        if (allArgs.size() != 1) {
            throw new IllegalArgumentException(
                    "Too many arguments to single-input tool " + this.name + ". Args: " + allArgs);
        }

        return Pair.of(allArgs.toArray(), new HashMap<>());
    }

    @Override
    public Object _run(String args, Map<String, Object> kwargs) {
        return func.apply(args);
    }
}
