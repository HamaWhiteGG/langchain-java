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

package com.hw.langchain.requests;

import com.hw.langchain.exception.LangChainException;

import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * Lightweight wrapper around requests library.
 * The main purpose of this wrapper is to always return a text output.
 *
 * @author HamaWhite
 */
public class TextRequestsWrapper {

    private final Map<String, String> headers;

    public TextRequestsWrapper(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Performs an HTTP request using the provided `Requests` instance.
     *
     * @param requests The `Requests` instance to use for sending the request
     * @param url      The URL to send the request to
     * @param method   The HTTP method to use (e.g., "GET", "POST")
     * @param data     The data to send in the request body (can be null)
     * @return The response body as a string, or null if the response body is empty
     * @throws LangChainException If an error occurs while performing the request
     */
    private String performRequest(Requests requests, String url, String method, Map<String, Object> data) {
        try (Response response = requests.sendRequest(url, method, data)) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                return responseBody != null ? responseBody.string() : null;
            } else {
                throw new LangChainException("Failed with status code %d. messages: %s", response.code(),
                        response.message());
            }
        } catch (IOException e) {
            throw new LangChainException("An error occurred while performing " + method + " request.", e);
        }
    }

    public String get(String url) {
        Requests requests = getRequests();
        return performRequest(requests, url, "GET", null);
    }

    public String post(String url, Map<String, Object> data) {
        Requests requests = getRequests();
        return performRequest(requests, url, "POST", data);
    }

    public String patch(String url, Map<String, Object> data) {
        Requests requests = getRequests();
        return performRequest(requests, url, "PATCH", data);
    }

    public String put(String url, Map<String, Object> data) {
        Requests requests = getRequests();
        return performRequest(requests, url, "PUT", data);
    }

    public String delete(String url) {
        Requests requests = getRequests();
        return performRequest(requests, url, "DELETE", null);
    }

    private Requests getRequests() {
        return new Requests(headers);
    }
}
