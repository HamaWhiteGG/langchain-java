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

import java.util.List;

/**
 * Since the token we input to LLM is limited, the history messages we add into gpt request is also limited. <br/>
 * <p/>
 * This interface defines the behavior to pick up limited history memory.<br/>
 * @author zhangxiaojia002
 * @date 2023/7/22 12:14 下午
 **/
public interface MemoryMessagePicker {

    /**
     * pick up memory messages as needed
     * @param memoryMessages all history memory messages
     * @return memory messages picked
     */
    List<BaseMessage> pickMemoryMessage(List<BaseMessage> memoryMessages);
}
