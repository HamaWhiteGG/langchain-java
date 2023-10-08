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

package com.hw.langchain.chat.models.base;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.schema.*;

import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HamaWhite
 */
@SuperBuilder
public abstract class BaseChatModel implements BaseLanguageModel {

    /**
     * Tags to add to the run trace.
     */
    private List<String> tags;

    public Map<String, Object> combineLlmOutputs(List<Map<String, Object>> llmOutputs) {
        return MapUtil.of();
    }

    public LLMResult generate(List<List<BaseMessage>> messages) {
        return generate(messages, null);
    }

    /**
     * Top Level call
     */
    public LLMResult generate(List<List<BaseMessage>> messages, List<String> stop) {
        List<ChatResult> results = messages.stream()
                .map(message -> innerGenerate(message, stop))
                .collect(Collectors.toList());

        List<Map<String, Object>> llmOutputs = results.stream()
                .map(ChatResult::getLlmOutput)
                .collect(Collectors.toList());
        Map<String, Object> llmOutput = combineLlmOutputs(llmOutputs);

        List<List<ChatGeneration>> generations = results.stream()
                .map(ChatResult::getGenerations)
                .collect(Collectors.toList());
        return new LLMResult(generations, llmOutput);
    }

    @Override
    public LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop) {
        List<List<BaseMessage>> promptMessages = prompts.stream()
                .map(PromptValue::toMessages)
                .collect(Collectors.toList());
        return generate(promptMessages, stop);
    }

    /**
     * Top Level call
     */
    public abstract ChatResult innerGenerate(List<BaseMessage> messages, List<String> stop);

    public BaseMessage call(List<BaseMessage> messages) {
        return call(messages, null);
    }

    public BaseMessage call(List<BaseMessage> messages, List<String> stop) {
        Generation generation = generate(ListUtil.partition(messages, 1), stop).getGenerations().get(0).get(0);
        if (generation instanceof ChatGeneration) {
            ChatGeneration chatGeneration = (ChatGeneration) generation;
            return chatGeneration.getMessage();
        } else {
            throw new IllegalArgumentException("Unexpected generation type");
        }
    }

    @Override
    public String predict(String text, List<String> stop) {
        List<String> copyStop = stop != null ? ListUtil.toList(stop) : null;
        BaseMessage message = new HumanMessage(text);

        BaseMessage result = call(ListUtil.of(message), copyStop);
        return result.getContent();
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> messages, List<String> stop) {
        List<String> copyStop = stop != null ? ListUtil.toList(stop) : null;
        return call(messages, copyStop);
    }

    /**
     * Return type of chat model.
     */
    public abstract String llmType();
}
