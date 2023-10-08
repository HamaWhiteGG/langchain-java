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

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.schema.BaseMessage;
import lombok.var;

import java.util.*;

/**
 * @author HamaWhite
 */
public class ChatPromptTemplate extends BaseChatPromptTemplate {

    /**
     * Union[BaseMessagePromptTemplate, BaseMessage]
     */
    private final List<?> messages;

    public ChatPromptTemplate(List<String> inputVariables, List<?> messages) {
        this(inputVariables, messages, new HashMap<>());
    }

    public ChatPromptTemplate(List<String> inputVariables, List<?> messages, Map<String, Object> partialVariables) {
        super(inputVariables, partialVariables);
        this.messages = messages;

        validateInputVariables();
    }

    private void validateInputVariables() {
        Set<String> inputVars = new HashSet<>();
        for (var message : messages) {
            if (message instanceof BaseMessagePromptTemplate) {
                inputVars.addAll(((BaseMessagePromptTemplate)message).inputVariables());
            }
        }
        if (partialVariables != null) {
            inputVars.removeAll(partialVariables.keySet());
        }
        if (inputVariables != null) {
            if (!inputVars.equals(new HashSet<>(inputVariables))) {
                throw new IllegalArgumentException(String
                        .format("Got mismatched inputVariables. Expected: %s. Got: %s", inputVars, inputVariables));
            }
        } else {
            inputVariables = ListUtil.toList(inputVars);
        }
    }

    @Override
    public List<BaseMessage> formatMessages(Map<String, Object> kwargs) {
        kwargs = mergePartialAndUserVariables(kwargs);
        List<BaseMessage> result = new ArrayList<>();
        for (var messageTemplate : messages) {
            if (messageTemplate instanceof BaseMessage) {
                result.add((BaseMessage) messageTemplate);
            } else if (messageTemplate instanceof BaseMessagePromptTemplate) {
                var relParams = new HashMap<String, Object>();
                BaseMessagePromptTemplate promptTemplate = (BaseMessagePromptTemplate) messageTemplate;
                kwargs.forEach((key, value) -> {
                    if (promptTemplate.inputVariables().contains(key)) {
                        relParams.put(key, value);
                    }
                });
                result.addAll(promptTemplate.formatMessages(relParams));
            } else {
                throw new IllegalArgumentException("Unexpected input: " + messageTemplate);
            }
        }
        return result;
    }

    public static ChatPromptTemplate fromMessages(List<?> messages) {
        Set<String> inputVars = new HashSet<>();
        for (var message : messages) {
            if (message instanceof BaseMessagePromptTemplate) {
                inputVars.addAll(((BaseMessagePromptTemplate)message).inputVariables());
            }
        }
        return new ChatPromptTemplate(new ArrayList<>(inputVars), messages);
    }
}
