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

package com.hw.langchain.examples.chat.models;

import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.examples.runner.RunnableExample;
import com.hw.langchain.schema.HumanMessage;

import java.util.List;

import static com.hw.langchain.examples.utils.PrintUtils.println;

/**
 * @author HamaWhite
 */
@RunnableExample
public class ChatExample {

    public static void main(String[] args) {
        var chat = ChatOpenAI.builder()
                .temperature(0)
                .build()
                .init();

        var result = chat.predictMessages(
                List.of(new HumanMessage("Translate this sentence from English to French. I love programming.")));
        println(result);

        var output = chat.predict("Translate this sentence from English to French. I love programming.");
        println(output);
    }
}
