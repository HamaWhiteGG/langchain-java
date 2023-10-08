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

package com.hw.langchain.agents.load.tools;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.math.base.LLMMathChain;
import com.hw.langchain.tools.base.BaseTool;
import com.hw.langchain.tools.base.Tool;
import com.hw.langchain.utilities.SerpAPIWrapper;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Load tools.
 *
 * @author HamaWhite
 */
public class LoadTools {

    public static BaseTool getLLMMath(BaseLanguageModel llm) {
        return new Tool(
                "Calculator",
                "Useful for when you need to answer questions about math.",
                LLMMathChain.fromLLM(llm)::run);
    }

    public static BaseTool getSerpapi(Map<String, Object> kwargs) {
        return new Tool("Search",
                "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.",
                SerpAPIWrapper.of(kwargs)::run);
    }

    private static Map<String, Pair<Function<Map<String, Object>, BaseTool>, List<String>>> _EXTRA_OPTIONAL_TOOLS =
            MapUtil.of("serpapi", Pair.of(LoadTools::getSerpapi, ListUtil.of("serpapi_api_key", "aiosession")));

    private static Map<String, Function<BaseLanguageModel, BaseTool>> _LLM_TOOLS = MapUtil.of("llm-math", LoadTools::getLLMMath);

    public static List<BaseTool> loadTools(List<String> toolNames, BaseLanguageModel llm) {
        return loadTools(toolNames, llm, MapUtil.empty());
    }

    /**
     * Load tools based on their name.
     *
     * @param toolNames name of tools to load.
     * @param llm       language model, may be needed to initialize certain tools.
     * @param kwargs    keyword arguments
     * @return List of tools.
     */
    public static List<BaseTool> loadTools(List<String> toolNames, BaseLanguageModel llm, Map<String, Object> kwargs) {
        List<BaseTool> tools = new ArrayList<>();
        for (String name : toolNames) {
            if (_LLM_TOOLS.containsKey(name)) {
                BaseTool tool = _LLM_TOOLS.get(name).apply(llm);
                tools.add(tool);
            } else if (_EXTRA_OPTIONAL_TOOLS.containsKey(name)) {
                Pair<Function<Map<String, Object>, BaseTool>, List<String>> pair = _EXTRA_OPTIONAL_TOOLS.get(name);
                List<String> extraKeys = pair.getRight();
                Map<String, Object> subKwargs = extraKeys.stream()
                        .filter(kwargs::containsKey)
                        .collect(Collectors.toMap(key -> key, kwargs::get));

                BaseTool tool = pair.getLeft().apply(subKwargs);
                tools.add(tool);
            } else {
                throw new IllegalArgumentException("Got unknown tool " + name);
            }
        }
        return tools;
    }
}
