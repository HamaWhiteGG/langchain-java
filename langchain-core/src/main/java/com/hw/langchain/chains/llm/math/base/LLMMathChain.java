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

package com.hw.langchain.chains.llm.math.base;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.chains.llm.math.prompt.Prompt.PROMPT;

/**
 * Chain that interprets a prompt and executes python code to do math.
 *
 * @author HamaWhite
 */
public class LLMMathChain extends Chain {

    private static final Logger LOG = LoggerFactory.getLogger(LLMMathChain.class);

    private LLMChain llmChain;

    private String inputKey = "question";

    private String outputKey = "answer";

    public LLMMathChain(LLMChain llmChain) {
        this.llmChain = llmChain;
    }

    public static LLMMathChain fromLLM(BaseLanguageModel llm) {
        return fromLLM(llm, PROMPT);
    }

    public static LLMMathChain fromLLM(BaseLanguageModel llm, BasePromptTemplate prompt) {
        LLMChain llmChain = new LLMChain(llm, prompt);
        return new LLMMathChain(llmChain);
    }

    @Override
    public String chainType() {
        return null;
    }

    @Override
    public List<String> inputKeys() {
        return null;
    }

    @Override
    public List<String> outputKeys() {
        return null;
    }

    @Override
    public Map<String, String> _call(Map<String, Object> inputs) {
        return null;
    }
}
