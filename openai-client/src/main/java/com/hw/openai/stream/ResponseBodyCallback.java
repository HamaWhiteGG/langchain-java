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

package com.hw.openai.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.openai.common.OpenAiError;
import com.hw.openai.exception.OpenAiException;
import com.hw.openai.exception.SSEFormatException;

import io.reactivex.FlowableEmitter;
import lombok.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.hw.openai.OpenAiClient.defaultObjectMapper;

/**
 * Callback to parse Server Sent Events (SSE) from raw InputStream and
 * emit the events with io.reactivex.FlowableEmitter to allow streaming of SSE.
 *
 * @author HamaWhite
 */
public class ResponseBodyCallback implements Callback<ResponseBody> {

    private final FlowableEmitter<SSE> emitter;

    private final boolean emitDone;

    private final ObjectMapper objectMapper;

    public ResponseBodyCallback(FlowableEmitter<SSE> emitter, boolean emitDone) {
        this(emitter, emitDone, defaultObjectMapper());
    }

    public ResponseBodyCallback(FlowableEmitter<SSE> emitter, boolean emitDone, ObjectMapper objectMapper) {
        this.emitter = emitter;
        this.emitDone = emitDone;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
        BufferedReader reader = null;
        try {
            if (!response.isSuccessful()) {
                HttpException e = new HttpException(response);
                try (ResponseBody errorBody = response.errorBody()) {
                    if (errorBody == null) {
                        throw e;
                    } else {
                        OpenAiError error = objectMapper.readValue(errorBody.string(), OpenAiError.class);
                        throw new OpenAiException(error, e, e.code());
                    }
                }
            }
            String line;
            SSE sse = null;
            try (ResponseBody responseBody = response.body()) {
                if (responseBody != null) {
                    InputStream in = responseBody.byteStream();
                    reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

                    while (!emitter.isCancelled() && (line = reader.readLine()) != null) {
                        if (line.startsWith("data:")) {
                            String data = line.substring(5).trim();
                            sse = new SSE(data);
                        } else if (line.isEmpty() && sse != null) {
                            if (sse.isDone()) {
                                if (emitDone) {
                                    emitter.onNext(sse);
                                }
                                break;
                            }
                            emitter.onNext(sse);
                            sse = null;
                        } else {
                            throw new SSEFormatException("Invalid sse format! " + line);
                        }
                    }
                }
            }
            emitter.onComplete();
        } catch (Throwable t) {
            onFailure(call, t);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
        emitter.onError(t);
    }
}
