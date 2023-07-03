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

package com.hw.langchain.prompts.chat;

import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.PromptValue;

import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
public abstract class BaseChatPromptTemplate extends BasePromptTemplate {

    public BaseChatPromptTemplate(List<String> inputVariables) {
        super(inputVariables);
    }

    public BaseChatPromptTemplate(List<String> inputVariables, Map<String, Object> partialVariables) {
        super(inputVariables, partialVariables);
    }

    @Override
    public String format(Map<String, Object> kwargs) {
        return formatPrompt(kwargs).toString();
    }

    public PromptValue formatPrompt(Map<String, Object> kwargs) {
        List<BaseMessage> messages = formatMessages(kwargs);
        return new ChatPromptValue(messages);
    }

    /**
     * Format kwargs into a list of messages.
     */
    public abstract List<BaseMessage> formatMessages(Map<String, Object> kwargs);

}
