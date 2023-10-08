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

package com.hw.openai.example.chat;

import com.hw.langchain.examples.runner.RunnableExample;
import com.hw.openai.OpenAiClient;
import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.Message;

import java.util.List;

/**
 * @author HamaWhite
 */
@RunnableExample
public class StreamChatCompletionExample {

    public static void main(String[] args) {
        OpenAiClient client = OpenAiClient.builder()
                .requestTimeout(120)
                .build()
                .init();

        Message message = Message.of("Introduce West Lake in Hangzhou, China.");
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4")
                .messages(List.of(message))
                .temperature(0)
                .stream(true)
                .build();

        client.streamChatCompletion(chatCompletion)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(e -> {
                    String content = e.getChoices().get(0).getMessage().getContent();
                    if (content != null) {
                        System.out.print(content);
                    }
                });
        client.close();
    }
}
