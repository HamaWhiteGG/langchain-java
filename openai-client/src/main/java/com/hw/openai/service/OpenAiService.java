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
import com.hw.openai.entity.embeddings.Embedding;
import com.hw.openai.entity.embeddings.EmbeddingResp;
import com.hw.openai.entity.image.CreateImageReq;
import com.hw.openai.entity.image.ImageResp;
import com.hw.openai.entity.models.Model;
import com.hw.openai.entity.models.ModelResp;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Service interface for interacting with the OpenAI API.
 *
 * @author HamaWhite
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
    Single<CompletionResp> createCompletion(@Body Completion completion);

    /**
     * Creates a completion for the provided prompt and parameters, using azure openai.
     *
     * @param deploymentId The deploymentId for azure openai url.
     * @param apiVersion   The apiVersion for azure openai url parameter 'api-version'.
     * @param completion   the completion request object containing the prompt and parameters
     * @return a Single emitting the response containing the completion result
     */
    @POST("{deploymentId}/completions")
    Single<CompletionResp> createCompletion(@Path("deploymentId") String deploymentId,
            @Query("api-version") String apiVersion,
            @Body Completion completion);

    /**
     * Creates a stream completion for the provided prompt and parameters.
     *
     * @param completion the completion request object containing the prompt and parameters
     * @return a Single emitting the response containing the completion result
     */
    @Streaming
    @POST("completions")
    Call<ResponseBody> streamCompletion(@Body Completion completion);

    /**
     * Creates a response for the given chat conversation.
     *
     * @param chatCompletion the chat completion request object containing the chat conversation
     * @return a Single emitting the response containing the chat completion result
     */
    @POST("chat/completions")
    Single<ChatCompletionResp> createChatCompletion(@Body ChatCompletion chatCompletion);

    /**
     * Creates a model response for the given chat conversation, using azure openai.
     *
     * @param deploymentId   The deploymentId for azure openai url.
     * @param apiVersion     The apiVersion for azure openai url parameter 'api-version'.
     * @param chatCompletion the chat completion request object containing the chat conversation
     * @return a Single emitting the response containing the chat completion result
     */
    @POST("{deploymentId}/chat/completions")
    Single<ChatCompletionResp> createChatCompletion(@Path("deploymentId") String deploymentId,
            @Query("api-version") String apiVersion, @Body ChatCompletion chatCompletion);

    /**
     * Creates a stream response for the given chat conversation.
     *
     * @param chatCompletion the chat completion request object
     * @return a {@link Call} representing the streaming response
     */
    @Streaming
    @POST("chat/completions")
    Call<ResponseBody> streamChatCompletion(@Body ChatCompletion chatCompletion);

    /**
     * Creates an embedding vector representing the input text.
     *
     * @param embedding The Embedding object containing the input text.
     * @return A Single object that emits an EmbeddingResp, representing the response containing the embedding vector.
     */
    @POST("embeddings")
    Single<EmbeddingResp> createEmbedding(@Body Embedding embedding);

    /**
     * Creates an embedding vector representing the input text, using azure openai.
     *
     * @param deploymentId The deploymentId for azure openai url.
     * @param apiVersion   The apiVersion for azure openai url parameter 'api-version'.
     * @param embedding    The Embedding object containing the input text.
     * @return A Single object that emits an EmbeddingResp, representing the response containing the embedding vector.
     */
    @POST("{deploymentId}/embeddings")
    Single<EmbeddingResp> createEmbedding(@Path("deploymentId") String deploymentId,
            @Query("api-version") String apiVersion,
            @Body Embedding embedding);

    /**
     * Creates an image given a prompt.
     * @param request the create image request
     * @return A Single emitting the response containing the generated image.
     */
    @POST("images/generations")
    Single<ImageResp> createImage(@Body CreateImageReq request);
}
