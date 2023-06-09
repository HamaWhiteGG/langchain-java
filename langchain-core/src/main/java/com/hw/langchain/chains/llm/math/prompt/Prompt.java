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

package com.hw.langchain.chains.llm.math.prompt;

import com.hw.langchain.prompts.prompt.PromptTemplate;

import java.util.List;

/**
 * @author HamaWhite
 */
public class Prompt {

    private static String _PROMPT_TEMPLATE =
            """
                    Translate a math problem into a expression that can be executed using Python's numexpr library. Use the output of running this code to answer the question.

                    Question: ${{Question with math problem.}}
                    ```text
                    ${{single line mathematical expression that solves the problem}}
                    ```
                    ...numexpr.evaluate(text)...
                    ```output
                    ${{Output of running the code}}
                    ```
                    Answer: ${{Answer}}

                    Begin.

                    Question: What is 37593 * 67?
                    ```text
                    37593 * 67
                    ```
                    ...numexpr.evaluate("37593 * 67")...
                    ```output
                    2518731
                    ```
                    Answer: 2518731

                    Question: 37593^(1/5)
                    ```text
                    37593**(1/5)
                    ```
                    ...numexpr.evaluate("37593**(1/5)")...
                    ```output
                    8.222831614237718
                    ```
                    Answer: 8.222831614237718

                    Question: {question}
                    """;

    public static PromptTemplate PROMPT =
            new PromptTemplate(List.of("question"), _PROMPT_TEMPLATE);

}
