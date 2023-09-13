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

package com.hw.langchain.chains.query.constructor.prompt;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.utils.ResourceBundleUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
public class Prompt {

    private Prompt() {
    }

    private static final String SONG_DATA_SOURCE =
            ResourceBundleUtils.getString("prompt.chain.query.template")
            .replace("{", "{{").replace("}", "}}");

    private static final String FULL_ANSWER = ResourceBundleUtils.getString("prompt.chain.full.answer");

    private static final String NO_FILTER_ANSWER = ResourceBundleUtils.getString("prompt.chain.filter.answer");

    private static final String WITH_LIMIT_ANSWER = ResourceBundleUtils.getString("prompt.chain.limit.answer");

    public static final List<Map<String, Object>> DEFAULT_EXAMPLES = ListUtil.of(
            createExample(1, SONG_DATA_SOURCE,
                    "What are songs by Taylor Swift or Katy Perry about teenage romance under 3 minutes long in the dance pop genre",
                    FULL_ANSWER),
            createExample(2, SONG_DATA_SOURCE, "What are songs that were not published on Spotify", NO_FILTER_ANSWER));

    public static final List<Map<String, Object>> EXAMPLES_WITH_LIMIT = ListUtil.of(
            createExample(1, SONG_DATA_SOURCE,
                    "What are songs by Taylor Swift or Katy Perry about teenage romance under 3 minutes long in the dance pop genre",
                    FULL_ANSWER),
            createExample(2, SONG_DATA_SOURCE, "What are songs that were not published on Spotify", NO_FILTER_ANSWER),
            createExample(3, SONG_DATA_SOURCE, "What are three songs about love", WITH_LIMIT_ANSWER));

    private static Map<String, Object> createExample(int i, String dataSource, String userQuery,
            String structuredRequest) {
        return MapBuilder.create(new HashMap<String, Object>())
                .put("i", i)
                .put("data_source", dataSource)
                .put("user_query", userQuery)
                .put("structured_request", structuredRequest)
                .map();
    }

    private static final String EXAMPLE_PROMPT_TEMPLATE = ResourceBundleUtils.getString("prompt.chain.example.template");

    public static final PromptTemplate EXAMPLE_PROMPT = new PromptTemplate(
            ListUtil.of("i", "data_source", "user_query", "structured_request"), EXAMPLE_PROMPT_TEMPLATE);

    public static final String DEFAULT_SCHEMA = ResourceBundleUtils.getString("prompt.chain.default.schema");

    public static final String SCHEMA_WITH_LIMIT = ResourceBundleUtils.getString("prompt.chain.scheme.limit");

    public static final String DEFAULT_PREFIX = ResourceBundleUtils.getString("prompt.chain.default.prefix");

    public static final String DEFAULT_SUFFIX = ResourceBundleUtils.getString("prompt.chain.default.suffix");

}
