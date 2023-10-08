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

package com.hw.openai.entity.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ChatCompletion
 * @author HamaWhite
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletion implements Serializable {

    /**
     * ID of the model to use.
     */
    @NotBlank
    private String model;

    /**
     * A list of messages describing the conversation so far.
     */
    @NotEmpty
    private List<Message> messages;

    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     * <p>
     * We generally recommend altering this or top_p but not both.
     */
    @Builder.Default
    private float temperature = 1.0f;

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of
     * the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     * <p>
     * We generally recommend altering this or temperature but not both.
     */
    @Builder.Default
    @JsonProperty("top_p")
    private float topP = 1.0f;

    /**
     * How many chat completion choices to generate for each input message.
     */
    @Builder.Default
    private Integer n = 1;

    /**
     * If set, partial message deltas will be sent, like in ChatGPT. Tokens will be sent as data-only server-sent
     * events as they become available, with the stream terminated by a data: [DONE] message.
     */
    private boolean stream;

    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     */
    private List<String> stop;

    /**
     * The maximum number of tokens to generate in the chat completion.
     * <p>
     * The total length of input tokens and generated tokens is limited by the model's context length.
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far,
     * increasing the model's likelihood to talk about new topics.
     */
    @JsonProperty("presence_penalty")
    private float presencePenalty;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text
     * so far, decreasing the model's likelihood to repeat the same line verbatim.
     */
    @JsonProperty("frequency_penalty")
    private float frequencyPenalty;

    /**
     * Modify the likelihood of specified tokens appearing in the completion.
     */
    @JsonProperty("logit_bias")
    private Map<String, Float> logitBias;

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    private String user;
}
