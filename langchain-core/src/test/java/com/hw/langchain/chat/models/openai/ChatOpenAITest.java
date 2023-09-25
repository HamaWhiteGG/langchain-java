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

package com.hw.langchain.chat.models.openai;

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.schema.AIMessage;
import com.hw.langchain.schema.HumanMessage;
import com.hw.langchain.schema.SystemMessage;

import lombok.var;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class ChatOpenAITest {

    private static final Logger LOG = LoggerFactory.getLogger(ChatOpenAITest.class);

    private static ChatOpenAI chat;

    @BeforeAll
    public static void setup() {
        chat = ChatOpenAI.builder()
                .temperature(0)
                .build()
                .init();
    }

    /**
     * You can get completions by passing in a single message.
     */
    @Test
    void testChatWithSingleMessage() {
        var message = new HumanMessage("Translate this sentence from English to French. I love programming.");
        var actual = chat.call(ListUtil.of(message));

        var expected = new AIMessage("J'adore la programmation.");
        assertEquals(expected, actual);
    }

    /**
     * You can also pass in multiple messages for OpenAI’s gpt-3.5-turbo and gpt-4 models.
     */
    @Test
    void testChatWithMultiMessages() {
        var messages = ListUtil.of(
                new SystemMessage("You are a helpful assistant that translates English to French."),
                new HumanMessage("I love programming."));
        var actual = chat.call(messages);

        var expected = new AIMessage("J'adore la programmation.");
        assertEquals(expected, actual);
    }

    /**
     * You can go one step further and generate completions for multiple sets of messages using generate.
     * This returns an LLMResult with an additional message parameter.
     */
    @Test
    void testGenerateWithMultiMessages() {
        var batchMessages = ListUtil.of(
                ListUtil.of(
                        new SystemMessage("You are a helpful assistant that translates English to French."),
                        new HumanMessage("I love programming.")),
                ListUtil.of(
                        new SystemMessage("You are a helpful assistant that translates English to French."),
                        new HumanMessage("I love artificial intelligence.")));
        var result = chat.generate(batchMessages);
        assertNotNull(result, "result should not be null");

        LOG.info("result: {}", result);
        LOG.info("token_usage: {}", result.getLlmOutput().get("token_usage"));
        assertThat(result.getGenerations()).isNotNull().hasSize(2);
    }

    @Test
    void testPredictMessages() {
        var message = new HumanMessage("Translate this sentence from English to French. I love programming.");
        var actual = chat.predictMessages(ListUtil.of(message));

        var expected = new AIMessage("J'aime programmer.");
        assertEquals(expected, actual);
    }

    @Test
    void testPredict() {
        var text = "Translate this sentence from English to French. I love programming.";
        var actual = chat.predict(text);
        var expected = "J'aime programmer.";
        assertEquals(expected, actual);
    }
}