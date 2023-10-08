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
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.llms.base.BaseLLM;
import com.hw.langchain.schema.AsyncLLMResult;
import com.hw.langchain.schema.Generation;
import com.hw.langchain.schema.LLMResult;
import com.hw.openai.OpenAiClient;
import com.hw.openai.common.OpenaiApiType;
import com.hw.openai.entity.completions.Choice;
import com.hw.openai.entity.completions.Completion;
import com.hw.openai.entity.completions.CompletionChunk;
import com.hw.openai.entity.completions.CompletionResp;

import io.reactivex.Flowable;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.var;
import okhttp3.Interceptor;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hw.langchain.utils.Resilience4jRetryUtils.retryWithExponentialBackoff;

/**
 * Wrapper around OpenAI large language models.
 *
 * @author HamaWhite
 */
@SuperBuilder
public class BaseOpenAI extends BaseLLM {

    protected OpenAiClient client;

    /**
     * Model name to use.
     */
    @Builder.Default
    protected String model = "text-davinci-003";

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
     * Generates best_of completions server-side and returns the "best".
     */
    @Builder.Default
    protected int bestOf = 1;

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
     * the username for proxy authentication (optional)
     */
    protected String proxyUsername;

    /**
     * the password for proxy authentication (optional)
     */
    protected String proxyPassword;

    /**
     * Batch size to use when passing multiple documents to generate.
     */
    @Builder.Default
    protected int batchSize = 20;

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
     * Maximum number of retries to make when generating.
     */
    @Builder.Default
    protected int maxRetries = 6;

    /**
     * Whether to stream the results or not.
     */
    protected boolean stream;

    /**
     * Set of special tokens that are allowed.
     */
    protected Set<String> allowedSpecial;

    /**
     * Set of special tokens that are not allowed.
     */
    protected Set<String> disallowedSpecial;

    /**
     * list of okhttp interceptor
     */
    protected List<Interceptor> interceptorList;

    @Override
    public String llmType() {
        return "openai";
    }

    /**
     * Call out to OpenAI's endpoint with k unique prompts.
     *
     * @param prompts The prompts to pass into the model.
     * @param stop    list of stop words to use when generating.
     * @return The full LLM output.
     */
    @Override
    protected LLMResult innerGenerate(List<String> prompts, List<String> stop) {
        List<Choice> choices = new ArrayList<>();
        List<List<String>> subPrompts = getSubPrompts(prompts);
        Completion completion = buildCompletion(stop);

        for (var prompt : subPrompts) {
            completion.setPrompt(prompt);
            CompletionResp response =
                    retryWithExponentialBackoff(maxRetries, () -> client.createCompletion(completion));
            choices.addAll(response.getChoices());
        }

        return createLlmResult(choices, prompts, MapUtil.empty());
    }

    @Override
    protected Flux<AsyncLLMResult> asyncInnerGenerate(List<String> prompts, List<String> stop) {
        Completion completion = buildCompletion(stop);

        completion.setPrompt(prompts);
        completion.setStream(true);
        Flowable<CompletionChunk> response = retryWithExponentialBackoff(maxRetries,
                () -> client.streamCompletion(completion));

        return RxJava2Adapter.flowableToFlux(response).map(e -> {
            Generation generation = new Generation(e.getChoices().get(0).getText());
            return new AsyncLLMResult(ListUtil.of(generation), null);
        });
    }

    private Completion buildCompletion(List<String> stop) {
        return Completion.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .topP(topP)
                .frequencyPenalty(frequencyPenalty)
                .presencePenalty(presencePenalty)
                .n(n)
                .logitBias(logitBias)
                .stop(stop)
                .build();
    }

    /**
     * Create the LLMResult from the choices and prompts.
     */
    private LLMResult createLlmResult(List<Choice> choices, List<String> prompts, Map<String, Integer> tokenUsage) {
        List<List<Generation>> generations = new ArrayList<>();
        for (int i = 0; i < prompts.size(); i++) {
            List<Choice> subChoices = choices.subList(i * n, (i + 1) * n);
            List<Generation> generationList = new ArrayList<>();
            for (Choice choice : subChoices) {
                Map<String, Object> generationInfo = new HashMap<>(2);
                generationInfo.put("finishReason", choice.getFinishReason());
                generationInfo.put("logprobs", choice.getLogprobs());

                Generation generation = Generation.builder()
                        .text(choice.getText())
                        .generationInfo(generationInfo)
                        .build();
                generationList.add(generation);
            }
            generations.add(generationList);
        }

        Map<String, Object> llmOutput = new HashMap<>(2);
        llmOutput.put("token_usage", tokenUsage);
        llmOutput.put("model_name", model);

        return new LLMResult(generations, llmOutput);
    }

    /**
     * Get the sub prompts for llm call.
     */
    private List<List<String>> getSubPrompts(List<String> prompts) {
        if (maxTokens == -1) {
            checkArgument(prompts.size() == 1, "maxTokens set to -1 not supported for multiple inputs.");
            maxTokens = maxTokensForPrompt(prompts.get(0));
        }
        List<List<String>> subPrompts = new ArrayList<>();
        for (int i = 0; i < prompts.size(); i += this.batchSize) {
            subPrompts.add(prompts.subList(i, Math.min(i + this.batchSize, prompts.size())));
        }
        return subPrompts;
    }

    /**
     * Calculate the maximum number of tokens possible to generate for a prompt.
     *
     * @param prompt The prompt to pass into the model.
     * @return The maximum number of tokens to generate for a prompt.
     */
    private int maxTokensForPrompt(String prompt) {
        // TODO
        return 0;
    }
}
