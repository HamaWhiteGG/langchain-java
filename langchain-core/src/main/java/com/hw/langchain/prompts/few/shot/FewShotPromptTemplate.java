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

package com.hw.langchain.prompts.few.shot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hw.langchain.prompts.base.StringPromptTemplate;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.schema.BaseOutputParser;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hw.langchain.prompts.utils.FormatUtils.formatTemplate;

/**
 * Prompt template that contains few shot examples.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public class FewShotPromptTemplate extends StringPromptTemplate {

    /**
     * Examples to format into the prompt.
     * Either this or exampleSelector should be provided.
     */
    private final List<Map<String, Object>> examples;

    /**
     * PromptTemplate used to format an individual example.
     */
    private final PromptTemplate examplePrompt;

    /**
     * A prompt template string to put before the examples.
     */
    private final String prefix;

    /**
     * A prompt template string to put after the examples.
     */
    private final String suffix;

    /**
     * String separator used to join the prefix, the examples, and suffix.
     */
    private String exampleSeparator;

    public FewShotPromptTemplate(List<Map<String, Object>> examples, PromptTemplate examplePrompt, String prefix,
            String suffix, List<String> inputVariables) {
        this(examples, examplePrompt, prefix, suffix, inputVariables, "\n\n");
    }

    public FewShotPromptTemplate(List<Map<String, Object>> examples, PromptTemplate examplePrompt, String prefix,
            String suffix, List<String> inputVariables, String exampleSeparator) {
        this(examples, examplePrompt, prefix, suffix, inputVariables, exampleSeparator, null);
    }

    public FewShotPromptTemplate(List<Map<String, Object>> examples, PromptTemplate examplePrompt, String prefix,
            String suffix, List<String> inputVariables, String exampleSeparator, BaseOutputParser<?> outputParser) {
        super(inputVariables, outputParser);
        this.examples = examples;
        this.examplePrompt = examplePrompt;
        this.prefix = prefix;
        this.suffix = suffix;
        this.exampleSeparator = exampleSeparator;
    }

    public List<Map<String, Object>> getExamples() {
        return this.examples;
    }

    @Override
    public String format(Map<String, Object> kwargs) {
        kwargs = mergePartialAndUserVariables(kwargs);
        // Get the examples to use.
        List<Map<String, Object>> exampleList = getExamples();
        exampleList = exampleList.stream()
                .map(example -> Maps.filterKeys(example, examplePrompt.getInputVariables()::contains))
                .collect(Collectors.toList());
        // Format the examples.
        List<String> exampleStrings = exampleList.stream()
                .map(examplePrompt::format)
                .collect(Collectors.toList());
        // Create the overall template.
        List<String> pieces = Lists.newArrayList(prefix);
        pieces.addAll(exampleStrings);
        pieces.add(suffix);

        String template = String.join(exampleSeparator, pieces);
        return formatTemplate(template, kwargs);
    }
}
