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

package com.hw.langchain.prompts.chat;

import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.HumanMessage;
import com.hw.langchain.schema.SystemMessage;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author HamaWhite
 */
class ChatPromptTemplateTest {

    @Test
    void testFormatMessages() {
        var template = "You are a helpful assistant that translates {input_language} to {output_language}.";
        var systemMessagePrompt = SystemMessagePromptTemplate.fromTemplate(template);

        var humanTemplate = "{text}";
        var humanMessagePrompt = HumanMessagePromptTemplate.fromTemplate(humanTemplate);

        var chatPrompt = ChatPromptTemplate.fromMessages(List.of(systemMessagePrompt, humanMessagePrompt));
        List<BaseMessage> actual = chatPrompt.formatMessages(Map.of("input_language", "English",
                "output_language", "French",
                "text", "I love programming."));

        List<BaseMessage> expected = List.of(
                new SystemMessage("You are a helpful assistant that translates English to French."),
                new HumanMessage("I love programming."));
        assertEquals(expected, actual);
    }
}