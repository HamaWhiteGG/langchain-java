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

import cn.hutool.core.collection.ListUtil;
import com.hw.openai.entity.chat.ChatCompletion;
import com.hw.openai.entity.chat.Message;
import com.hw.openai.entity.completions.Completion;
import com.hw.openai.entity.embeddings.Embedding;
import com.hw.openai.entity.models.Model;
import com.hw.openai.entity.models.ModelResp;

import lombok.var;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <a href="https://platform.openai.com/docs/api-reference/completions">OpenAI API reference</a>
 *
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class OpenAiClientTest {

    private static OpenAiClient client;

    @BeforeAll
    static void setup() {
        client = OpenAiClient.builder()
                .build()
                .init();
    }

    @AfterAll
    static void cleanup() {
        client.close();
    }

    @Test
    void testListModels() {
        ModelResp moduleResp = client.listModels();

        assertThat(moduleResp).isNotNull();
        List<Model> dataList = moduleResp.getDataList();
        assertThat(dataList).isNotNull();

        List<String> modelIdList = dataList.stream()
                .map(Model::getId)
                .collect(Collectors.toList());
        assertThat(modelIdList).contains("text-davinci-003", "gpt-3.5-turbo");
    }

    @Test
    void testRetrieveModel() {
        Model model = client.retrieveModel("text-davinci-003");

        assertThat(model).isNotNull();
        assertThat(model.getId()).isEqualTo("text-davinci-003");
        assertThat(model.getOwnedBy()).isEqualTo("openai-internal");
    }

    @Test
    void testCompletion() {
        Completion completion = Completion.builder()
                .model("text-davinci-003")
                .prompt(ListUtil.of("Say this is a test"))
                .maxTokens(700)
                .temperature(0)
                .build();

        assertThat(client.completion(completion)).isEqualTo("This is indeed a test.");
    }

    @Test
    void testStreamCompletion() {
        Completion completion = Completion.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(ListUtil.of("Say this is a test"))
                .temperature(0)
                .stream(true)
                .build();

        // Call client.streamCompletion(completion) and verify the results
        List<String> resultList = client.streamCompletion(completion)
                .doOnError(Throwable::printStackTrace)
                .map(e -> e.getChoices().get(0).getText())
                .toList()
                .blockingGet();

        assertThat(resultList).isNotNull();
        assertThat(resultList).isNotEmpty();
        assertThat(resultList).isEqualTo(ListUtil.of("\n\n", "This", " is", " a", " test", ".", ""));
    }

    @Test
    void testChatCompletion() {
        Message message = Message.of("Hello!");

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-3.5-turbo")
                .temperature(0)
                .messages(ListUtil.of(message))
                .build();

        assertThat(client.chatCompletion(chatCompletion)).isEqualTo("Hello! How can I assist you today?");
    }

    @Test
    void testStreamChatCompletion() {
        Message message = Message.of("Hello!");

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-3.5-turbo")
                .temperature(0)
                .messages(ListUtil.of(message))
                .stream(true)
                .build();

        List<String> resultList = client.streamChatCompletion(chatCompletion)
                .doOnError(Throwable::printStackTrace)
                .map(e -> {
                    String content = e.getChoices().get(0).getMessage().getContent();
                    return content != null ? content : "";
                })
                .toList()
                .blockingGet();

        assertThat(resultList).isNotNull();
        assertThat(resultList).isNotEmpty();
        assertThat(resultList)
                .isEqualTo(ListUtil.of("", "Hello", "!", " How", " can", " I", " assist", " you", " today", "?", ""));
    }

    @Test
    void testEmbeddings() {
        var embedding = Embedding.builder()
                .model("text-embedding-ada-002")
                .input(ListUtil.of("The food was delicious and the waiter..."))
                .build();

        var response = client.createEmbedding(embedding);

        assertThat(response).as("Response should not be null").isNotNull();
        assertThat(response.getData()).as("Data list should have size 1").hasSize(1);
        assertThat(response.getData().get(0).getEmbedding()).as("Embedding should have size 1536").hasSize(1536);
    }
}