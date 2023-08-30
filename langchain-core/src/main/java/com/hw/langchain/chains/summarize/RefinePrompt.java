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

package com.hw.langchain.chains.summarize;

import com.hw.langchain.prompts.prompt.PromptTemplate;

import java.util.List;

/**
 * @author HamaWhite
 */
public class RefinePrompt {

    private RefinePrompt() {
        throw new IllegalStateException("Utility class");
    }

    public static final PromptTemplate REFINE_PROMPT = new PromptTemplate(
            List.of("existing_answer", "text"),
            """
                    Your job is to produce a final summary
                    We have provided an existing summary up to a certain point: {existing_answer}
                    We have the opportunity to refine the existing summary
                    (only if needed) with some more context below.
                    ------------
                    {text}
                    ------------
                    Given the new context, refine the original summary
                    If the context isn't useful, return the original summary.
                    """);

    public static final PromptTemplate PROMPT = new PromptTemplate(
            List.of("text"),
            """
                    Write a concise summary of the following:

                    "{text}"

                    CONCISE SUMMARY:
                    """);
}
