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
 *//*


package com.hw.openai.stream;

import com.hw.openai.exception.OpenAiException;
import com.hw.openai.exception.SSEFormatException;

import org.junit.jupiter.api.Test;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

import static org.junit.jupiter.api.Assertions.*;

*/
/**
 * @author HamaWhite
 *//*

class ResponseBodyCallbackTest {

    @Test
    void testHappyPath() {
        ResponseBody body = ResponseBody.create(MediaType.get("application/json"), """
                data: line 1

                data: line 2

                data: [DONE]

                """);
        Call<ResponseBody> call = Calls.response(body);

        Flowable<SSE> flowable = Flowable.create(emitter -> call.enqueue(new ResponseBodyCallback(emitter, false)),
                BackpressureStrategy.BUFFER);

        TestSubscriber<SSE> testSubscriber = new TestSubscriber<>();
        flowable.subscribe(testSubscriber);

        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(2);
        assertEquals("line 1", testSubscriber.values().get(0).data());
        assertEquals("line 2", testSubscriber.values().get(1).data());
    }

    @Test
    void testEmitDone() {
        ResponseBody body = ResponseBody.create(MediaType.get("application/json"), """
                data: line 1

                data: line 2

                data: [DONE]

                """);
        Call<ResponseBody> call = Calls.response(body);

        Flowable<SSE> flowable = Flowable.create(emitter -> call.enqueue(new ResponseBodyCallback(emitter, true)),
                BackpressureStrategy.BUFFER);

        TestSubscriber<SSE> testSubscriber = new TestSubscriber<>();
        flowable.subscribe(testSubscriber);

        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(3);
        assertEquals("[DONE]", testSubscriber.values().get(2).data());
    }

    @Test
    void testSseFormatException() {
        ResponseBody body = ResponseBody.create(MediaType.get("application/json"), """
                bad: line 1

                data: line 2

                data: [DONE]

                """);

        Call<ResponseBody> call = Calls.response(body);

        Flowable<SSE> flowable = Flowable.create(emitter -> call.enqueue(new ResponseBodyCallback(emitter, true)),
                BackpressureStrategy.BUFFER);

        TestSubscriber<SSE> testSubscriber = new TestSubscriber<>();

        flowable.subscribe(testSubscriber);

        testSubscriber.assertError(SSEFormatException.class);
    }

    @Test
    void testServerError() {
        String errorBody = """
                    {
                        "error": {
                            "message": "Invalid auth token",
                            "type": "type",
                            "param": "param",
                            "code": "code"
                        }
                    }
                """;
        ResponseBody body = ResponseBody.create(MediaType.get("application/json"), errorBody);
        Call<ResponseBody> call = Calls.response(Response.error(401, body));

        Flowable<SSE> flowable = Flowable.create(emitter -> call.enqueue(new ResponseBodyCallback(emitter, true)),
                BackpressureStrategy.BUFFER);

        TestSubscriber<SSE> testSubscriber = new TestSubscriber<>();
        flowable.subscribe(testSubscriber);

        testSubscriber.assertError(OpenAiException.class);

        assertEquals("Invalid auth token", testSubscriber.errors().get(0).getMessage());
    }

}*/
