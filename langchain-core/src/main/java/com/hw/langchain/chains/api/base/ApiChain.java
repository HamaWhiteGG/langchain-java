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

package com.hw.langchain.chains.api.base;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.requests.TextRequestsWrapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hw.langchain.chains.api.prompt.Prompt.API_RESPONSE_PROMPT;
import static com.hw.langchain.chains.api.prompt.Prompt.API_URL_PROMPT;

/**
 * Chain that makes API calls and summarizes the responses to answer a question.
 *
 * @author HamaWhite
 */
public class ApiChain extends Chain {

    private final LLMChain apiRequestChain;

    private final LLMChain apiAnswerChain;

    private final TextRequestsWrapper requestsWrapper;

    private final String apiDocs;

    private static final String QUESTION_KEY = "question";

    private static final String OUTPUT_KEY = "output";

    private static final String API_DOCS = "api_docs";

    public ApiChain(LLMChain apiRequestChain, LLMChain apiAnswerChain, TextRequestsWrapper requestsWrapper,
            String apiDocs) {
        this.apiRequestChain = apiRequestChain;
        this.apiAnswerChain = apiAnswerChain;
        this.requestsWrapper = requestsWrapper;
        this.apiDocs = apiDocs;

        // check that api request prompt expects the right variables.
        validateApiRequestPrompt();
        // check that api answer prompt expects the right variables.
        validateApiAnswerPrompt();
    }

    /**
     * Check that api request prompt expects the right variables.
     */
    private void validateApiRequestPrompt() {
        List<String> inputVars = apiRequestChain.getPrompt().getInputVariables();
        Set<String> inputVarsSet = new HashSet<>(inputVars);

        Set<String> expectedVars = Set.of(QUESTION_KEY, API_DOCS);
        if (!inputVarsSet.equals(expectedVars)) {
            throw new IllegalArgumentException("Input variables should be " + expectedVars + ", got " + inputVars);
        }
    }

    /**
     * Check that api answer prompt expects the right variables.
     */
    private void validateApiAnswerPrompt() {
        List<String> inputVars = apiAnswerChain.getPrompt().getInputVariables();
        Set<String> inputVarsSet = new HashSet<>(inputVars);

        Set<String> expectedVars = Set.of(QUESTION_KEY, API_DOCS, "api_url", "api_response");
        if (!inputVarsSet.equals(expectedVars)) {
            throw new IllegalArgumentException("Input variables should be " + expectedVars + ", got " + inputVars);
        }
    }

    @Override
    public List<String> inputKeys() {
        return List.of(QUESTION_KEY);
    }

    @Override
    public List<String> outputKeys() {
        return List.of(OUTPUT_KEY);
    }

    @Override
    protected Map<String, String> innerCall(Map<String, Object> inputs) {
        var question = inputs.get(QUESTION_KEY);
        String apiUrl = apiRequestChain.predict(Map.of(QUESTION_KEY, question, API_DOCS, apiDocs));
        apiUrl = apiUrl.strip();

        String apiResponse = requestsWrapper.get(apiUrl);
        String answer = apiAnswerChain.predict(Map.of(QUESTION_KEY, question, API_DOCS, apiDocs,
                "api_url", apiUrl, "api_response", apiResponse));
        return Map.of(OUTPUT_KEY, answer);
    }

    public static ApiChain fromLlmAndApiDocs(BaseLanguageModel llm, String apiDocs) {
        return fromLlmAndApiDocs(llm, apiDocs, null, API_URL_PROMPT, API_RESPONSE_PROMPT);
    }

    public static ApiChain fromLlmAndApiDocs(BaseLanguageModel llm, String apiDocs, Map<String, String> headers,
            BasePromptTemplate apiUrlPrompt, BasePromptTemplate apiResponsePrompt) {
        LLMChain getRequestChain = new LLMChain(llm, apiUrlPrompt);
        TextRequestsWrapper requestsWrapper = new TextRequestsWrapper(headers);
        LLMChain getAnswerChain = new LLMChain(llm, apiResponsePrompt);
        return new ApiChain(getRequestChain, getAnswerChain, requestsWrapper, apiDocs);
    }

    @Override
    public String chainType() {
        return "api_chain";
    }
}
