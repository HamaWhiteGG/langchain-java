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

import cn.hutool.core.map.MapBuilder;
import com.hw.langchain.chat.models.base.BaseChatModel;
import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.ChatGeneration;
import com.hw.langchain.schema.ChatResult;

import com.hw.openai.OpenAiClient;
import com.hw.openai.common.OpenaiApiType;
import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.ChatCompletionResp;
import com.hw.openai.entity.chat.Message;
import com.hw.openai.entity.completions.Usage;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.var;
import okhttp3.Interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hw.langchain.chat.models.openai.OpenAI.convertOpenAiToLangChain;
import static com.hw.langchain.utils.Resilience4jRetryUtils.retryWithExponentialBackoff;
import static com.hw.langchain.utils.Utils.getOrEnvOrDefault;

/**
 * Wrapper around OpenAI Chat large language models.
 *
 * @author HamaWhite
 */
@SuperBuilder
public class ChatOpenAI extends BaseChatModel {

    protected OpenAiClient client;

    /**
     * Model name to use.
     */
    @Builder.Default
    protected String model = "gpt-3.5-turbo";

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

    @Builder.Default
    protected OpenaiApiType openaiApiType = OpenaiApiType.OPENAI;

    protected String openaiApiVersion;

    protected String openaiOrganization;

    /**
     * To support explicit proxy for OpenAI.
     */
    protected String openaiProxy;

    /**
     * Timeout for requests to OpenAI completion API. Default is 16 seconds.
     */
    @Builder.Default
    protected long requestTimeout = 16;

    /**
     * Maximum number of retries to make when generating.
     */
    @Builder.Default
    protected int maxRetries = 6;

    /**
     * Whether to stream the results or not.
     */
    protected boolean stream;

    /**
     * Number of chat completions to generate for each prompt.
     */
    @Builder.Default
    protected int n = 1;

    /**
     * Maximum number of tokens to generate.
     */
    protected Integer maxTokens;

    /**
     * list of okhttp interceptor
     */
    private List<Interceptor> interceptorList;

    /**
     * Validate parameters and init client
     */
    public ChatOpenAI init() {
        openaiApiKey = getOrEnvOrDefault(openaiApiKey, "OPENAI_API_KEY");
        openaiOrganization = getOrEnvOrDefault(openaiOrganization, "OPENAI_ORGANIZATION", "");
        openaiApiBase = getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE", "");
        openaiProxy = getOrEnvOrDefault(openaiProxy, "OPENAI_PROXY", "");
        openaiApiVersion = getOrEnvOrDefault(openaiApiVersion, "OPENAI_API_VERSION", "");

        this.client = OpenAiClient.builder()
                .openaiApiBase(openaiApiBase)
                .openaiApiKey(openaiApiKey)
                .openaiApiVersion(openaiApiVersion)
                .openaiApiType(openaiApiType)
                .openaiOrganization(openaiOrganization)
                .openaiProxy(openaiProxy)
                .requestTimeout(requestTimeout)
                .interceptorList(interceptorList)
                .build()
                .init();

        if (n < 1) {
            throw new IllegalArgumentException("n must be at least 1.");
        }
        if (n > 1 && stream) {
            throw new IllegalArgumentException("n must be 1 when streaming.");
        }
        return this;
    }

    @Override
    public Map<String, Object> combineLlmOutputs(List<Map<String, Object>> llmOutputs) {
        Usage usage = llmOutputs.stream()
                .filter(Objects::nonNull)
                .map(e -> (Usage) e.get("token_usage"))
                .reduce((a1, a2) -> new Usage(
                        a1.getPromptTokens() + a2.getPromptTokens(),
                        a1.getCompletionTokens() + a2.getCompletionTokens(),
                        a1.getTotalTokens() + a2.getTotalTokens()))
                .orElse(new Usage());

        return MapBuilder.create(new HashMap<String,Object>()).put("token_usage", usage)
                .put("model_name", this.model).map();
    }

    @Override
    public ChatResult innerGenerate(List<BaseMessage> messages, List<String> stop) {
        List<Message> chatMessages = convertMessages(messages);

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(model)
                .temperature(temperature)
                .messages(chatMessages)
                .maxTokens(maxTokens)
                .stream(stream)
                .n(n)
                .stop(stop)
                .build();

        var response = retryWithExponentialBackoff(maxRetries, () -> client.createChatCompletion(chatCompletion));
        return createChatResult(response);
    }

    public List<Message> convertMessages(List<BaseMessage> messages) {
        return messages.stream()
                .map(OpenAI::convertLangChainToOpenAI)
                .collect(Collectors.toList());
    }

    public ChatResult createChatResult(ChatCompletionResp response) {
        List<ChatGeneration> generations = response.getChoices()
                .stream()
                .map(choice -> convertOpenAiToLangChain(choice.getMessage()))
                .map(ChatGeneration::new)
                .collect(Collectors.toList());

        Map<String, Object> llmOutput = MapBuilder.create(new HashMap<String,Object>())
                .put("token_usage", response.getUsage())
                .put("model_name", response.getModel()).map();
        return new ChatResult(generations, llmOutput);
    }

    @Override
    public String llmType() {
        return "openai-chat";
    }
}
