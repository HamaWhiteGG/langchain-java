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

package com.hw.openai;

import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.Message;
import com.hw.openai.entity.completions.Completion;
import com.hw.openai.util.ProxyUtils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <a href="https://platform.openai.com/docs/api-reference/completions">OpenAI API reference</a>
 *
 * @description: OpenAITest
 * @author: HamaWhite
 */
class OpenAITest {

    private static OpenAI openai;

    @BeforeAll
    static void setup() {
        openai = OpenAI.builder()
                .proxy(ProxyUtils.http("127.0.0.1", 1087))
                .build()
                .init();
    }

    @Test
    void testCompletion() {
        Completion completion = Completion.builder()
                .model("text-davinci-003")
                .prompt("Say this is a test")
                .maxTokens(700)
                .temperature(0)
                .build();

        assertThat(openai.completion(completion)).isEqualTo("This is indeed a test.");
    }

    @Test
    void testChatCompletion() {
        Message message = Message.of("Hello!");

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(message))
                .build();

        assertThat(openai.chatCompletion(chatCompletion))
                .isEqualTo("Hello there! How can I assist you today?");
    }
}