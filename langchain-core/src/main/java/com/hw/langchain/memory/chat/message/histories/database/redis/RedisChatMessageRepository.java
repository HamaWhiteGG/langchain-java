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

import com.fasterxml.jackson.core.type.TypeReference;
import com.hw.langchain.chains.query.constructor.JsonUtils;
import com.hw.langchain.memory.chat.message.histories.database.ChatMessageRepository;
import com.hw.langchain.schema.BaseMessage;

import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import lombok.Builder;
import lombok.experimental.Tolerate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangxiaojia002
 * @date 2023/7/20 9:59 下午
 **/
@Builder
public class RedisChatMessageRepository implements ChatMessageRepository {

    private RedissonClient redissonClient;
    private String keyPrefix = "message_store";
    private Integer ttlSeconds;

    @Tolerate
    public RedisChatMessageRepository(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Tolerate
    public RedisChatMessageRepository(RedissonClient redissonClient, int ttlSeconds) {
        this.redissonClient = redissonClient;
        this.ttlSeconds = ttlSeconds;
    }

    /**
     * Construct the record key to use
     *
     * @return key
     */
    private String key(String sessionId) {
        if (this.keyPrefix == null) {
            return sessionId;
        }
        return this.keyPrefix + sessionId;
    }

    @Override
    public List<BaseMessage> loadMessage(String sessionId) {
        RQueue<String> messageQueue = redissonClient.getQueue(key(sessionId));
        List<String> messageJSonStrList = messageQueue.readAll();
        return messageJSonStrList.stream().map(x -> {
            Map<String, Object> data =
                    JsonUtils.convertFromJsonStr(x, new TypeReference<Map<String, Object>>() {});
            return BaseMessage.fromMap(data);
        }).collect(Collectors.toList());
    }

    @Override
    public void saveMessage(String sessionId, BaseMessage baseMessage) {
        RQueue<String> messageQueue = redissonClient.getQueue(key(sessionId));
        messageQueue.add(JsonUtils.toJsonStringWithIndent(baseMessage.toMap()));
        if (this.ttlSeconds != null) {
            messageQueue.expire(Duration.of(ttlSeconds, ChronoUnit.SECONDS));
        }
    }

    @Override
    public void clearSessionChatMessage(String sessionId) {
        RQueue<String> messageQueue = redissonClient.getQueue(key(sessionId));
        messageQueue.delete();
    }
}
