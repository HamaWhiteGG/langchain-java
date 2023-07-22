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

import com.hw.langchain.exception.LangChainException;
import com.hw.langchain.schema.BaseMessage;

import java.util.List;

/**
 * @author zhangxiaojia002
 * @date 2023/7/22 12:24 下午
 **/
public class BufferWindowMemoryMessagePicker implements MemoryMessagePicker {

    // k represents the number of dialog round of chat messages we want to add into gpt request
    private int k = 5;
    public BufferWindowMemoryMessagePicker(int k) {
        if (k <= 0) {
            throw new LangChainException("buffer window size musts larger than zero");
        }
        this.k = k;
    }

    @Override
    public List<BaseMessage> pickMemoryMessage(List<BaseMessage> memoryMessages) {
        // select the last 2 * k messages from memoryMessages List
        if (memoryMessages.size() > 2 * k) {
            return memoryMessages.subList(memoryMessages.size() - 2 * k, memoryMessages.size());
        }
        return memoryMessages;
    }
}
