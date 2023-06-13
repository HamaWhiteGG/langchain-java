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

package com.hw.serpapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Due to <a href="https://github.com/serpapi/google-search-results-java/issues/22">Cant get this artifact from jitpack</a>, I decided to add the serpapi-client module myself.
 * <p>
 * SerpApiSearch wraps HTTP interaction with the service serpapi.com
 *
 * @author HamaWhite
 */
public class SerpApiSearch {

    /**
     * Set of constant
     */
    public static final String API_KEY_NAME = "api_key";

    /**
     * user secret API key
     */
    protected String serpapiApiKey;

    /**
     * Current search engine
     */
    protected String engine;

    /**
     * search parameters
     */
    public Map<String, String> parameter;

    /**
     * initialize gson
     */
    private static Gson gson = new Gson();

    /**
     * https search implementation for Java 7+
     */
    public SerpApiHttpClient search;

    /***
     * Constructor
     *
     * @param parameter user search
     * @param serpapiApiKey secret user API key
     * @param engine service like: google, naver, yahoo...
     */
    public SerpApiSearch(Map<String, String> parameter, String serpapiApiKey, String engine) {
        this.parameter = parameter;
        this.serpapiApiKey = serpapiApiKey;
        this.engine = engine;
    }

    /***
     * Constructor
     *
     * @param parameter user search
     * @param engine service like: google, yahoo, bing...
     */
    public SerpApiSearch(Map<String, String> parameter, String engine) {
        this.parameter = parameter;
        this.engine = engine;
    }

    /***
     * Constructor with no parameter
     * @param engine service like: google, bing, yahoo...
     */
    public SerpApiSearch(String engine) {
        this.parameter = new HashMap<String, String>();
        this.engine = engine;
    }

    /**
     * Constructor
     *
     * @param serpapiApiKey secret API key
     * @param engine service like: google, bing, yahoo...
     */
    public SerpApiSearch(String serpapiApiKey, String engine) {
        this.serpapiApiKey = serpapiApiKey;
        this.engine = engine;
    }

    /***
     * Build a serp API query by expanding existing parameter
     *
     * @param path backend HTTP path
     * @param output type of output format (json, html, json_with_images)
     * @return format parameter hash map
     * @throws SerpApiSearchException wraps backend error message
     */
    public Map<String, String> buildQuery(String path, String output) throws SerpApiSearchException {
        // Initialize search if not done
        if (search == null) {
            this.search = new SerpApiHttpClient(path);
            this.search.setHttpConnectionTimeout(6000);
        } else {
            this.search.path = path;
        }

        // Set current programming language
        this.parameter.put("source", "java");

        // Set api_key
        if (this.parameter.get(API_KEY_NAME) == null) {
            if (this.serpapiApiKey != null) {
                this.parameter.put(API_KEY_NAME, this.serpapiApiKey);
            } else if (getApiKeyFromEnv() != null) {
                this.parameter.put(API_KEY_NAME, getApiKeyFromEnv());
            } else {
                throw new SerpApiSearchException(API_KEY_NAME + " is not defined");
            }
        }

        this.parameter.put("engine", this.engine);

        // Set output format
        this.parameter.put("output", output);

        return this.parameter;
    }

    /**
     * @return current secret api key
     */
    public static String getApiKeyFromEnv() {
        return System.getenv("SERPAPI_API_KEY");
    }

    /***
     * Get HTML output
     *
     * @return raw HTML response from the search engine for custom parsing
     * @throws SerpApiSearchException wraps backend error message
     */
    public String getHtml() throws SerpApiSearchException {
        Map<String, String> query = buildQuery("/search", "html");
        return search.getResults(query);
    }

    /***
     * Get JSON output
     *
     * @return JsonObject parent node
     * @throws SerpApiSearchException wraps backend error message
     */
    public JsonObject getJson() throws SerpApiSearchException {
        Map<String, String> query = buildQuery("/search", "json");
        return asJson(search.getResults(query));
    }

    /***
     * Convert HTTP content to JsonValue
     *
     * @param content raw JSON HTTP response
     * @return JsonObject created by gson parser
     */
    public JsonObject asJson(String content) {
        JsonElement element = gson.fromJson(content, JsonElement.class);
        return element.getAsJsonObject();
    }

    /***
     * @return http search
     */
    public SerpApiHttpClient getClient() {
        return this.search;
    }

    /***
     * Get location
     *
     * @param q     query
     * @param limit number of location
     * @return JsonObject location using Location API
     * @throws SerpApiSearchException wraps backend error message
     */
    public JsonArray getLocation(String q, Integer limit) throws SerpApiSearchException {
        Map<String, String> query = buildQuery("/locations.json", "json");
        query.remove("output");
        query.remove(API_KEY_NAME);
        query.put("q", q);
        query.put("limit", limit.toString());
        String s = search.getResults(query);
        return gson.fromJson(s, JsonArray.class);
    }

    /***
     * Get search result from the Search Archive API
     *
     * @param searchID archived search result = search_metadata.id
     * @return JsonObject search result
     * @throws SerpApiSearchException wraps backend error message
     */
    public JsonObject getSearchArchive(String searchID) throws SerpApiSearchException {
        Map<String, String> query = buildQuery("/searches/" + searchID + ".json", "json");
        query.remove("output");
        query.remove("q");
        return asJson(search.getResults(query));
    }

    /***
     * Get account information using Account API
     *
     * @return JsonObject account information
     * @throws SerpApiSearchException wraps backend error message
     */
    public JsonObject getAccount() throws SerpApiSearchException {
        Map<String, String> query = buildQuery("/account", "json");
        query.remove("output");
        query.remove("q");
        return asJson(search.getResults(query));
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }
}
