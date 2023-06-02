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

package com.hw.langchain.prompts.prompt;

import com.hw.langchain.prompts.base.StringPromptTemplate;
import com.hw.langchain.schema.BaseOutputParser;

import org.apache.commons.text.StringSubstitutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: Schema to represent a prompt for an LLM.
 * @author: HamaWhite
 */
public class PromptTemplate extends StringPromptTemplate {

    /**
     * The prompt template.
     */
    private final String template;

    /**
     * Whether or not to try validating the template.
     */
    private boolean validateTemplate;

    public PromptTemplate(List<String> inputVariables, String template) {
        super(inputVariables);
        this.template = template;
    }

    public PromptTemplate(List<String> inputVariables, String template, BaseOutputParser outputParser) {
        super(inputVariables, outputParser);
        this.template = template;
    }

    @Override
    public String format(Map<String, Object> kwargs) {
        return StringSubstitutor.replace(template, kwargs, "{", "}");
    }

    public static PromptTemplate fromTemplate(String template) {
        List<String> variableNames = new ArrayList<>();
        StringSubstitutor substitutor = new StringSubstitutor(variable -> {
            variableNames.add(variable);
            return null;
        });
        substitutor.setVariablePrefix("{");
        substitutor.setVariableSuffix("}");
        substitutor.replace(template);
        return new PromptTemplate(variableNames, template);
    }
}
