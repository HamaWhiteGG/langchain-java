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

import com.hw.langchain.schema.BaseMessage;

import java.util.List;

/**
 * interface for database supported chat message repository;
 *
 * @author zhangxiaojia002
 * @date 2023/7/20 9:50 下午
 **/
public interface ChatMessageRepository {

    /**
     * Loads all historical chat messages for the given sessionId.
     *
     * @param sessionId The unique identifier of the chat session.
     * @return A List of BaseMessage containing the chat message history for the session.
     */
    List<BaseMessage> loadMessage(String sessionId);

    /**
     * Saves a chat message to the specified sessionId.
     *
     * @param sessionId   The unique identifier of the chat session.
     * @param baseMessage The BaseMessage to be saved.
     */
    void saveMessage(String sessionId, BaseMessage baseMessage);

    /**
     * Clears all chat messages for the specified sessionId.
     *
     * @param sessionId The unique identifier of the chat session.
     */
    void clearSessionChatMessage(String sessionId);
}
