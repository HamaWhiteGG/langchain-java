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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.openai.common.OpenAiError;
import com.hw.openai.common.OpenaiApiType;
import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.ChatCompletionChunk;
import com.hw.openai.entity.chat.ChatCompletionResp;
import com.hw.openai.entity.completions.Completion;
import com.hw.openai.entity.completions.CompletionChunk;
import com.hw.openai.entity.completions.CompletionResp;
import com.hw.openai.entity.embeddings.Embedding;
import com.hw.openai.entity.embeddings.EmbeddingResp;
import com.hw.openai.entity.models.Model;
import com.hw.openai.entity.models.ModelResp;
import com.hw.openai.exception.OpenAiException;
import com.hw.openai.service.OpenAiService;
import com.hw.openai.stream.ResponseBodyCallback;
import com.hw.openai.stream.SSE;
import com.hw.openai.utils.ProxyUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import lombok.Builder;
import lombok.Data;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Represents a client for interacting with the OpenAI API.
 *
 * @author HamaWhite
 */
@Data
@Builder
public class OpenAiClient implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAiClient.class);

    private String openaiApiBase;

    private String openaiApiKey;

    @Builder.Default
    private OpenaiApiType openaiApiType = OpenaiApiType.OPENAI;

    private String openaiApiVersion;

    private String openaiOrganization;

    private String openaiProxy;

    /**
     * the username for proxy authentication (optional)
     */
    private String proxyUsername;

    /**
     * the password for proxy authentication (optional)
     */
    private String proxyPassword;

    /**
     * Timeout for requests to OpenAI completion API. Default is 16 seconds.
     */
    @Builder.Default
    protected long requestTimeout = 16;

    private List<Interceptor> interceptorList;

    private OpenAiService service;

    private OkHttpClient httpClient;

    private ObjectMapper objectMapper;

    /**
     * Initializes the OpenAiClient instance.
     *
     * @return the initialized OpenAiClient instance
     */
    public OpenAiClient init() {
        initializeOpenaiApiBase();
        openaiProxy = getOrEnvOrDefault(openaiProxy, "OPENAI_PROXY");

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(requestTimeout, TimeUnit.SECONDS)
                .readTimeout(requestTimeout, TimeUnit.SECONDS)
                .writeTimeout(requestTimeout, TimeUnit.SECONDS)
                .callTimeout(requestTimeout, TimeUnit.SECONDS);

        httpClientBuilder.addInterceptor(chain -> {
            // If openaiApiKey is not set, read the value of OPENAI_API_KEY from the environment.
            openaiApiKey = getOrEnvOrDefault(openaiApiKey, "OPENAI_API_KEY");
            openaiOrganization = getOrEnvOrDefault(openaiOrganization, "OPENAI_ORGANIZATION", "");

            Request.Builder requestBuilder = chain.request().newBuilder();
            requestBuilder.header("Content-Type", "application/json");
            if (isAzureApiType()) {
                requestBuilder.header("api-key", openaiApiKey);
            } else {
                requestBuilder.header("Authorization", "Bearer " + openaiApiKey);
                requestBuilder.header("OpenAI-Organization", openaiOrganization);
            }
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        // Add HttpLogging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(LOG::debug);
        // Note that setting it to Level.BODY will block the OpenAI stream output.
        loggingInterceptor.setLevel(
                LOG.isDebugEnabled() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
        httpClientBuilder.addInterceptor(loggingInterceptor);
        if (this.interceptorList != null) {
            this.interceptorList.forEach(httpClientBuilder::addInterceptor);
        }

        if (StringUtils.isNotEmpty(openaiProxy)) {
            httpClientBuilder.proxy(ProxyUtils.http(openaiProxy, proxyUsername, proxyPassword));
        }
        httpClient = httpClientBuilder.build();

        if (objectMapper == null) {
            objectMapper = defaultObjectMapper();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(openaiApiBase)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClient)
                .build();

        this.service = retrofit.create(OpenAiService.class);
        return this;
    }

    private void initializeOpenaiApiBase() {
        if (isAzureApiType()) {
            openaiApiBase = getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE");
            if (openaiApiBase == null) {
                throw new NullPointerException(
                        "Did not find OPENAI_API_BASE, please add an environment variable `OPENAI_API_BASE` which contains it, or pass `OPENAI_API_BASE` as a named parameter.");
            }
            openaiApiBase = StringUtils.appendIfMissing(openaiApiBase, "/") + "openai/deployments/";
        } else {
            openaiApiBase = getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE", "https://api.openai.com/v1/");
        }
    }

    public static ObjectMapper defaultObjectMapper() {
        // Used for automatic discovery and registration of Jackson modules
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        // Ignore unknown fields
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
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
        return execute(service.listModels());
    }

    /**
     * Retrieves a model instance, providing basic information about the model such as the owner and permissions.
     *
     * @param model the ID of the model to retrieve
     * @return the retrieved model
     */
    public Model retrieveModel(String model) {
        return execute(service.retrieveModel(model));
    }

    /**
     * Creates a completion for the provided prompt and parameters.
     *
     * @param completion the completion object containing the prompt and parameters
     * @return the generated completion text
     */
    public String completion(Completion completion) {
        CompletionResp response = createCompletion(completion);
        String text = response.getChoices().get(0).getText();
        return StringUtils.trim(text);
    }

    /**
     * Creates a completion for the provided prompt and parameters.
     *
     * @param completion the completion object containing the prompt and parameters
     * @return the completion response
     */
    public CompletionResp createCompletion(Completion completion) {
        return isAzureApiType()
                ? execute(service.createCompletion(completion.getModel(), openaiApiVersion, completion))
                : execute(service.createCompletion(completion));
    }

    /**
     * Creates a stream completion for the provided prompt and parameters.
     *
     * @param completion the completion request object containing the prompt and parameters
     * @return a stream of generated completions
     */
    public Flowable<CompletionChunk> streamCompletion(Completion completion) {
        completion.setStream(true);
        return stream(service.streamCompletion(completion), CompletionChunk.class);
    }

    /**
     * Creates a model response for the given chat conversation.
     *
     * @param chatCompletion the chat completion object containing the conversation
     * @return the generated model response text
     */
    public String chatCompletion(ChatCompletion chatCompletion) {
        ChatCompletionResp response = createChatCompletion(chatCompletion);
        String content = response.getChoices().get(0).getMessage().getContent();
        return StringUtils.trim(content);
    }

    /**
     * Creates a model response for the given chat conversation.
     *
     * @param chatCompletion the chat completion object containing the conversation
     * @return the chat completion response
     */
    public ChatCompletionResp createChatCompletion(ChatCompletion chatCompletion) {
        return isAzureApiType()
                ? execute(service.createChatCompletion(chatCompletion.getModel(), openaiApiVersion, chatCompletion))
                : execute(service.createChatCompletion(chatCompletion));
    }

    /**
     * Creates a stream response for the given chat conversation.
     *
     * @param chatCompletion the chat completion request object
     * @return a stream of generated chat completions
     */
    public Flowable<ChatCompletionChunk> streamChatCompletion(ChatCompletion chatCompletion) {
        chatCompletion.setStream(true);
        return stream(service.streamChatCompletion(chatCompletion), ChatCompletionChunk.class);
    }

    /**
     * Creates an embedding vector representing the input text.
     *
     * @param embedding The Embedding object containing the input text.
     * @return The embedding vector response.
     */
    public EmbeddingResp createEmbedding(Embedding embedding) {
        return isAzureApiType()
                ? execute(service.createEmbedding(embedding.getModel(), openaiApiVersion, embedding))
                : execute(service.createEmbedding(embedding));
    }

    /**
     * Checks if the Openai API type matches Azure or Azure AD.
     *
     * @return true if the API type is Azure or Azure AD, otherwise false
     */
    private boolean isAzureApiType() {
        return EnumSet.of(OpenaiApiType.AZURE, OpenaiApiType.AZURE_AD).contains(openaiApiType);
    }

    /**
     * Calls the Open AI api and returns a Flowable of SSE for streaming omitting the last message.
     *
     * @param apiCall The api call
     */
    private Flowable<SSE> stream(Call<ResponseBody> apiCall) {
        return stream(apiCall, false);
    }

    /**
     * Calls the Open AI api and returns a Flowable of SSE for streaming.
     *
     * @param apiCall  The api call
     * @param emitDone If true the last message ([DONE]) is emitted
     */
    public Flowable<SSE> stream(Call<ResponseBody> apiCall, boolean emitDone) {
        return Flowable.create(emitter -> apiCall.enqueue(new ResponseBodyCallback(emitter, emitDone, objectMapper)),
                BackpressureStrategy.BUFFER);
    }

    /**
     * Calls the Open AI api and returns a Flowable of type T for streaming
     * omitting the last message.
     *
     * @param apiCall The api call
     * @param clazz   Class of type T to return
     */
    private <T> Flowable<T> stream(Call<ResponseBody> apiCall, Class<T> clazz) {
        return stream(apiCall).map(sse -> objectMapper.readValue(sse.getData(), clazz));
    }

    /**
     * Calls the Open AI api, returns the response, and parses error messages if the request fails
     */
    public <T> T execute(Single<T> apiCall) {
        try {
            return apiCall.blockingGet();
        } catch (HttpException e) {
            try {
                if (e.response() != null) {
                    try (ResponseBody responseBody = requireNonNull(e.response()).errorBody()) {
                        if (responseBody != null) {
                            String errorBody = responseBody.string();
                            OpenAiError error = objectMapper.readValue(errorBody, OpenAiError.class);
                            throw new OpenAiException(error, e, e.code());
                        }
                    }
                }
                throw e;
            } catch (IOException ex) {
                // couldn't parse OpenAI error
                throw e;
            }
        }
    }

    /**
     * Closes the HttpClient connection pool.
     */
    @Override
    public void close() {
        // Cancel all ongoing requests
        httpClient.dispatcher().cancelAll();

        // Shut down the connection pool (if any)
        httpClient.connectionPool().evictAll();
        httpClient.dispatcher().executorService().shutdown();
    }
}
