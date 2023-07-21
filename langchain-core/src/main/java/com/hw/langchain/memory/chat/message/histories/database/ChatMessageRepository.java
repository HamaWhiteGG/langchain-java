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
     * load all history chat message of given sessionId
     *
     * @param sessionId
     * @return
     */
    List<BaseMessage> loadMessage(String sessionId);

    void saveMessage(String sessionId, BaseMessage baseMessage);

    void clearSessionChatMessage(String sessionId);
}
