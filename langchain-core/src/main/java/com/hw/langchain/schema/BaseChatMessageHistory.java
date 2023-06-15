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

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Base interface for chat message history, See `ChatMessageHistory` for default implementation.
 *
 * @author HamaWhite
 */
@Data
public abstract class BaseChatMessageHistory {

    protected List<BaseMessage> messages = new ArrayList<>();

    /**
     * Add a user message to the store
     *
     * @param message
     */
    public void addUserMessage(String message) {
        addMessage(new HumanMessage(message));
    }

    /**
     * Add an AI message to the store
     */
    public void addAIMessage(String message) {
        addMessage(new AIMessage(message));
    }

    /**
     * Add a self-created message to the store
     */
    public abstract void addMessage(BaseMessage message);

    /**
     * Remove all messages from the store
     */
    public abstract void clear();

}
