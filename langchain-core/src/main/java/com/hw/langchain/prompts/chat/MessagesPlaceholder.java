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
import com.google.common.collect.Lists;
import com.hw.langchain.schema.BaseMessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Prompt template that assumes variable is already list of messages.
 *
 * @author HamaWhite
 */
public class MessagesPlaceholder extends BaseMessagePromptTemplate {

    private String variableName;

    public MessagesPlaceholder(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public List<BaseMessage> formatMessages(Map<String, Object> kwargs) {
        Object value = kwargs.get(variableName);
        List<?> messages;
        if (value instanceof List<?>) {
            messages = (List<?>) value;
            if(!messages.stream().allMatch(BaseMessage.class::isInstance)){
                throw new IllegalArgumentException(
                        "Variable " + variableName + " should be a list of base messages, got " + value);
            }
            return messages.stream().map(BaseMessage.class::cast).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<String> inputVariables() {
        return ListUtil.of(variableName);
    }
}
