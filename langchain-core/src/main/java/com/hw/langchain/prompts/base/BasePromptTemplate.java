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

package com.hw.langchain.prompts.base;

import com.hw.langchain.schema.BaseOutputParser;
import com.hw.langchain.schema.PromptValue;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: Base class for all prompt templates, returning a prompt.
 * @author: HamaWhite
 */
@Data
public abstract class BasePromptTemplate {

    /**
     * A list of the names of the variables the prompt template expects.
     */
    protected List<String> inputVariables;

    /**
     * How to parse the output of calling an LLM on this formatted prompt.
     */
    protected BaseOutputParser outputParser;

    private Map<String, Object> partialVariables = new HashMap<>();

    public BasePromptTemplate(List<String> inputVariables) {
        this.inputVariables = inputVariables;
    }

    public BasePromptTemplate(List<String> inputVariables, BaseOutputParser outputParser) {
        this.inputVariables = inputVariables;
        this.outputParser = outputParser;
    }

    /**
     * Create Chat Messages.
     */
    public abstract PromptValue formatPrompt(Map<String, Object> kwargs);

    /**
     * Format the prompt with the inputs.
     * @param kwargs Any arguments to be passed to the prompt template.
     * @return  A formatted string.
     */
    public abstract String format(Map<String, Object> kwargs);

}
