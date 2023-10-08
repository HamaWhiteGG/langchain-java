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

package com.hw.langchain.chains.question.answering;

import cn.hutool.core.map.MapUtil;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.ChainType;
import com.hw.langchain.chains.combine.documents.base.BaseCombineDocumentsChain;
import com.hw.langchain.chains.combine.documents.stuff.StuffDocumentsChain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;

import java.util.Map;
import java.util.function.Function;

import static com.hw.langchain.chains.ChainType.STUFF;
import static com.hw.langchain.chains.question.answering.StuffPrompt.PROMPT_SELECTOR;

/**
 * @author HamaWhite
 */
public class Init {

    private Init() {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<ChainType, Function<BaseLanguageModel, BaseCombineDocumentsChain>> LOADER_MAPPING = MapUtil.of(
            STUFF, Init::loadStuffChain);

    public static StuffDocumentsChain loadStuffChain(BaseLanguageModel llm) {
        return loadStuffChain(llm, PROMPT_SELECTOR.getPrompt(llm), "context");
    }

    public static StuffDocumentsChain loadStuffChain(BaseLanguageModel llm, BasePromptTemplate prompt,
            String documentVariableName) {
        LLMChain llmChain = new LLMChain(llm, prompt);
        return new StuffDocumentsChain(llmChain, documentVariableName);
    }

    public static BaseCombineDocumentsChain loadQaChain(BaseLanguageModel llm) {
        return loadQaChain(llm, STUFF);
    }

    public static BaseCombineDocumentsChain loadQaChain(BaseLanguageModel llm, ChainType chainType) {
        return LOADER_MAPPING.get(chainType).apply(llm);
    }
}
