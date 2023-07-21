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

package com.hw.langchain.memory.chat.message.histories.database;

import com.hw.langchain.schema.BaseChatMessageHistory;
import com.hw.langchain.schema.BaseMessage;

import java.util.List;

/**
 * database based chat message history;
 *
 * @author zhangxiaojia002
 * @date 2023/7/20 9:53 下午
 **/
public class DataBaseChatMessageHistory extends BaseChatMessageHistory {

    private final String sessionId;
    private final ChatMessageRepository chatMessageRepository;

    public DataBaseChatMessageHistory(String sessionId, ChatMessageRepository chatMessageRepository) {
        this.sessionId = sessionId;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void addMessage(BaseMessage message) {
        chatMessageRepository.saveMessage(sessionId, message);
    }

    @Override
    public void clear() {
        chatMessageRepository.clearSessionChatMessage(sessionId);
    }

    @Override
    public List<BaseMessage> getMessages() {
        return chatMessageRepository.loadMessage(sessionId);
    }
}
