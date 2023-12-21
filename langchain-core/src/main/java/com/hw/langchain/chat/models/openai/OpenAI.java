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

package com.hw.langchain.chat.models.openai;

import com.hw.langchain.schema.*;
import com.hw.openai.entity.chat.ChatMessage;
import com.hw.openai.entity.chat.ChatMessageRole;

/**
 * @author HamaWhite
 */
public class OpenAI {

    private OpenAI() {
    }

    public static ChatMessage convertLangChainToOpenAI(BaseMessage message) {
        if (message instanceof com.hw.langchain.schema.ChatMessage chatMessage) {
            return ChatMessage.of(chatMessage.getRole(), message.getContent());
        } else if (message instanceof HumanMessage) {
            return ChatMessage.of(message.getContent());
        } else if (message instanceof AIMessage) {
            return ChatMessage.ofAssistant(message.getContent());
        } else if (message instanceof SystemMessage) {
            return ChatMessage.ofSystem(message.getContent());
        } else if (message instanceof FunctionMessage functionMessage) {
            return ChatMessage.ofFunction(message.getContent(), functionMessage.getName());
        } else {
            throw new IllegalArgumentException("Got unknown type " + message.getClass().getSimpleName());
        }
    }

    public static BaseMessage convertOpenAiToLangChain(ChatMessage message) {
        ChatMessageRole role = message.getRole();
        String content = message.getContent();
        switch (role) {
            case USER -> {
                return new HumanMessage(content);
            }
            case ASSISTANT -> {
                content = content != null ? content : "";
                return new AIMessage(content);
            }
            case SYSTEM -> {
                return new SystemMessage(content);
            }
            default -> {
                return new com.hw.langchain.schema.ChatMessage(content, role.getValue());
            }
        }
    }
}
