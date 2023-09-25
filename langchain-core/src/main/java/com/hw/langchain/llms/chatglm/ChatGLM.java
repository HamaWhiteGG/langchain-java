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

package com.hw.langchain.llms.chatglm;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hw.langchain.chains.query.constructor.JsonUtils;
import com.hw.langchain.chat.models.base.LLM;
import com.hw.langchain.requests.TextRequestsWrapper;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hw.langchain.llms.Utils.enforceStopTokens;

/**
 * ChatGLM LLM service.
 *
 * @author HamaWhite
 */
@SuperBuilder
public class ChatGLM extends LLM {

    private static final Logger LOG = LoggerFactory.getLogger(ChatGLM.class);

    /**
     * Endpoint URL to use.
     */
    @Builder.Default
    private String endpointUrl = "http://127.0.0.1:8000/";

    /**
     * Max token allowed to pass to the model.
     */
    @Builder.Default
    private int maxToken = 20000;

    /**
     * LLM model temperature from 0 to 10.
     */
    @Builder.Default
    private float temperature = 0.1f;

    /**
     * History of the conversation
     */
    @Builder.Default
    private List<List<?>> history = new ArrayList<>();

    /**
     * Top P for nucleus sampling from 0 to 1
     */
    @Builder.Default
    private float topP = 0.7f;

    /**
     * Whether to use history or not
     */
    private boolean withHistory;

    private TextRequestsWrapper requestsWrapper;

    public ChatGLM init() {
        Map<String, String> headers = MapUtil.of("Content-Type", "application/json");
        this.requestsWrapper = new TextRequestsWrapper(headers);
        return this;
    }

    @Override
    public String llmType() {
        return "chat_glm";
    }

    @Override
    public String innerCall(String prompt, List<String> stop) {
        Map<String, Object> payload = MapBuilder.create(new HashMap<String, Object>())
                .put("prompt", prompt)
                .put("temperature", temperature)
                .put("history", history)
                .put("max_length", maxToken)
                .put("top_p", topP).map();

        LOG.debug("ChatGLM payload: {}", payload);
        String response = requestsWrapper.post(endpointUrl, payload);
        LOG.debug("ChatGLM response: {}", response);

        Map<String, Object> parsedResponse = JsonUtils.convertFromJsonStr(response, new TypeReference<Map<String, Object>>() {});
        String text = parsedResponse.get("response").toString();

        if (CollectionUtils.isNotEmpty(stop)) {
            text = enforceStopTokens(text, stop);
        }
        if (withHistory) {
            history.add(ListUtil.of(text));
        }
        return text;
    }
}
