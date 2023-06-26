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

package com.hw.langchain.embeddings.openai;

import com.hw.langchain.embeddings.base.Embeddings;
import com.hw.openai.OpenAiClient;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hw.langchain.utils.Utils.getOrEnvOrDefault;

/**
 * Wrapper around OpenAI embedding models.
 * <p>
 * To use, you should have the environment variable OPENAI_API_KEY set with your API key or pass it as a
 * named parameter to the constructor.
 *
 * @author HamaWhite
 */
@Builder
@AllArgsConstructor
public class OpenAIEmbeddings implements Embeddings {

    private OpenAiClient client;

    @Builder.Default
    private String model = "text-embedding-ada-002";

    private String openaiApiBase;

    /**
     * To support explicit proxy for OpenAI.
     */
    private String openaiProxy;

    @Builder.Default
    private int embeddingCtxLength = 8191;

    private String openaiApiKey;

    protected String openaiOrganization;

    @Builder.Default
    private Set<String> allowedSpecial = new HashSet<>();

    @Builder.Default
    private Set<String> disallowedSpecial = Set.of("all");

    /**
     * Maximum number of texts to embed in each batch
     */
    @Builder.Default
    private int chunkSize = 1000;

    /**
     * Maximum number of retries to make when generating.
     */
    @Builder.Default
    private int maxRetries = 6;

    /**
     * Timeout for requests to OpenAI completion API. Default is 10 seconds.
     */
    @Builder.Default
    protected long requestTimeout = 10;

    /**
     * Validate parameters and init client
     */
    public OpenAIEmbeddings init() {
        openaiApiKey = getOrEnvOrDefault(openaiApiKey, "OPENAI_API_KEY");
        openaiApiBase = getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE", "");
        openaiProxy = getOrEnvOrDefault(openaiProxy, "OPENAI_PROXY", "");
        openaiOrganization = getOrEnvOrDefault(openaiOrganization, "OPENAI_ORGANIZATION", "");

        this.client = OpenAiClient.builder()
                .openaiApiBase(openaiApiBase)
                .openaiApiKey(openaiApiKey)
                .openaiOrganization(openaiOrganization)
                .openaiProxy(openaiProxy)
                .requestTimeout(requestTimeout)
                .build()
                .init();
        return this;
    }

    /**
     * Quick construction method, can also use the Builder pattern.
     */
    public OpenAIEmbeddings() {
        init();
    }

    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        return null;
    }

    @Override
    public List<Float> embedQuery(String text) {
        return null;
    }
}
