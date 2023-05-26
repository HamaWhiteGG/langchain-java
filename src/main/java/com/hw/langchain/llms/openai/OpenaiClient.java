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

import com.hw.langchain.llms.openai.entity.request.Completion;
import com.hw.langchain.llms.openai.service.OpenaiService;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.Data;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.Proxy;

/**
 * @description: OpenaiClient
 * @author: HamaWhite
 */
@Data
@Builder
public class OpenaiClient {

    private static final String BASE_URL = "https://api.openai.com/";

    private String apiKey;

    private Proxy proxy;

    private OpenaiService service;

    public OpenaiClient init() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.addInterceptor(new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey);
                Request newRequest = requestBuilder.build();
                return chain.proceed(newRequest);
            }
        });

        if (proxy != null) {
            httpClientBuilder.proxy(proxy);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build();

        this.service = retrofit.create(OpenaiService.class);
        return this;
    }

    public String completion(Completion completion) {
        String text = service.completion(completion).blockingGet().getChoices().get(0).getText();
        return StringUtils.trim(text);
    }
}
