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

package com.hw.langchain.memory.chat.memory;

import com.hw.langchain.memory.chat.message.histories.in.memory.ChatMessageHistory;
import com.hw.langchain.schema.BaseChatMessageHistory;
import com.hw.langchain.schema.BaseMemory;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Map;

import static com.hw.langchain.output.parsers.list.Utils.getPromptInputKey;

/**
 * @author HamaWhite
 */
public abstract class BaseChatMemory implements BaseMemory {

    protected BaseChatMessageHistory chatMemory = new ChatMessageHistory();

    protected String outputKey;

    protected String inputKey;

    protected boolean returnMessages;

    protected BaseChatMemory() {

    }

    protected BaseChatMemory(BaseChatMessageHistory chatMemory) {
        this.chatMemory = chatMemory;
    }

    private Pair<String, String> getInputOutput(Map<String, Object> inputs, Map<String, String> outputs) {
        String promptInputKey;
        if (inputKey == null) {
            promptInputKey = getPromptInputKey(inputs, memoryVariables());
        } else {
            promptInputKey = inputKey;
        }

        String tmpOutputKey;
        if (outputKey == null) {
            if (outputs.size() != 1) {
                throw new IllegalArgumentException("One output key expected, got " + outputs.size());
            }
            tmpOutputKey = new ArrayList<>(outputs.keySet()).get(0);
        } else {
            tmpOutputKey = this.outputKey;
        }

        String inputStr = inputs.get(promptInputKey).toString();
        String outputStr = outputs.get(tmpOutputKey);

        return Pair.of(inputStr, outputStr);
    }

    @Override
    public void saveContext(Map<String, Object> inputs, Map<String, String> outputs) {
        Pair<String, String> inputOutputPair = getInputOutput(inputs, outputs);
        String inputStr = inputOutputPair.getLeft();
        String outputStr = inputOutputPair.getRight();

        chatMemory.addUserMessage(inputStr);
        chatMemory.addAIMessage(outputStr);
    }

    @Override
    public void clear() {
        chatMemory.clear();
    }
}
