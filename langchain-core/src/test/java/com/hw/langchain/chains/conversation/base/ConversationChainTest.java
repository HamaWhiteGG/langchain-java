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
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.memory.buffer.ConversationBufferMemory;
import com.hw.langchain.prompts.chat.ChatPromptTemplate;
import com.hw.langchain.prompts.chat.HumanMessagePromptTemplate;
import com.hw.langchain.prompts.chat.MessagesPlaceholder;
import com.hw.langchain.prompts.chat.SystemMessagePromptTemplate;

import lombok.var;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class ConversationChainTest {

    private static final Logger LOG = LoggerFactory.getLogger(ConversationChainTest.class);

    @Test
    void testConversationChainWithLLM() {
        var llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        var conversation = new ConversationChain(llm);

        var output = conversation.predict(MapUtil.of("input", "Hi there!"));
        LOG.info("Finished chain.\n{}", output);
        assertNotNull(output, "Output should not be null");

        output = conversation.predict(MapUtil.of("input", "I'm doing well! Just having a conversation with an AI."));
        LOG.info("Finished chain.\n{}", output);
        assertNotNull(output, "Output should not be null");
    }

    @Test
    void testConversationChainWithChatModel() {
        var prompt = ChatPromptTemplate.fromMessages(ListUtil.of(
                SystemMessagePromptTemplate.fromTemplate(
                        "The following is a friendly conversation between a human and an AI. The AI is talkative and " +
                                "provides lots of specific details from its context. If the AI does not know the " +
                                "answer to a question, it truthfully says it does not know."),
                new MessagesPlaceholder("history"),
                HumanMessagePromptTemplate.fromTemplate("{input}")));

        var chat = ChatOpenAI.builder().temperature(0).build().init();
        var memory = new ConversationBufferMemory(true);
        var conversation = new ConversationChain(chat, prompt, memory);

        var output1 = conversation.predict(MapUtil.of("input", "Hi there!"));
        // Hello! How can I assist you today?
        LOG.info("output1: \n{}", output1);
        assertNotNull(output1, "output1 should not be null");

        var output2 = conversation.predict(MapUtil.of("input", "I'm doing well! Just having a conversation with an AI."));
        // That sounds like fun! I'm happy to chat with you. What would you like to talk about?
        LOG.info("output2: \n{}", output2);
        assertNotNull(output2, "output2 should not be null");

        var output3 = conversation.predict(MapUtil.of("input", "Tell me about yourself."));
        // Sure! I am an AI language model created by OpenAI. I was trained on a large dataset ...
        LOG.info("output3: \n{}", output3);
        assertNotNull(output3, "output3 should not be null");
    }
}