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

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;

import lombok.var;
import org.apache.commons.lang3.tuple.Pair;

import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Tool that takes in function or coroutine directly.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public class Tool extends BaseTool {

    /**
     * The function to run when the tool is called.
     */
    private final UnaryOperator<String> func;

    public Tool(String name, String description, UnaryOperator<String> func) {
        super(name, description);
        this.func = func;
    }

    /**
     * The tool's input arguments.
     */
    @Override
    public Map<String, Object> args() {
        // For backwards compatibility, if the function signature is ambiguous,
        // assume it takes a single string input.
        return MapUtil.of("tool_input", MapUtil.of("type", "string"));
    }

    @Override
    public Pair<Object[], Map<String, Object>> toArgsAndKwargs(Object toolInput) {
        var pair = super.toArgsAndKwargs(toolInput);
        Object[] args = pair.getKey();
        Map<String, Object> kwargs = pair.getValue();

        List<Object> allArgs = new ArrayList<>(Arrays.asList(args));
        allArgs.addAll(kwargs.values());

        if (allArgs.size() != 1) {
            throw new IllegalArgumentException(
                    "Too many arguments to single-input tool " + name + ". Args: " + allArgs);
        }
        return Pair.of(allArgs.toArray(), Maps.newHashMap());
    }

    @Override
    public Object innerRun(String args, Map<String, Object> kwargs) {
        return func.apply(args);
    }
}
