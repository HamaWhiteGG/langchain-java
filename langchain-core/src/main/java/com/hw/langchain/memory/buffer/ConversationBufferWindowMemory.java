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

import com.hw.langchain.schema.BaseChatMessageHistory;

/**
 * @author zhangxiaojia002
 * @date 2023/7/22 1:12 下午
 **/
public class ConversationBufferWindowMemory extends ConversationBufferLimitedMemory {

    public ConversationBufferWindowMemory(int k) {
        super(new BufferWindowMemoryMessagePicker(k));
    }

    public ConversationBufferWindowMemory(int k, boolean returnMessages) {
        super(new BufferWindowMemoryMessagePicker(k), returnMessages);
    }

    public ConversationBufferWindowMemory(int k, boolean returnMessages, BaseChatMessageHistory chatMessageHistory) {
        super(new BufferWindowMemoryMessagePicker(k), returnMessages, chatMessageHistory);
    }
}
