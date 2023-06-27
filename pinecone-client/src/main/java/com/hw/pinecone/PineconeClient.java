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

package com.hw.pinecone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.pinecone.entity.index.CreateIndexCmd;
import com.hw.pinecone.service.IndexService;
import com.hw.pinecone.service.VectorService;

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

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Because the <a href="https://github.com/pinecone-io/pinecone-java-client">pinecone-java-client</a> does not support managing Pinecone services,
 * only reading and writing from existing indices. Therefore, this needs to be rewritten.
 *
 * @author HamaWhite
 */
@Data
@Builder
public class PineconeClient {

    private static final Logger LOG = LoggerFactory.getLogger(PineconeClient.class);

    private String apiKey;

    @Builder.Default
    private String host = "https://controller.%s.pinecone.io/";

    private String environment;

    private String projectName;

    /**
     * Default is 10 seconds.
     */
    @Builder.Default
    protected long requestTimeout = 10;

    private IndexService indexService;

    private VectorService vectorService;

    private OkHttpClient httpClient;

    /**
     * Initializes the PineconeClient instance.
     *
     * @return the initialized PineconeClient instance
     */
    public PineconeClient init() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(requestTimeout, TimeUnit.SECONDS)
                .readTimeout(requestTimeout, TimeUnit.SECONDS)
                .writeTimeout(requestTimeout, TimeUnit.SECONDS)
                .callTimeout(requestTimeout, TimeUnit.SECONDS);

        httpClientBuilder.addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Api-Key", apiKey)
                    .build();

            return chain.proceed(request);
        });

        // Add HttpLogging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(LOG::debug);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        httpClient = httpClientBuilder.build();

        // Used for automatic discovery and registration of Jackson modules
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.format(host, environment))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClient)
                .build();

        this.indexService = retrofit.create(IndexService.class);
        this.vectorService = retrofit.create(VectorService.class);
        return this;
    }

    /**
     * Closes the HttpClient connection pool.
     */
    public void close() {
        // Cancel all ongoing requests
        httpClient.dispatcher().cancelAll();

        // Shut down the connection pool (if any)
        httpClient.connectionPool().evictAll();
        httpClient.dispatcher().executorService().shutdown();
    }

    /**
     * This operation returns a list of your Pinecone indexes.
     *
     * @return a list of strings representing the Pinecone indexes.
     */
    public List<String> listIndexes() {
        return indexService.listIndexes().blockingGet();
    }

    /**
     * This operation creates a Pinecone index.
     *
     * @param command create index command
     */
    public void createIndex(CreateIndexCmd command) {
        indexService.createIndex(command).subscribe();
    }
}
