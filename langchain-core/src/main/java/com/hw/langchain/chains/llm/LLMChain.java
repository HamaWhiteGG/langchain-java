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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.schema.*;

import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chain to run queries against LLMs
 *
 * @author HamaWhite
 */
public class LLMChain extends Chain {

    private static final Logger LOG = LoggerFactory.getLogger(LLMChain.class);

    protected BaseLanguageModel llm;

    /**
     * Prompt object to use.
     */
    @Getter
    protected BasePromptTemplate prompt;

    protected String outputKey = "text";

    /**
     * Output parser to use.
     * Defaults to one that takes the most likely string but does not change it.
     */
    protected BaseLLMOutputParser<String> outputParser = new NoOpOutputParser();

    /**
     * Whether to return only the final parsed result. Defaults to true.
     * If false, will return a bunch of extra information about the generation.
     */
    protected boolean returnFinalOnly = true;

    public LLMChain(BaseLanguageModel llm, BasePromptTemplate prompt) {
        this.llm = llm;
        this.prompt = prompt;
    }

    public LLMChain(BaseLanguageModel llm, BasePromptTemplate prompt, String outputKey) {
        this.llm = llm;
        this.prompt = prompt;
        this.outputKey = outputKey;
    }

    @Override
    public String chainType() {
        return "llm_chain";
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
        return ListUtil.of(outputKey);
    }

    @Override
    protected Map<String, String> innerCall(Map<String, Object> inputs) {
        LLMResult response = generate(ListUtil.of(inputs));
        return createOutputs(response).get(0);
    }

    @Override
    protected Flux<Map<String, String>> asyncInnerCall(Map<String, Object> inputs) {
        var response = asyncGenerate(ListUtil.of(inputs));
        return response.get(0).map(this::createAsyncOutputs);
    }

    /**
     * Generate LLM result from inputs.
     */
    private LLMResult generate(List<Map<String, Object>> inputList) {
        List<String> stop = prepStop(inputList);
        List<PromptValue> prompts = prepPrompts(inputList);
        return llm.generatePrompt(prompts, stop);
    }

    /**
     * Generate LLM result from inputs async.
     */
    private List<Flux<AsyncLLMResult>> asyncGenerate(List<Map<String, Object>> inputList) {
        List<String> stop = prepStop(inputList);
        List<PromptValue> prompts = prepPrompts(inputList);
        return llm.asyncGeneratePrompt(prompts, stop);
    }

    /**
     * Prepare prompts from inputs.
     */
    private List<PromptValue> prepPrompts(List<Map<String, Object>> inputList) {
        List<PromptValue> prompts = new ArrayList<>();
        for (Map<String, ?> inputs : inputList) {
            Map<String, Object> selectedInputs = new HashMap<>();
            prompt.getInputVariables().forEach(key -> {
                if (inputs.containsKey(key)) {
                    selectedInputs.put(key, inputs.get(key));
                }
            });

            PromptValue promptValue = this.prompt.formatPrompt(selectedInputs);
            LOG.debug("Prompt after formatting:\n{}", promptValue);
            prompts.add(promptValue);
        }
        return prompts;
    }

    @SuppressWarnings("unchecked")
    private List<String> prepStop(List<Map<String, Object>> inputList) {
        Map<String, Object> firstInput = inputList.get(0);
        return firstInput.containsKey("stop") ? (List<String>) firstInput.get("stop") : null;
    }

    /**
     * Create outputs from response.
     */
    private List<Map<String, String>> createOutputs(LLMResult llmResult) {
        var result = llmResult.getGenerations().stream()
                .map(generation -> MapBuilder.create(new HashMap<String, String>())
                        .put(outputKey, outputParser.parseResult(generation))
                        .put("full_generation", generation.toString()).map())
                .collect(Collectors.toList());

        if (returnFinalOnly) {
            result = result.stream()
                    .map(r -> MapUtil.of(outputKey, r.get(outputKey)))
                    .collect(Collectors.toList());
        }
        return result;
    }

    /**
     * Create outputs from response async.
     */
    private Map<String, String> createAsyncOutputs(AsyncLLMResult llmResult) {
        Map<String, String> result = MapBuilder.create(new HashMap<String,String>())
                .put(outputKey, outputParser.parseResult(llmResult.getGenerations()))
                .put("full_generation", llmResult.getGenerations().toString()).map();
        if (returnFinalOnly) {
            result = MapUtil.of(outputKey, result.get(outputKey));
        }
        return result;
    }

    /**
     * Format prompt with kwargs and pass to LLM.
     *
     * @param kwargs Keys to pass to prompt template.
     * @return Completion from LLM.
     */
    public String predict(Map<String, Object> kwargs) {
        Map<String, String> resultMap = call(kwargs, false);
        return resultMap.get(outputKey);
    }

    /**
     * Format prompt with kwargs and pass to LLM async.
     *
     * @param kwargs Keys to pass to prompt template.
     * @return Completion from LLM.
     */
    public Flux<String> asyncPredict(Map<String, Object> kwargs) {
        var flux = asyncCall(kwargs, false);
        return flux.map(m -> m.get(outputKey));
    }

    /**
     * Call predict and then parse the results.
     */
    @SuppressWarnings("all")
    public <T> T predictAndParse(Map<String, Object> kwargs) {
        String result = predict(kwargs);
        if (prompt.getOutputParser() != null) {
            return (T) prompt.getOutputParser().parse(result);
        }
        return (T) result;
    }

}
