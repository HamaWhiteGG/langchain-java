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

package com.hw.langchain.llms.openai;

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.llms.base.BaseLLM;
import com.hw.langchain.schema.AsyncLLMResult;
import com.hw.langchain.schema.Generation;
import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.utils.Utils;
import com.hw.openai.OpenAiClient;
import com.hw.openai.common.OpenaiApiType;
import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.ChatCompletionResp;
import com.hw.openai.entity.chat.Message;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import reactor.core.publisher.Flux;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Wrapper around OpenAI Chat large language models.
 * @author HamaWhite
 */
@SuperBuilder
public class OpenAIChat extends BaseLLM {

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
     * The maximum number of tokens to generate in the completion.
     * -1 returns as many tokens as possible given the prompt and the model's maximal context size.
     */
    @Builder.Default
    protected int maxTokens = 256;

    /**
     * Total probability mass of tokens to consider at each step.
     */
    @Builder.Default
    protected float topP = 1f;

    /**
     * Penalizes repeated tokens according to frequency.
     */
    protected float frequencyPenalty;

    /**
     * Penalizes repeated tokens.
     */
    protected float presencePenalty;

    /**
     * How many completions to generate for each prompt.
     */
    @Builder.Default
    protected int n = 1;

    /**
     * API key for OpenAI.
     */
    protected String openaiApiKey;

    /**
     * Base URL for OpenAI API.
     */
    protected String openaiApiBase;

    /**
     * Api type for Azure OpenAI API.
     */
    @Builder.Default
    protected OpenaiApiType openaiApiType = OpenaiApiType.OPENAI;

    /**
     * Api version for Azure OpenAI API.
     */
    protected String openaiApiVersion;

    /**
     * Organization ID for OpenAI.
     */
    protected String openaiOrganization;

    /**
     * Support explicit proxy for OpenAI
     */
    protected String openaiProxy;

    /**
     * Maximum number of retries to make when generating.
     */
    @Builder.Default
    protected int maxRetries = 6;

    /**
     * Series of messages for Chat input.
     */
    @Builder.Default
    private List<Message> prefixMessages = new ArrayList<>();

    /**
     * Timeout for requests to OpenAI completion API. Default is 16 seconds.
     */
    @Builder.Default
    protected long requestTimeout = 16;

    /**
     * Adjust the probability of specific tokens being generated.
     */
    protected Map<String, Float> logitBias;

    /**
     * Whether to stream the results or not.
     */
    protected boolean stream;

    public OpenAIChat init() {
        openaiApiBase = Utils.getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE", "");
        openaiApiKey = Utils.getOrEnvOrDefault(openaiApiKey, "OPENAI_API_KEY");
        openaiOrganization = Utils.getOrEnvOrDefault(openaiOrganization, "OPENAI_ORGANIZATION", "");
        openaiProxy = Utils.getOrEnvOrDefault(openaiProxy, "OPENAI_PROXY", "");
        openaiApiVersion = Utils.getOrEnvOrDefault(openaiApiVersion, "OPENAI_API_VERSION", "");

        this.client = OpenAiClient.builder()
                .openaiApiBase(openaiApiBase)
                .openaiApiKey(openaiApiKey)
                .openaiApiVersion(openaiApiVersion)
                .openaiApiType(openaiApiType)
                .openaiOrganization(openaiOrganization)
                .openaiProxy(openaiProxy)
                .requestTimeout(requestTimeout)
                .build()
                .init();
        return this;
    }

    @Override
    public String llmType() {
        return "openai-chat";
    }

    @Override
    protected LLMResult innerGenerate(List<String> prompts, List<String> stop) {
        List<Message> messages = getChatMessages(prompts);

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(model)
                .temperature(temperature)
                .messages(messages)
                .maxTokens(maxTokens)
                .topP(topP)
                .frequencyPenalty(frequencyPenalty)
                .presencePenalty(presencePenalty)
                .n(n)
                .logitBias(logitBias)
                .stop(stop)
                .build();

        ChatCompletionResp response = client.createChatCompletion(chatCompletion);

        List<List<Generation>> generations = new ArrayList<>();
        Generation generation = Generation.builder()
                .text(response.getChoices().get(0).getMessage().getContent())
                .build();

        generations.add(ListUtil.of(generation));

        Map<String, Object> llmOutput = new HashMap<>(2);
        llmOutput.put("token_usage", response.getUsage());
        llmOutput.put("model_name", response.getModel());

        return new LLMResult(generations, llmOutput);
    }

    @Override
    protected Flux<AsyncLLMResult> asyncInnerGenerate(List<String> prompts, List<String> stop) {
        throw new UnsupportedOperationException("not supported yet.");
    }

    private List<Message> getChatMessages(List<String> prompts) {
        checkArgument(prompts.size() == 1, "OpenAIChat currently only supports single prompt, got %s", prompts);
        List<Message> messages = new ArrayList<>(prefixMessages);
        messages.add(Message.of(prompts.get(0)));
        return messages;
    }
}
