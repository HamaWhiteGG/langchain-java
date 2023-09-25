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
import com.hw.langchain.schema.AIMessage;
import com.hw.langchain.schema.BaseChatMessageHistory;
import com.hw.langchain.schema.HumanMessage;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author zhangxiaojia002
 * @date 2023/7/22 1:25 下午
 **/
class ConversationBufferWindowMemoryTest {

    @Test
    void whenChatMessageHistoryHasManyMessagesOnlyReturnMax2KMessages() {
        BaseChatMessageHistory baseChatMessageHistory = Mockito.mock(BaseChatMessageHistory.class);
        BaseChatMemory conversationBufferWindowMemory =
                new ConversationBufferWindowMemory(1, true, baseChatMessageHistory);

        when(baseChatMessageHistory.getMessages()).thenReturn(
                ListUtil.of(new HumanMessage("hi"), new AIMessage("hi"),
                        new HumanMessage("what are you doing"), new AIMessage("I'm thinking")));

        assertEquals(2,
                ((List<?>) conversationBufferWindowMemory.loadMemoryVariables(MapUtil.empty()).get("history")).size());
    }
}