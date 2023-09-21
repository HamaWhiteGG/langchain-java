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
import com.hw.openai.entity.chat.Message;
import com.hw.openai.entity.chat.Role;

/**
 * @author HamaWhite
 */
public class OpenAI {

    private OpenAI() {
    }

    public static Message convertLangChainToOpenAI(BaseMessage message) {
        if (message instanceof ChatMessage) {
            return Message.of(((ChatMessage) message).getRole(), message.getContent());
        } else if (message instanceof HumanMessage) {
            return Message.of(message.getContent());
        } else if (message instanceof AIMessage) {
            return Message.ofAssistant(message.getContent());
        } else if (message instanceof SystemMessage) {
            return Message.ofSystem(message.getContent());
        } else if (message instanceof FunctionMessage) {
            return Message.ofFunction(message.getContent(), ((FunctionMessage)message).getName());
        } else {
            throw new IllegalArgumentException("Got unknown type " + message.getClass().getSimpleName());
        }
    }

    public static BaseMessage convertOpenAiToLangChain(Message message) {
        Role role = message.getRole();
        String content = message.getContent();
        switch (role) {
            case USER :
                return new HumanMessage(content);
            case ASSISTANT :
                content = content != null ? content : "";
                return new AIMessage(content);
            case SYSTEM :
                return new SystemMessage(content);
            default :
                return new ChatMessage(content, role.getValue());
        }
    }
}
