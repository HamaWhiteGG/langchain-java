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
import com.google.gson.JsonObject;
import org.apache.commons.collections4.MapUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Open API for RestfulAPI Request
 *
 * @author Artisan
 */
public class RestApiHttpClient {

    /**
     * can config
     */
    private int httpConnectionTimeout;

    /**
     * can config by lion
     */
    private int httpReadTimeout;

    /**
     * can config
     */
    private Map<String, Object> httpRequestProperty;

    /**
     * initialize gson
     */
    private static Gson gson = new Gson();

    /**
     * http rest backend url
     */
    private String restBaseUrl;

    /**
     * current backend HTTP path
     */
    public String path;

    /***
     * Constructor
     *
     * @param restBaseUrl
     * @param path HTTP url path
     */
    public RestApiHttpClient(String restBaseUrl, String path) {
        this.restBaseUrl = restBaseUrl;
        this.path = path;
    }

    /***
     * Build URL
     *
     * @param path url end point
     * @param parameter search parameter map like: { "city": "nanjing", "key": "3kdskndglaskdjg"}
     * @return httpUrlConnection
     * @throws RestApiException wraps error message
     */
    protected HttpURLConnection buildConnection(String path, Map<String, Object> parameter)
            throws RestApiException {
        HttpURLConnection con;
        try {
            String query = ParameterStringBuilder.getParamsString(parameter);
            URL url = new URL(restBaseUrl + path + "?" + query);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(getHttpConnectionTimeout());
            con.setReadTimeout(getHttpReadTimeout());
            if (MapUtils.isNotEmpty(httpRequestProperty)) {
                for (String key : httpRequestProperty.keySet()) {
                    con.setRequestProperty(key, httpRequestProperty.get(key).toString());
                }
            }
            con.connect();
        } catch (IOException e) {
            throw new RestApiException(e);
        }

        return con;
    }

    /***
     * Get results
     *
     * @param parameter user search parameters
     * @return http response body
     * @throws RestApiException wraps error message
     */
    public String getResults(Map<String, Object> parameter) throws RestApiException {
        HttpURLConnection con = buildConnection(this.path, parameter);

        // Get HTTP status
        int statusCode = -1;
        // Hold response stream
        InputStream is = null;
        // Read buffer
        BufferedReader in = null;
        try {
            statusCode = con.getResponseCode();

            if (statusCode == 200) {
                is = con.getInputStream();
            } else {
                is = con.getErrorStream();
            }

            Reader reader = new InputStreamReader(is);
            in = new BufferedReader(reader);
        } catch (IOException e) {
            throw new RestApiException(e);
        }

        String inputLine;
        StringBuffer content = new StringBuffer();
        try {
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new RestApiException(e);
        }

        // Disconnect
        con.disconnect();

        if (statusCode != 200) {
            triggerSerpApiClientException(content.toString());
        }
        return content.toString();
    }

    /**
     * trigger a exception on error
     *
     * @param content raw JSON response from serpapi.com
     * @throws RestApiException wraps error message
     */
    protected void triggerSerpApiClientException(String content) throws RestApiException {
        String errorMessage;
        try {
            JsonObject element = gson.fromJson(content, JsonObject.class);
            errorMessage = element.get("error").getAsString();
        } catch (Exception e) {
            throw new AssertionError("invalid response format: " + content);
        }
        throw new RestApiException(errorMessage);
    }

    /**
     * @return current HTTP connection timeout
     */
    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    /**
     * @param httpConnectionTimeout set HTTP connection timeout
     */
    public void setHttpConnectionTimeout(int httpConnectionTimeout) {
        this.httpConnectionTimeout = httpConnectionTimeout;
    }

    /**
     * @return current HTTP read timeout
     */
    public int getHttpReadTimeout() {
        return httpReadTimeout;
    }

    /**
     * @param httpReadTimeout set HTTP read timeout
     */
    public void setHttpReadTimeout(int httpReadTimeout) {
        this.httpReadTimeout = httpReadTimeout;
    }

    /**
     * header request body
     *
     * @param requestProperty
     */
    public void setRequestProperty(Map<String, Object> requestProperty) {
        this.httpRequestProperty = requestProperty;
    }
}
