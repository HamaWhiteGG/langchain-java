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

package com.hw.langchain.chat.models.openai;

import com.hw.langchain.chat.models.base.BaseChatModel;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper around OpenAI Chat large language models.
 *
 * @author HamaWhite
 */
@SuperBuilder
public class ChatOpenAI extends BaseChatModel {

    protected Object client;

    /**
     * Model name to use.
     */
    @Builder.Default
    protected String modelName = "gpt-3.5-turbo";

    /**
     * What sampling temperature to use.
     */
    @Builder.Default
    protected float temperature = 0.7f;

    /**
     * Holds any model parameters valid for `create` call not explicitly specified.
     */
    @Builder.Default
    protected Map<String, Object> modelKwargs = new HashMap<>();

    /**
     * Base URL path for API requests, leave blank if not using a proxy or service emulator.
     */
    protected String openaiApiKey;

    protected String openaiApiBase;

    protected String openaiOrganization;

    /**
     * To support explicit proxy for OpenAI.
     */
    protected String openaiProxy;

    /**
     * Timeout for requests to OpenAI completion API. Default is 10 seconds.
     */
    @Builder.Default
    protected long requestTimeout = 10;

    /**
     * Maximum number of retries to make when generating.
     */
    @Builder.Default
    protected int maxRetries = 6;

    /**
     * Whether to stream the results or not.
     */
    protected boolean streaming;

    /**
     * Number of chat completions to generate for each prompt.
     */
    @Builder.Default
    protected int n = 1;

    /**
     * Maximum number of tokens to generate.
     */
    protected Integer maxTokens;

}
