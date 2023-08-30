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

package com.hw.langchain.llms.ollama;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.hw.langchain.chains.query.constructor.JsonUtils;
import com.hw.langchain.llms.base.BaseLLM;
import com.hw.langchain.requests.TextRequestsWrapper;
import com.hw.langchain.schema.GenerationChunk;
import com.hw.langchain.schema.LLMResult;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Ollama locally run large language models.
 *
 * @author HamaWhite
 */
@SuperBuilder
public class Ollama extends BaseLLM {

    /**
     * Base url the model is hosted under.
     */
    @Builder.Default
    private String baseUrl = "http://localhost:11434";

    @Builder.Default
    private String model = "llama2";

    private TextRequestsWrapper requestsWrapper;

    /**
     * Enable Mirostat sampling for controlling perplexity. (0 = disabled, 1 = Mirostat, 2 = Mirostat 2.0)
     */
    @Builder.Default
    private Integer mirostat = 0;

    /**
     * Influences how quickly the algorithm responds to feedback from the generated text.
     * A lower learning rate will result in slower adjustments, while a higher learning rate will make the algorithm more responsive.
     */
    @Builder.Default
    private Float mirostatEta = 0.1f;

    /**
     * Controls the balance between coherence and diversity of the output.
     * A lower value will result in more focused and coherent text.
     */
    @Builder.Default
    private Float mirostatTau = 5.0f;

    /**
     * Sets the size of the context window used to generate the next token.
     */
    @Builder.Default
    private Integer numCtx = 2048;

    /**
     * The number of GQA groups in the transformer layer. Required for some models, for example it is 8 for llama2:70b
     */
    private Integer numGqa;

    /**
     * The number of GPUs to use. On macOS, it defaults to 1 to enable metal support, 0 to disable.
     */
    private Integer numGpu;

    /**
     * Sets the number of threads to use during computation. By default, Ollama will detect this for optimal performance.
     * It is recommended to set this value to the number of physical CPU cores your system has (as opposed to the logical number of cores).
     */
    private Integer numThread;

    /**
     * Sets how far back for the model to look back to prevent repetition. (0 = disabled, -1 = num_ctx)
     */
    @Builder.Default
    private Integer repeatLastN = 64;

    /**
     * Sets how strongly to penalize repetitions. A higher value (e.g., 1.5) will penalize repetitions more strongly,
     * while a lower value (e.g., 0.9) will be more lenient.
     */
    @Builder.Default
    private Float repeatPenalty = 1.1f;

    /**
     * The temperature of the model. Increasing the temperature will make the model answer more creatively.
     */
    @Builder.Default
    private Float temperature = 0.8f;

    /**
     * ail free sampling is used to reduce the impact of less probable tokens from the output.
     * A higher value (e.g., 2.0) will reduce the impact more, while a value of 1.0 disables this setting.
     */
    @Builder.Default
    private Float tfsZ = 1.0f;

    /**
     * Reduces the probability of generating nonsense.
     * A higher value (e.g. 100) will give more diverse answers, while a lower value (e.g. 10) will be more conservative.
     */
    @Builder.Default
    private Integer topK = 40;

    /**
     * Works together with top-k. A higher value (e.g., 0.95) will lead to more diverse text,
     * while a lower value (e.g., 0.5) will generate more focused and conservative text.
     */
    @Builder.Default
    private Float topP = 0.9f;

    @Override
    public String llmType() {
        return "ollama-llm";
    }

    public Ollama init() {
        Map<String, String> headers = Map.of("Content-Type", "application/json");
        this.requestsWrapper = new TextRequestsWrapper(headers);
        return this;
    }

    private Map<String, Object> createParams(List<String> stop) {
        Map<String, Object> options = Maps.newHashMap();
        options.put("mirostat", mirostat);
        options.put("mirostat_eta", mirostatEta);
        options.put("mirostat_tau", mirostatTau);
        options.put("num_ctx", numCtx);
        options.put("num_gqa", numGqa);
        options.put("num_gpu", numGpu);
        options.put("num_thread", numThread);
        options.put("repeat_last_n", repeatLastN);
        options.put("repeat_penalty", repeatPenalty);
        options.put("temperature", temperature);
        options.put("stop", stop);
        options.put("tfs_z", tfsZ);
        options.put("top_k", topK);
        options.put("top_p", topP);
        return options;
    }

    public List<String> createStream(String prompt, List<String> stop) {
        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", prompt,
                "options", createParams(stop));
        String response = requestsWrapper.post(baseUrl + "/api/generate", body);
        return response.lines().toList();
    }

    /**
     * Call out to Ollama to generate endpoint.
     *
     * @param prompts The prompt to pass into the model.
     * @param stop    list of stop words to use when generating.
     * @return The string generated by the model.
     */
    @Override
    protected LLMResult innerGenerate(List<String> prompts, List<String> stop) {
        List<List<GenerationChunk>> generations = new ArrayList<>();

        for (String prompt : prompts) {
            GenerationChunk finalChunk = null;

            for (String streamResp : createStream(prompt, stop)) {
                if (StringUtils.isNotEmpty(streamResp)) {
                    GenerationChunk chunk = streamResponseToGenerationChunk(streamResp);
                    if (finalChunk == null) {
                        finalChunk = chunk;
                    } else {
                        finalChunk = finalChunk.add(chunk);
                    }
                }
            }
            generations.add(List.of(requireNonNull(finalChunk)));
        }
        return new LLMResult(generations);
    }

    /**
     * Convert a stream response to a generation chunk.
     *
     * @param streamResponse The stream response as a JSON string.
     * @return A GenerationChunk object containing the converted data.
     */
    public static GenerationChunk streamResponseToGenerationChunk(String streamResponse) {
        Map<String, Object> parsedResponse = JsonUtils.convertFromJsonStr(streamResponse, new TypeReference<>() {
        });

        Map<String, Object> generationInfo = null;
        if (parsedResponse.get("done").equals(true)) {
            generationInfo = parsedResponse;
        }
        String text = (String) parsedResponse.getOrDefault("response", "");
        return new GenerationChunk(text, generationInfo);
    }
}
