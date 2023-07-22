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

import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.HumanMessage;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zhangxiaojia002
 * @date 2023/7/22 12:41 下午
 **/
class BufferWindowMemoryMessagePickerTest {

    // when 2*K <= memoryMessages.size(), return all memoryMessages
    @Test
    void returnAllMessageWhenListLengthLessOrEquals2K() {
        int k = 2;
        BufferWindowMemoryMessagePicker bufferWindowMemoryMessagePicker = new BufferWindowMemoryMessagePicker(k);
        assertEquals(2 * k,
                bufferWindowMemoryMessagePicker.pickMemoryMessage(
                        generateRandomHumanMessages(2 * k)).size());
        assertEquals(2 * k - 1,
                bufferWindowMemoryMessagePicker.pickMemoryMessage(
                        generateRandomHumanMessages(2 * k - 1)).size());
    }

    // when 2*K > memoryMessage.size(), return last 2*k memoryMessages;
    @Test
    void returnLast2KMemoryMessagesWhenListLengthLargerThan2K() {
        int k = 2;
        BufferWindowMemoryMessagePicker bufferWindowMemoryMessagePicker = new BufferWindowMemoryMessagePicker(k);
        assertEquals(2 * k,
                bufferWindowMemoryMessagePicker.pickMemoryMessage(
                        generateRandomHumanMessages(2 * k + 1)).size());
        assertEquals(2 * k,
                bufferWindowMemoryMessagePicker.pickMemoryMessage(
                        generateRandomHumanMessages(2 * k + 2)).size());

        List<BaseMessage> messageList = generateRandomHumanMessages(2 * k + 2);
        assertEquals(messageList.subList(2, messageList.size()),
                bufferWindowMemoryMessagePicker.pickMemoryMessage(messageList));
    }

    // generate random k Human messages
    private List<BaseMessage> generateRandomHumanMessages(int k) {
        List<BaseMessage> baseMessageList = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            baseMessageList.add(new HumanMessage(String.valueOf(i)));
        }
        return baseMessageList;
    }
}