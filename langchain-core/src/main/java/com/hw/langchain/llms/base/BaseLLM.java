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

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.callbacks.manager.Callbacks;
import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.schema.PromptValue;

import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @description: LLM wrapper should take in a prompt and return a string.
 * @author: HamaWhite
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
    protected abstract LLMResult _generate(List<String> prompts, List<String> stop);

    /**
     * Check Cache and run the LLM on the given prompt and input.
     */
    public String call(String prompt, List<String> stop, Callbacks callbacks) {
        return generate(List.of(prompt), stop, callbacks).getGenerations().get(0).get(0).getText();
    }

    public String call(String prompt) {
        return call(prompt, null, null);
    }

    /**
     * Run the LLM on the given prompt and input.
     */
    public LLMResult generate(List<String> prompts, List<String> stop, Callbacks callbacks) {
        return _generate(prompts, stop);
    }

    @Override
    public LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop) {
        List<String> promptStrings = prompts.stream()
                .map(PromptValue::toString)
                .toList();
        return generate(promptStrings, stop, null);
    }
}
