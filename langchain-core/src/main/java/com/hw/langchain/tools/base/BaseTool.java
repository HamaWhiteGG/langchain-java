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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interface LangChain tools must implement.
 *
 * @author HamaWhite
 */
@Data
public abstract class BaseTool {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTool.class);

    /**
     * The unique name of the tool that clearly communicates its purpose.
     */
    public final String name;

    /**
     * Used to tell the model how/when/why to use the tool.
     * You can provide few-shot examples as a part of the description.
     */
    public final String description;

    /**
     * Whether to return the tool's output directly. Setting this to true means
     * that after the tool is called, the AgentExecutor will stop looping.
     */
    public final boolean returnDirect;

    protected BaseTool(String name, String description) {
        this(name, description, false);
    }

    protected BaseTool(String name, String description, boolean returnDirect) {
        this.name = name;
        this.description = description;
        this.returnDirect = returnDirect;
    }

    /**
     * Whether the tool only accepts a single input.
     */
    public boolean isSingleInput() {
        var keys = args().keySet()
                .stream()
                .filter(k -> !"kwargs".equals(k))
                .collect(Collectors.toSet());
        return keys.size() == 1;
    }

    public Map<String, Object> args() {
        return MapUtil.empty();
    }

    /**
     * Use the tool.
     *
     * @param args   Tool arguments as a String.
     * @param kwargs Keyword arguments as a Map<String, Object>.
     * @return Result of using the tool as an Object.
     */
    public abstract Object innerRun(String args, Map<String, Object> kwargs);

    public Pair<Object[], Map<String, Object>> toArgsAndKwargs(Object toolInput) {
        if (toolInput instanceof String) {
            return Pair.of(new Object[]{toolInput}, Maps.newHashMap());
        } else {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapInput = (Map<String, Object>) toolInput;
            return Pair.of(new Object[]{}, mapInput);
        }
    }

    /**
     * Run the tool.
     *
     * @param toolInput Input for the tool, can be a String or a Map<String, Object>.
     * @param kwargs    Keyword arguments for the tool as a Map<String, Object>.
     * @return Result of running the tool as an Object.
     */
    public Object run(Object toolInput, Map<String, Object> kwargs) {
        LOG.debug("kwargs: {}", kwargs);
        Pair<Object[], Map<String, Object>> pair = toArgsAndKwargs(toolInput);
        String args = pair.getKey()[0].toString();

        kwargs.putAll(pair.getValue());
        return innerRun(args, kwargs);
    }
}
