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

package com.hw.langchain.embeddings.ollama;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hw.langchain.chains.query.constructor.JsonUtils;
import com.hw.langchain.embeddings.base.Embeddings;
import com.hw.langchain.requests.TextRequestsWrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HamaWhite
 */
@Builder
@AllArgsConstructor
public class OllamaEmbeddings implements Embeddings {

    @Builder.Default
    private String baseUrl = "http://localhost:11434";

    @Builder.Default
    private String model = "llama2";

    private TextRequestsWrapper requestsWrapper;

    public OllamaEmbeddings init() {
        Map<String, String> headers = MapUtil.of("Content-Type", "application/json");
        this.requestsWrapper = new TextRequestsWrapper(headers);
        return this;
    }

    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        return texts.stream().map(this::embeddings).collect(Collectors.toList());
    }

    @Override
    public List<Float> embedQuery(String text) {
        return embeddings(text);
    }

    private List<Float> embeddings(String prompt) {
        Map<String, Object> body = MapBuilder.create(new HashMap<String, Object>())
                .put("model", model)
                .put("prompt", prompt).map();
        String response = requestsWrapper.post(baseUrl + "/api/embeddings", body);
        Map<String, List<Float>> parsedResponse = JsonUtils.convertFromJsonStr(response, new TypeReference<Map<String, List<Float>>>() {});
        return parsedResponse.get("embedding");
    }
}
