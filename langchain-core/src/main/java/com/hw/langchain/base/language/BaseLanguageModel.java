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

package com.hw.langchain.base.language;

import com.hw.langchain.schema.AsyncLLMResult;
import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.schema.PromptValue;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * BaseLanguageModel is an interface for interacting with a language model.
 *
 * @author HamaWhite
 */
public interface BaseLanguageModel {

    /**
     * Take in a list of prompt values and return an LLMResult.
     */
    LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop);

    /**
     * Predict text from text.
     */
    default String predict(String text) {
        return predict(text, null);
    }

    /**
     * Predict text from text.
     */
    String predict(String text, List<String> stop);

    /**
     * Predict message from messages.
     */
    default BaseMessage predictMessages(List<BaseMessage> messages) {
        return predictMessages(messages, null);
    }

    /**
     * Predict message from messages.
     */
    BaseMessage predictMessages(List<BaseMessage> messages, List<String> stop);

    /**
     * Take in a list of prompt values and return an Flux<AsyncLLMResult> for every PromptValue.
     */
    default List<Flux<AsyncLLMResult>> asyncGeneratePrompt(List<PromptValue> prompts) {
        return asyncGeneratePrompt(prompts, null);
    }

    /**
     * Take in a list of prompt values and return an Flux<AsyncLLMResult> for every PromptValue.
     */
    default List<Flux<AsyncLLMResult>> asyncGeneratePrompt(List<PromptValue> prompts, List<String> stop) {
        throw new UnsupportedOperationException("not supported yet.");
    }

    /**
     * Predict text from text async.
     */
    default Flux<String> asyncPredict(String text) {
        return asyncPredict(text, null);
    }

    /**
     * Predict text from text async.
     */
    default Flux<String> asyncPredict(String text, List<String> stop) {
        throw new UnsupportedOperationException("not supported yet.");
    }

    /**
     * Predict message from messages async.
     */
    default Flux<BaseMessage> asyncPredictMessages(List<BaseMessage> messages, List<String> stop) {
        throw new UnsupportedOperationException("not supported yet.");
    }

}
