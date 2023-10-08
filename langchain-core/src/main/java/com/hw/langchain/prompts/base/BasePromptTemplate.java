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
import lombok.NoArgsConstructor;
import lombok.var;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Base class for all prompt templates, returning a prompt.
 *
 * @author HamaWhite
 */
@Data
@NoArgsConstructor
public abstract class BasePromptTemplate {

    /**
     * A list of the names of the variables the prompt template expects.
     */
    protected List<String> inputVariables;

    /**
     * How to parse the output of calling an LLM on this formatted prompt.
     */
    protected BaseOutputParser<?> outputParser;

    protected Map<String, Object> partialVariables = new HashMap<>();

    protected BasePromptTemplate(List<String> inputVariables) {
        this.inputVariables = inputVariables;
    }

    protected BasePromptTemplate(List<String> inputVariables, Map<String, Object> partialVariables) {
        this.inputVariables = inputVariables;
        this.partialVariables = partialVariables;
    }

    protected BasePromptTemplate(List<String> inputVariables, BaseOutputParser<?> outputParser) {
        this.inputVariables = inputVariables;
        this.outputParser = outputParser;
    }

    /**
     * Create Chat Messages.
     */
    public abstract PromptValue formatPrompt(Map<String, Object> kwargs);

    /**
     * Merge the partial variables and user variables into a single map.
     *
     * @param kwargs Additional user variables provided.
     * @return Merged map containing partial variables and user variables.
     */
    public Map<String, Object> mergePartialAndUserVariables(Map<String, Object> kwargs) {
        var mergedVariables = new HashMap<String, Object>(partialVariables.size() + kwargs.size());
        // Add partial variables
        partialVariables.forEach((key, value) -> {
            if (value instanceof String) {
                mergedVariables.put(key, value);
            } else if (value instanceof Supplier<?>) {
                mergedVariables.put(key, ((Supplier<?>)value).get());
            }
        });
        // Add user variables
        mergedVariables.putAll(kwargs);
        return mergedVariables;
    }

    /**
     * Format the prompt with the inputs.
     *
     * @param kwargs Any arguments to be passed to the prompt template.
     * @return A formatted string.
     */
    public abstract String format(Map<String, Object> kwargs);

}
