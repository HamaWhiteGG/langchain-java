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

package com.hw.openai.example.completions;

import com.hw.langchain.examples.runner.RunnableExample;
import com.hw.openai.OpenAiClient;
import com.hw.openai.entity.completions.Completion;
import com.hw.openai.entity.completions.CompletionResp;

import java.util.List;

import static com.hw.langchain.examples.utils.PrintUtils.println;

/**
 * @author HamaWhite
 */
@RunnableExample
public class CompletionExample {

    public static void main(String[] args) {
        OpenAiClient client = OpenAiClient.builder()
                .requestTimeout(120)
                .build()
                .init();

        Completion completion = Completion.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(List.of("Introduce West Lake in Hangzhou, China."))
                .maxTokens(1000)
                .temperature(0)
                .build();

        CompletionResp response = client.createCompletion(completion);
        println(response.getChoices().get(0).getText());

        client.close();
    }
}
