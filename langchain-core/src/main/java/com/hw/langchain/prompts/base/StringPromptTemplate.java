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

import java.util.List;
import java.util.Map;

/**
 * @description: String prompt should expose the format method, returning a prompt.
 * @author: HamaWhite
 */
public abstract class StringPromptTemplate extends BasePromptTemplate {

    public StringPromptTemplate(List<String> inputVariables) {
        super(inputVariables);
    }

    public StringPromptTemplate(List<String> inputVariables, BaseOutputParser outputParser) {
        super(inputVariables, outputParser);
    }

    @Override
    public PromptValue formatPrompt(Map<String, Object> kwargs) {
        return new StringPromptValue(format(kwargs));
    }

}
