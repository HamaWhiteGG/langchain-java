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

package com.hw.langchain.llms.base;

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.schema.PromptValue;

import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LLM wrapper should take in a prompt and return a string.
 * @author HamaWhite
 */
@SuperBuilder
public abstract class BaseLLM implements BaseLanguageModel {

    /**
     * Return type of llm.
     */
    public abstract String llmType();

    /**
     * Run the LLM on the given prompts.
     */
    protected abstract LLMResult innerGenerate(List<String> prompts, List<String> stop);

    /**
     * Check Cache and run the LLM on the given prompt and input.
     */
    public String call(String prompt, List<String> stop) {
        return generate(ListUtil.of(prompt), stop).getGenerations().get(0).get(0).getText();
    }

    public String call(String prompt) {
        return call(prompt, null);
    }

    /**
     * Run the LLM on the given prompt and input.
     */
    public LLMResult generate(List<String> prompts, List<String> stop) {
        return innerGenerate(prompts, stop);
    }

    @Override
    public LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop) {
        List<String> promptStrings = prompts.stream()
                .map(PromptValue::toString)
                .collect(Collectors.toList());
        return generate(promptStrings, stop);
    }

    @Override
    public String predict(String text) {
        return predict(text, null);
    }

    @Override
    public String predict(String text, List<String> stop) {
        return call(text, stop);
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> messages) {
        return predictMessages(messages, null);
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> messages, List<String> stop) {
        return null;
    }
}
