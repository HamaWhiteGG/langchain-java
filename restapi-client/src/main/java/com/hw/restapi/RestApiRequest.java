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

package com.hw.restapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author artisan
 */
public class RestApiRequest {

    /**
     * Set of constant
     */
    protected String apiKeyName;

    /**
     * user secret API key
     */
    protected String restApiKey;

    /**
     * Current search engine
     */
    protected String apiPlateForm;

    /**
     * request parameters
     */
    public Map<String, String> parameter;

    /**
     * initialize gson
     */
    private static Gson gson = new Gson();

    /**
     * https search implementation for Java 7+
     */
    public RestApiHttpClient restApiHttpClient;

    /***
     * Constructor
     *
     * @param parameter user search
     * @param restApiKey secret user API key
     * @param apiKeyName specific api key name, api_key, key, secret_key
     */
    public RestApiRequest(Map<String, String> parameter, String restApiKey, String apiKeyName) {
        this.parameter = parameter;
        this.restApiKey = restApiKey;
        this.apiKeyName = apiKeyName;
    }

    /**
     * Constructor
     *
     * @param restApiKey secret API key
     * @param platform service like: public openapi, private openapi
     */
    public RestApiRequest(String restApiKey, String platform) {
        this.restApiKey = restApiKey;
        this.apiPlateForm = platform;
    }

    /***
     * Build a serp API query by expanding existing parameter
     *
     * @param path backend HTTP path
     * @param output type of output format (json, html, json_with_images)
     * @return format parameter hash map
     * @throws RestApiException wraps backend error message
     */
    public Map<String, String> buildQuery(String path, String output) throws RestApiException {
        // Initialize search if not done
        if (restApiHttpClient == null) {
            this.restApiHttpClient = new RestApiHttpClient(path);
            this.restApiHttpClient.setHttpConnectionTimeout(6000);
        } else {
            this.restApiHttpClient.path = path;
        }

        // Set current programming language
        this.parameter.put("source", "java");

        // Set api_key
        if (this.parameter.get(apiKeyName) == null) {
            if (this.restApiKey != null) {
                this.parameter.put(apiKeyName, this.restApiKey);
            } else if (getApiKeyFromEnv() != null) {
                this.parameter.put(apiKeyName, getApiKeyFromEnv());
            } else {
                throw new RestApiException(apiKeyName + " is not defined");
            }
        }

        this.parameter.put("plateForm", this.apiPlateForm);

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
     * Get JSON output
     *
     * @return JsonObject parent node
     * @throws RestApiException wraps backend error message
     */
    public JsonObject getJson() throws RestApiException {
        Map<String, String> query = buildQuery("/search", "json");
        return asJson(restApiHttpClient.getResults(query));
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
     * Get search result from the Search Archive API
     *
     * @param searchID archived search result = search_metadata.id
     * @return JsonObject search result
     * @throws RestApiException wraps backend error message
     */
    public JsonObject getSearchArchive(String searchID) throws RestApiException {
        Map<String, String> query = buildQuery("/searches/" + searchID + ".json", "json");
        query.remove("output");
        query.remove("q");
        return asJson(restApiHttpClient.getResults(query));
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }
}
