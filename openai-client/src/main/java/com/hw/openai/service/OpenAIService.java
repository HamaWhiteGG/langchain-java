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

package com.hw.openai.service;

import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.ChatCompletionResp;
import com.hw.openai.entity.completions.Completion;
import com.hw.openai.entity.completions.CompletionResp;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @description: OpenAIService
 * @author: HamaWhite
 */
public interface OpenAIService {

    @POST("v1/completions")
    Single<CompletionResp> completion(@Body Completion completion);

    @POST("v1/chat/completions")
    Single<ChatCompletionResp> chatCompletion(@Body ChatCompletion chatCompletion);
}
