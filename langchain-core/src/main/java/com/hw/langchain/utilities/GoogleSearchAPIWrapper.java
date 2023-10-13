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

package com.hw.langchain.utilities;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.customsearch.v1.CustomSearchAPI;
import com.google.api.services.customsearch.v1.CustomSearchAPIRequestInitializer;
import com.google.api.services.customsearch.v1.model.Result;
import com.google.common.collect.Maps;

import org.apache.commons.collections4.CollectionUtils;

import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hw.langchain.utils.Utils.getOrEnvOrDefault;

/**
 * Wrapper for Google Search API.
 *
 * <ol>
 *   <li>Create an API key</li>
 *   <li>Setup Custom Search Engine to search the entire web</li>
 *   <li>Enable the Custom Search API</li>
 * </ol>
 *
 * @author HamaWhite
 */
public class GoogleSearchAPIWrapper {

    private final CustomSearchAPI customSearch;

    private final String googleCseId;

    /**
     * Number of search results to return.
     */
    private final int num;

    @SneakyThrows
    private GoogleSearchAPIWrapper(Builder builder) {
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        HttpRequestInitializer httpRequestInitializer = request -> {
            request.setConnectTimeout(builder.connectTimeout);
            request.setReadTimeout(builder.readTimeout);
        };
        String googleApiKey = getOrEnvOrDefault(builder.googleApiKey, "GOOGLE_API_KEY");

        this.customSearch = new CustomSearchAPI.Builder(transport, new GsonFactory(), httpRequestInitializer)
                .setApplicationName("Google Custom Search")
                .setGoogleClientRequestInitializer(new CustomSearchAPIRequestInitializer(googleApiKey))
                .build();

        this.googleCseId = getOrEnvOrDefault(builder.googleCseId, "GOOGLE_CSE_ID");
        this.num = builder.num;
    }

    @SneakyThrows
    private List<Result> googleSearchResults(String query, int num) {
        return customSearch.cse()
                .list()
                .setCx(googleCseId)
                .setQ(query)
                .setNum(num)
                .execute()
                .getItems();
    }

    /**
     * Run a query through Google Search and parse the results.
     *
     * @param query The search query to be executed.
     * @return A string containing snippets from the Google Search results, joined with spaces.
     */
    public String run(String query) {
        List<Result> results = googleSearchResults(query, num);
        if (CollectionUtils.isEmpty(results)) {
            return "No good Google Search Result was found";
        }
        return results.stream()
                .map(Result::getSnippet)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    /**
     * Run query through GoogleSearch and return metadata.
     *
     * @param query The query to search for.
     * @param num   The number of results to return.
     * @return A list of dictionaries with the following keys:
     * <ul>
     *     <li>snippet - The description of the result.</li>
     *     <li>title - The title of the result.</li>
     *     <li>link - The link to the result.</li>
     * </ul>
     */
    public List<Map<String, String>> results(String query, int num) {
        List<Result> results = googleSearchResults(query, num);

        if (CollectionUtils.isEmpty(results)) {
            return ListUtil.of(MapUtil.of("Result", "No good Google Search Result was found"));
        }
        return results.stream().map(result -> {
            Map<String, String> metadataResult = Maps.newHashMap();
            metadataResult.put("title", result.getTitle());
            metadataResult.put("link", result.getLink());

            if (result.getSnippet() != null) {
                metadataResult.put("snippet", result.getSnippet());
            }
            return metadataResult;
        }).collect(Collectors.toList());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        /**
         * Configure or set the environment variable GOOGLE_API_KEY.
         */
        private String googleApiKey;

        /**
         * Configure or set the environment variable GOOGLE_CSE_ID.
         */
        private String googleCseId;

        /**
         * Number of search results to return.
         */
        private int num = 10;

        /**
         * Timeout in milliseconds to establish a connection or {@code 0} for an infinite timeout.
         */
        private int connectTimeout = 20 * 1000;

        /**
         * Timeout in milliseconds to read data from an established connection or {@code 0} for an infinite timeout.
         */
        private int readTimeout = 20 * 1000;

        private Builder() {
        }

        public Builder googleApiKey(String googleApiKey) {
            this.googleApiKey = googleApiKey;
            return this;
        }

        public Builder googleCseId(String googleCseId) {
            this.googleCseId = googleCseId;
            return this;
        }

        public Builder num(int num) {
            this.num = num;
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public GoogleSearchAPIWrapper build() {
            return new GoogleSearchAPIWrapper(this);
        }
    }
}
