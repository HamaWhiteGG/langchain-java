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

package com.hw.langchain.memory.chat.message.histories.database.redis;

import com.hw.langchain.memory.chat.message.histories.database.DataBaseChatMessageHistory;
import com.hw.langchain.schema.BaseChatMessageHistory;
import com.hw.langchain.schema.BaseMessage;

import org.redisson.api.RedissonClient;

import java.util.List;

/**
 * a simple wrapper for DataBaseChatMessageHistory with redisChatMessageRepository;
 *
 * @author zhangxiaojia002
 * @date 2023/7/21 10:49 上午
 **/
public class RedisChatMessageHistory extends BaseChatMessageHistory {

    private DataBaseChatMessageHistory dataBaseChatMessageHistory;

    public RedisChatMessageHistory(String sessionId, RedissonClient redissonClient, int ttl) {
        RedisChatMessageRepository redisChatMessageRepository = new RedisChatMessageRepository(redissonClient, ttl);
        dataBaseChatMessageHistory = new DataBaseChatMessageHistory(sessionId, redisChatMessageRepository);
    }

    public RedisChatMessageHistory(String sessionId, RedissonClient redissonClient) {
        RedisChatMessageRepository redisChatMessageRepository = new RedisChatMessageRepository(redissonClient);
        dataBaseChatMessageHistory = new DataBaseChatMessageHistory(sessionId, redisChatMessageRepository);
    }

    @Override
    public void addMessage(BaseMessage message) {
        dataBaseChatMessageHistory.addMessage(message);
    }

    @Override
    public void clear() {
        dataBaseChatMessageHistory.clear();
    }

    @Override
    public List<BaseMessage> getMessages() {
        return dataBaseChatMessageHistory.getMessages();
    }
}
