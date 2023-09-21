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

package com.hw.langchain.schema;

import cn.hutool.core.map.MapBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hw.langchain.chains.query.constructor.JsonUtils;
import com.hw.langchain.exception.LangChainException;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Message object.
 *
 * @author HamaWhite
 */
@Data
@NoArgsConstructor
public abstract class BaseMessage {

    protected String content;

    protected Map<String, Object> additionalKwargs;

    protected BaseMessage(String content) {
        this.content = content;
        this.additionalKwargs = new HashMap<>();
    }

    /**
     * Type of the message, used for serialization.
     */
    public abstract String type();

    public Map<String, Object> toMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        objectMapper.registerModule(module);
        Map<String, Object> map = objectMapper.convertValue(this, new TypeReference<Map<String, Object>>() {});
        return MapBuilder.create(new HashMap<String, Object>())
                .put("type", type())
                .put("data", map).map();
    }

    public static BaseMessage fromMap(Map<String, Object> message) {
        String type = (String) message.get("type");
        Object data = message.get("data");
        String jsonStr = JsonUtils.toJsonStringWithIndent(data, 0);
        switch (type) {
            case "ai":
                return JsonUtils.convertFromJsonStr(jsonStr, AIMessage.class);
            case "human":
                return JsonUtils.convertFromJsonStr(jsonStr, HumanMessage.class);
            case "system":
                return JsonUtils.convertFromJsonStr(jsonStr, SystemMessage.class);
            case "chat" :
                return JsonUtils.convertFromJsonStr(jsonStr, ChatMessage.class);
            case "function" :
                return JsonUtils.convertFromJsonStr(jsonStr, FunctionMessage.class);
            default :
                throw new LangChainException(String.format("Got unexpected message type:%s", type));
        }
    }
}
