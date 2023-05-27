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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.langchain.llms.openai.entity.chat.ChatCompletion;
import com.hw.langchain.llms.openai.entity.chat.ChatCompletionResp;
import com.hw.langchain.llms.openai.entity.completions.Completion;
import com.hw.langchain.llms.openai.entity.completions.CompletionResp;
import com.hw.langchain.llms.openai.service.OpenAIService;

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

import java.net.Proxy;

/**
 * @description: OpenaiClient
 * @author: HamaWhite
 */
@Data
@Builder
public class OpenAI {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAI.class);

    private static final String BASE_URL = "https://api.openai.com/";

    private String openaiApiKey;

    private Proxy proxy;

    private OpenAIService service;

    public OpenAI init() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.addInterceptor(chain -> {
            Request originalRequest = chain.request();

            // If openaiApiKey is not set, read the value of OPENAI_API_KEY from the environment.
            if (StringUtils.isBlank(openaiApiKey)) {
                openaiApiKey = System.getenv("OPENAI_API_KEY");
            }
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openaiApiKey);
            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        });

        // Add HttpLogging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(LOG::debug);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        if (proxy != null) {
            httpClientBuilder.proxy(proxy);
        }

        // Used for automatic discovery and registration of Jackson modules
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClientBuilder.build())
                .build();

        this.service = retrofit.create(OpenAIService.class);
        return this;
    }

    public String completion(Completion completion) {
        CompletionResp response = service.completion(completion).blockingGet();

        String text = response.getChoices().get(0).getText();
        return StringUtils.trim(text);
    }

    public String chatCompletion(ChatCompletion chatCompletion) {
        ChatCompletionResp response = service.chatCompletion(chatCompletion).blockingGet();

        String content = response.getChoices().get(0).getMessage().getContent();
        return StringUtils.trim(content);
    }
}
