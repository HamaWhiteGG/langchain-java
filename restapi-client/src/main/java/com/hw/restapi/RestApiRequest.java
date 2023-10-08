/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hw.restapi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author artisan
 */
public abstract class RestApiRequest {

    /**
     * user secret API key
     */
    private String restApiKey;

    /**
     * request parameters
     */
    private Map<String, Object> parameter;

    /**
     * initialize gson
     */
    private static Gson gson = new Gson();

    /**
     * https search implementation for Java 7+
     */
    private RestApiHttpClient restApiHttpClient;

    protected abstract Map<String, Object> getRequestProperty();

    protected abstract String getApiKeyName();

    protected abstract String getRestBaseUrl();

    /***
     * Constructor
     *
     * @param parameter user search
     * @param restApiKey secret user API key
     */
    public RestApiRequest(Map<String, Object> parameter, String restApiKey) {
        this.parameter = parameter;
        this.restApiKey = restApiKey;
    }

    /***
     * Build a serp API query by expanding existing parameter
     *
     * @param path backend HTTP path
     * @return format parameter hash map
     * @throws RestApiException wraps backend error message
     */
    public Map<String, Object> buildQuery(String path) throws RestApiException {
        if (restApiHttpClient == null) {
            this.restApiHttpClient = new RestApiHttpClient(getRestBaseUrl(), path);
            this.restApiHttpClient.setHttpConnectionTimeout(15000);
            this.restApiHttpClient.setHttpReadTimeout(5000);
            this.restApiHttpClient.setRequestProperty(getRequestProperty());
        } else {
            this.restApiHttpClient.path = path;
        }

        String apiKeyName = getApiKeyName();
        // Set api_key
        if (this.parameter.get(apiKeyName) == null) {
            if (this.restApiKey != null) {
                this.parameter.put(apiKeyName, this.restApiKey);
            } else {
                throw new RestApiException(apiKeyName + " is not defined");
            }
        }

        return this.parameter;
    }

    /***
     * Get JSON output
     *
     * @return JsonObject parent node
     * @throws RestApiException wraps backend error message
     */
    public JsonObject getResultByPath(String path) throws RestApiException {
        Map<String, Object> query = buildQuery(path);
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
        return element.getAsJsonObject().getAsJsonObject("result");
    }

    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }
}
