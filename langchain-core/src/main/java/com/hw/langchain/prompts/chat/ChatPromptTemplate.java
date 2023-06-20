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

import com.hw.langchain.schema.BaseMessage;

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
        super(inputVariables);
        this.messages = messages;

        validateInputVariables();
    }

    private void validateInputVariables() {
        Set<String> inputVars = new HashSet<>();
        for (var message : messages) {
            if (message instanceof BaseMessagePromptTemplate promptTemplate) {
                inputVars.addAll(promptTemplate.inputVariables());
            }
        }
        if (inputVariables != null) {
            if (!inputVars.equals(new HashSet<>(inputVariables))) {
                throw new IllegalArgumentException(String
                        .format("Got mismatched input_variables. Expected: %s. Got: %s", inputVars, inputVariables));
            }
        } else {
            inputVariables = List.copyOf(inputVars);
        }
    }

    @Override
    public List<BaseMessage> formatMessages(Map<String, Object> kwargs) {
        List<BaseMessage> result = new ArrayList<>();
        for (var messageTemplate : messages) {
            if (messageTemplate instanceof BaseMessage baseMessage) {
                result.add(baseMessage);
            } else if (messageTemplate instanceof BaseMessagePromptTemplate promptTemplate) {
                var relParams = new HashMap<String, Object>();
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
            if (message instanceof BaseMessagePromptTemplate template) {
                inputVars.addAll(template.inputVariables());
            }
        }
        return new ChatPromptTemplate(new ArrayList<>(inputVars), messages);
    }
}
