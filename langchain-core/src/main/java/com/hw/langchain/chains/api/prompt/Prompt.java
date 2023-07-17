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

package com.hw.langchain.chains.api.prompt;

import com.hw.langchain.prompts.prompt.PromptTemplate;

import java.util.List;

/**
 * @author HamaWhite
 */
public class Prompt {

    private Prompt() {
    }

    private static final String API_URL_PROMPT_TEMPLATE =
            """
                        You are given the below API Documentation:
                    {api_docs}
                    Using this documentation, generate the full API url to call for answering the user question.
                    You should build the API url in order to get a response that is as short as possible, while still getting the necessary information to answer the question. Pay attention to deliberately exclude any unnecessary pieces of data in the API call.

                    Question:{question}
                    API url:""";

    public static final PromptTemplate API_URL_PROMPT =
            new PromptTemplate(List.of("api_docs", "question"), API_URL_PROMPT_TEMPLATE);

    private static final String API_RESPONSE_PROMPT_TEMPLATE =
            API_URL_PROMPT_TEMPLATE
                    + """
                            {api_url}

                            Here is the response from the API:

                            {api_response}

                            Summarize this response to answer the original question.

                            Summary:""";

    public static final PromptTemplate API_RESPONSE_PROMPT = new PromptTemplate(
            List.of("api_docs", "question", "api_url", "api_response"), API_RESPONSE_PROMPT_TEMPLATE);

}
