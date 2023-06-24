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

package com.hw.langchain.schema;

import java.util.List;

/**
 * Class to parse the output of an LLM call.
 * Output parsers help structure language model responses.
 *
 * @author HamaWhite
 */
public abstract class BaseOutputParser<T> extends BaseLLMOutputParser<T> {

    @Override
    public T parseResult(List<? extends Generation> result) {
        return parse(result.get(0).getText());
    }

    /**
     * Parse the output of an LLM call.
     * A method which takes in a string (assumed output of a language model) and parses it into some structure.
     *
     * @param text output of language model
     * @return structured output
     */
    public abstract T parse(String text);

    /**
     * Optional method to parse the output of an LLM call with a prompt.
     * The prompt is largely provided in the event the OutputParser wants
     * to retry or fix the output in some way, and needs information from
     * the prompt to do so.
     *
     * @param completion output of language model
     * @param prompt     prompt value
     * @return structured output
     */
    public Object parseWithPrompt(String completion, PromptValue prompt) {
        return parse(completion);
    }

    /**
     * Instructions on how the LLM output should be formatted.
     *
     * @return format instructions
     */
    public String getFormatInstructions() {
        throw new UnsupportedOperationException("Method getFormatInstructions() is not implemented.");
    }
}
