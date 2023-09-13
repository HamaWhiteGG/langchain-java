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

package com.hw.langchain.chains.conversation.base;

import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Sets;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.memory.buffer.ConversationBufferMemory;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.schema.BaseMemory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.hw.langchain.chains.conversation.prompt.Prompt.PROMPT;

/**
 * Chain to have a conversation and load context from memory.
 *
 * @author HamaWhite
 */
public class ConversationChain extends LLMChain {

    protected String inputKey = "input";

    public ConversationChain(BaseLanguageModel llm) {
        this(llm, PROMPT, new ConversationBufferMemory());
    }

    public ConversationChain(BaseLanguageModel llm, BasePromptTemplate prompt, BaseMemory memory) {
        super(llm, prompt, "response");
        this.memory = memory;

        validatePromptInputVariables();
    }

    /**
     * Use this since so some prompt vars come from history."
     */
    @Override
    public List<String> inputKeys() {
        return ListUtil.of(inputKey);
    }

    /**
     * Validate that prompt input variables are consistent.
     */
    public void validatePromptInputVariables() {
        List<String> memoryKeys = memory.memoryVariables();
        if (memoryKeys.contains(inputKey)) {
            throw new IllegalArgumentException(
                    String.format(
                            "The input key %s was also found in the memory keys %s - please provide keys that don't overlap.",
                            inputKey, memoryKeys));
        }
        List<String> promptVariables = prompt.getInputVariables();
        List<String> expectedKeys = new ArrayList<>(memoryKeys);
        expectedKeys.add(inputKey);
        if (!Sets.symmetricDifference(new HashSet<>(promptVariables), new HashSet<>(expectedKeys)).isEmpty()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Got unexpected prompt input variables. The prompt expects %s, but got %s as inputs from memory, and %s as the normal input key.",
                            promptVariables, memoryKeys, inputKey));
        }
    }
}
