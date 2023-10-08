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

package com.hw.langchain.memory.buffer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.memory.chat.memory.BaseChatMemory;
import com.hw.langchain.schema.BaseChatMessageHistory;
import com.hw.langchain.schema.BaseMessage;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.schema.Schema.getBufferString;

/**
 * Buffer for storing conversation memory.
 *
 * @author HamaWhite
 */
public class ConversationBufferMemory extends BaseChatMemory {

    private String humanPrefix = "Human";

    private String aiPrefix = "AI";

    private String memoryKey = "history";

    public ConversationBufferMemory() {
    }

    public ConversationBufferMemory(boolean returnMessages) {
        this.returnMessages = returnMessages;
    }

    public ConversationBufferMemory(boolean returnMessages, BaseChatMessageHistory messageHistory) {
        super(messageHistory);
        this.returnMessages = returnMessages;
    }

    /**
     * String buffer of memory.
     */
    public Object buffer() {
        if (returnMessages) {
            return getMemoryMessages();
        } else {
            return getBufferString(getMemoryMessages(), humanPrefix, aiPrefix);
        }
    }

    protected List<BaseMessage> getMemoryMessages() {
        return chatMemory.getMessages();
    }

    /**
     * Will always return list of memory variables.
     */
    @Override
    public List<String> memoryVariables() {
        return ListUtil.of(memoryKey);
    }

    /**
     * Return history buffer.
     */
    @Override
    public Map<String, Object> loadMemoryVariables(Map<String, Object> inputs) {
        return MapUtil.of(memoryKey, buffer());
    }
}
