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

package com.hw.langchain.chains.conversation.prompt;

import com.hw.langchain.prompts.prompt.PromptTemplate;

import java.util.List;

/**
 * @author HamaWhite
 */
public class Prompt {

    private static String DEFAULT_TEMPLATE =
            """
                    The following is a friendly conversation between a human and an AI. The AI is talkative and provides lots of specific details from its context. If the AI does not know the answer to a question, it truthfully says it does not know.

                    Current conversation:
                    {history}
                    Human: {input}
                    AI:""";

    public static PromptTemplate PROMPT = new PromptTemplate(List.of("history", "input"), DEFAULT_TEMPLATE);
}
