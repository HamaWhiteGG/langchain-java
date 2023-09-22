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

package com.hw.langchain.examples.memory;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.chains.conversation.base.ConversationChain;
import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.examples.runner.RunnableExample;
import com.hw.langchain.memory.buffer.ConversationBufferMemory;
import com.hw.langchain.prompts.chat.ChatPromptTemplate;
import com.hw.langchain.prompts.chat.HumanMessagePromptTemplate;
import com.hw.langchain.prompts.chat.MessagesPlaceholder;
import com.hw.langchain.prompts.chat.SystemMessagePromptTemplate;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.examples.utils.PrintUtils.println;

/**
 * @author HamaWhite
 */
@RunnableExample
public class ChatMemoryExample {

    public static void main(String[] args) {
        ChatPromptTemplate prompt = ChatPromptTemplate.fromMessages(ListUtil.of(
                SystemMessagePromptTemplate.fromTemplate(
                        "The following is a friendly conversation between a human and an AI. The AI is talkative and " +
                                "provides lots of specific details from its context. If the AI does not know the " +
                                "answer to a question, it truthfully says it does not know."),
                new MessagesPlaceholder("history"),
                HumanMessagePromptTemplate.fromTemplate("{input}")));

        ChatOpenAI chat = ChatOpenAI.builder().temperature(0).build().init();
        ConversationBufferMemory memory = new ConversationBufferMemory(true);
        ConversationChain conversation = new ConversationChain(chat, prompt, memory);

        String output = conversation.predict(MapUtil.of("input", "Hi there!"));
        println(output);

        output = conversation.predict(MapUtil.of("input", "I'm doing well! Just having a conversation with an AI."));
        println(output);

        output = conversation.predict(MapUtil.of("input", "Tell me about yourself."));
        println(output);
    }
}
