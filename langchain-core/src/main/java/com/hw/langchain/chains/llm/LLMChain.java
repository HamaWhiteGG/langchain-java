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

package com.hw.langchain.chains.llm;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.schema.PromptValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: Chain to run queries against LLMs
 * @author: HamaWhite
 */
public class LLMChain extends Chain {

    private static final Logger LOG = LoggerFactory.getLogger(LLMChain.class);

    private BaseLanguageModel llm;

    /**
     * Prompt object to use.
     */
    private BasePromptTemplate prompt;

    private String outputKey = "text";

    public LLMChain(BaseLanguageModel llm, BasePromptTemplate prompt) {
        this.llm = llm;
        this.prompt = prompt;
    }

    public LLMChain(BaseLanguageModel llm, BasePromptTemplate prompt, String outputKey) {
        this.llm = llm;
        this.prompt = prompt;
        this.outputKey = outputKey;
    }

    /**
     * Will be whatever keys the prompt expects.
     */
    @Override
    public List<String> inputKeys() {
        return prompt.getInputVariables();
    }

    /**
     * Will always return text key.
     */
    @Override
    public List<String> outputKeys() {
        return List.of(this.outputKey);
    }

    @Override
    public Map<String, String> _call(Map<String, ?> inputs) {
        LLMResult response = generate(List.of(inputs));
        return createOutputs(response).get(0);
    }

    /**
     * Generate LLM result from inputs.
     */
    private LLMResult generate(List<Map<String, ?>> inputList) {
        List<PromptValue> prompts = prepPrompts(inputList);
        return this.llm.generatePrompt(prompts, null);
    }

    /**
     * Prepare prompts from inputs.
     */
    private List<PromptValue> prepPrompts(List<Map<String, ?>> inputList) {
        List<PromptValue> prompts = new ArrayList<>();
        for (Map<String, ?> inputs : inputList) {
            Map<String, Object> selectedInputs = new HashMap<>();
            prompt.getInputVariables().forEach(key -> {
                if (inputs.containsKey(key)) {
                    selectedInputs.put(key, inputs.get(key));
                }
            });

            PromptValue promptValue = this.prompt.formatPrompt(selectedInputs);
            LOG.info("Prompt after formatting: {}", promptValue);
            prompts.add(promptValue);
        }
        return prompts;
    }

    /**
     * Create outputs from response.
     */
    private List<Map<String, String>> createOutputs(LLMResult response) {
        return response.getGenerations().stream()
                .map(generationList -> Map.of(outputKey, generationList.get(0).getText()))
                .toList();
    }
}
