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

import com.google.gson.Gson;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

/**
 * Wrapper around requests to handle auth and async.
 * The main purpose of this wrapper is to handle authentication (by saving headers)
 * and enable easy async methods on the same base object.
 *
 * @author HamaWhite
 */
public class Requests {

    private final Map<String, String> headers;

    private final OkHttpClient client;

    public Requests(Map<String, String> headers) {
        this.headers = headers;
        this.client = new OkHttpClient();
    }

    private Request buildRequest(String url, RequestBody body, String method) {
        Request.Builder builder = new Request.Builder()
                .url(url);

        if (headers != null) {
            builder.headers(Headers.of(headers));
        }
        builder.method(method, body);
        return builder.build();
    }

    private Response executeRequest(Request request) throws IOException {
        return client.newCall(request).execute();
    }

    public Response sendRequest(String url, String method, Map<String, Object> data) throws IOException {
        RequestBody body = null;

        if (data != null) {
            MediaType mediaType = MediaType.parse("application/json");
            String jsonBody = new Gson().toJson(data);
            body = RequestBody.create(mediaType, jsonBody);
        }

        Request request = buildRequest(url, body, method);
        return executeRequest(request);
    }

    public Response get(String url) throws IOException {
        return sendRequest(url, "GET", null);
    }

    public Response post(String url, Map<String, Object> data) throws IOException {
        return sendRequest(url, "POST", data);
    }

    public Response patch(String url, Map<String, Object> data) throws IOException {
        return sendRequest(url, "PATCH", data);
    }

    public Response put(String url, Map<String, Object> data) throws IOException {
        return sendRequest(url, "PUT", data);
    }

    public Response delete(String url) throws IOException {
        return sendRequest(url, "DELETE", null);
    }
}
