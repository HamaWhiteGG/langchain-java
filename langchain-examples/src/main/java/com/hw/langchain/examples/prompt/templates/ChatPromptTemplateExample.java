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

package com.hw.langchain.examples.prompt.templates;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import com.hw.langchain.examples.runner.RunnableExample;
import com.hw.langchain.prompts.chat.ChatPromptTemplate;
import com.hw.langchain.prompts.chat.HumanMessagePromptTemplate;
import com.hw.langchain.prompts.chat.SystemMessagePromptTemplate;
import lombok.var;

import java.util.HashMap;

import static com.hw.langchain.examples.utils.PrintUtils.println;

/**
 * @author HamaWhite
 */
@RunnableExample
public class ChatPromptTemplateExample {

    public static void main(String[] args) {
        var template = "You are a helpful assistant that translates {input_language} to {output_language}.";
        var systemMessagePrompt = SystemMessagePromptTemplate.fromTemplate(template);

        var humanTemplate = "{text}";
        var humanMessagePrompt = HumanMessagePromptTemplate.fromTemplate(humanTemplate);

        var chatPrompt = ChatPromptTemplate.fromMessages(ListUtil.of(systemMessagePrompt, humanMessagePrompt));
        var output = chatPrompt.formatMessages(
                MapBuilder.create(new HashMap<String, Object>())
                .put("input_language", "English")
                .put("output_language", "French")
                .put("text", "I love programming.").map());
        println(output);
    }
}
