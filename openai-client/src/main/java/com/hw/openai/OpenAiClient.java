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

package com.hw.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.ChatCompletionResp;
import com.hw.openai.entity.completions.Completion;
import com.hw.openai.entity.completions.CompletionResp;
import com.hw.openai.entity.models.Model;
import com.hw.openai.entity.models.ModelResp;
import com.hw.openai.service.OpenAiService;
import com.hw.openai.utils.ProxyUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @description: Represents a client for interacting with the OpenAI API.
 * @author: HamaWhite
 */
@Data
@Builder
public class OpenAiClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAiClient.class);

    private String openaiApiBase;

    private String openaiApiKey;

    private String openaiOrganization;

    private String openaiProxy;

    private OpenAiService service;

    /**
     * Initializes the OpenAiClient instance.
     *
     * @return the initialized OpenAiClient instance
     */
    public OpenAiClient init() {
        openaiApiBase = getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE", "https://api.openai.com/v1/");
        openaiProxy = getOrEnvOrDefault(openaiProxy, "OPENAI_PROXY");

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(chain -> {
            // If openaiApiKey is not set, read the value of OPENAI_API_KEY from the environment.
            openaiApiKey = getOrEnvOrDefault(openaiApiKey, "OPENAI_API_KEY");
            openaiOrganization = getOrEnvOrDefault(openaiOrganization, "OPENAI_ORGANIZATION", "");

            Request request = chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .header("OpenAI-Organization", openaiOrganization)
                    .build();

            return chain.proceed(request);
        });

        // Add HttpLogging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(LOG::debug);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        if (StringUtils.isNotEmpty(openaiProxy)) {
            httpClientBuilder.proxy(ProxyUtils.http(openaiProxy));
        }

        // Used for automatic discovery and registration of Jackson modules
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(openaiApiBase)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClientBuilder.build())
                .build();

        this.service = retrofit.create(OpenAiService.class);
        return this;
    }

    private String getOrEnvOrDefault(String originalValue, String envKey, String... defaultValue) {
        if (StringUtils.isNotEmpty(originalValue)) {
            return originalValue;
        }
        String envValue = System.getenv(envKey);
        if (StringUtils.isNotEmpty(envValue)) {
            return envValue;
        }
        if (defaultValue.length > 0) {
            return defaultValue[0];
        }
        return null;
    }

    /**
     * Lists the currently available models, and provides basic information about each one
     * such as the owner and availability.
     *
     * @return the response containing the list of available models
     */
    public ModelResp listModels() {
        return service.listModels().blockingGet();
    }

    /**
     * Retrieves a model instance, providing basic information about the model such as the owner and permissions.
     *
     * @param model the ID of the model to retrieve
     * @return the retrieved model
     */
    public Model retrieveModel(String model) {
        return service.retrieveModel(model).blockingGet();
    }

    /**
     * Creates a completion for the provided prompt and parameters.
     *
     * @param completion the completion object containing the prompt and parameters
     * @return the generated completion text
     */
    public String completion(Completion completion) {
        CompletionResp response = service.completion(completion).blockingGet();

        String text = response.getChoices().get(0).getText();
        return StringUtils.trim(text);
    }

    /**
     * Creates a completion for the provided prompt and parameters.
     *
     * @param completion the completion object containing the prompt and parameters
     * @return the completion response
     */
    public CompletionResp create(Completion completion) {
        return service.completion(completion).blockingGet();
    }

    /**
     * Creates a model response for the given chat conversation.
     *
     * @param chatCompletion the chat completion object containing the conversation
     * @return the generated model response text
     */
    public String chatCompletion(ChatCompletion chatCompletion) {
        ChatCompletionResp response = service.chatCompletion(chatCompletion).blockingGet();

        String content = response.getChoices().get(0).getMessage().getContent();
        return StringUtils.trim(content);
    }

    /**
     * Creates a model response for the given chat conversation.
     *
     * @param chatCompletion the chat completion object containing the conversation
     * @return the chat completion response
     */
    public ChatCompletionResp create(ChatCompletion chatCompletion) {
        return service.chatCompletion(chatCompletion).blockingGet();
    }
}
