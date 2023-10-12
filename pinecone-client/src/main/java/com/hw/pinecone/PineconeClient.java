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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.pinecone.entity.index.CreateIndexRequest;
import com.hw.pinecone.entity.index.IndexDescription;
import com.hw.pinecone.service.IndexService;
import com.hw.pinecone.service.VectorService;

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

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Because the <a href="https://github.com/pinecone-io/pinecone-java-client">pinecone-java-client</a> does not support managing Pinecone services,
 * only reading and writing from existing indices. Therefore, this needs to be rewritten.
 * <p>
 * <a href="https://docs.pinecone.io/reference">pinecone-api-doc</a>
 *
 * @author HamaWhite
 */
@Data
@Builder
public class PineconeClient implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PineconeClient.class);

    private String pineconeApiKey;

    @Builder.Default
    private String host = "https://controller.%s.pinecone.io";

    private String pineconeEnv;

    private String projectName;

    /**
     * Default is 16 seconds.
     */
    @Builder.Default
    protected long requestTimeout = 16;

    private OkHttpClient httpClient;

    private IndexService indexService;

    /**
     * Initializes the PineconeClient instance.
     *
     * @return the initialized PineconeClient instance
     */
    public PineconeClient init() {
        // If pineconeEnv is not set, read the value of PINECONE_ENV from the environment.
        pineconeEnv = getOrFromEnv(pineconeEnv, "PINECONE_ENV");

        Retrofit retrofit = createRetrofit(String.format(host, pineconeEnv));
        this.indexService = retrofit.create(IndexService.class);
        return this;
    }

    public IndexClient indexClient(String name) {
        String baseUrl = "https://" + describeIndex(name).getStatus().getHost();
        Retrofit retrofit = createRetrofit(baseUrl);
        return new IndexClient(retrofit.create(VectorService.class));
    }

    /**
     * Initializes the PineconeClient instance.
     *
     * @return the initialized PineconeClient instance
     */
    public Retrofit createRetrofit(String baseUrl) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(requestTimeout, TimeUnit.SECONDS)
                .readTimeout(requestTimeout, TimeUnit.SECONDS)
                .writeTimeout(requestTimeout, TimeUnit.SECONDS)
                .callTimeout(requestTimeout, TimeUnit.SECONDS);

        httpClientBuilder.addInterceptor(chain -> {
            // If pineconeApiKey is not set, read the value of PINECONE_API_KEY from the environment.
            pineconeApiKey = getOrFromEnv(pineconeApiKey, "PINECONE_API_KEY");

            Request request = chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Api-Key", pineconeApiKey)
                    .build();

            return chain.proceed(request);
        });

        // Add HttpLogging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(LOG::debug);
        loggingInterceptor.setLevel(
                LOG.isDebugEnabled() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        httpClient = httpClientBuilder.build();

        // Used for automatic discovery and registration of Jackson modules
        ObjectMapper objectMapper = defaultObjectMapper();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClient)
                .build();
    }

    public static ObjectMapper defaultObjectMapper() {
        // Used for automatic discovery and registration of Jackson modules
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        // Ignore unknown fields
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
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

    private String getOrFromEnv(String originalValue, String envKey) {
        if (StringUtils.isNotEmpty(originalValue)) {
            return originalValue;
        }
        return System.getenv(envKey);
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
     * @param request create index request
     */
    public void createIndex(CreateIndexRequest request) {
        indexService.createIndex(request).blockingGet();
    }

    /**
     * Get a description of an index.
     *
     * @param name the name of the index
     * @return a description of the index
     */
    public IndexDescription describeIndex(String name) {
        return indexService.describeIndex(name).blockingGet();
    }

    /**
     * This operation deletes an existing index.
     *
     * @param name the name of the index
     */
    public void deleteIndex(String name) {
        indexService.deleteIndex(name).blockingGet();
    }
}
