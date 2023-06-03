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

package com.hw.openai.service;

import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.ChatCompletionResp;
import com.hw.openai.entity.completions.Completion;
import com.hw.openai.entity.completions.CompletionResp;
import com.hw.openai.entity.models.Model;
import com.hw.openai.entity.models.ModelResp;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @description: Service interface for interacting with the OpenAI API.
 * @author: HamaWhite
 */
public interface OpenAiService {

    /**
     * Lists the currently available models, and provides basic information about each one
     * such as the owner and availability.
     *
     * @return a Single emitting the response containing the list of available models
     */
    @GET("models")
    Single<ModelResp> listModels();

    /**
     * Retrieves a model instance, providing basic information about the model such as the owner and permissions.
     *
     * @param model the ID of the model to use for this request
     * @return a Single emitting the response containing the retrieved model
     */
    @GET("models/{model}")
    Single<Model> retrieveModel(@Path("model") String model);

    /**
     * Creates a completion for the provided prompt and parameters.
     *
     * @param completion the completion request object containing the prompt and parameters
     * @return a Single emitting the response containing the completion result
     */
    @POST("completions")
    Single<CompletionResp> completion(@Body Completion completion);

    /**
     * Creates a model response for the given chat conversation.
     *
     * @param chatCompletion the chat completion request object containing the chat conversation
     * @return a Single emitting the response containing the chat completion result
    
     */
    @POST("chat/completions")
    Single<ChatCompletionResp> chatCompletion(@Body ChatCompletion chatCompletion);
}
