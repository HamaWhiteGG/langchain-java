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

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.combine.documents.stuff.StuffDocumentsChain;
import com.hw.langchain.chains.combine.documents.stuff.StuffUtils;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;

/**
 * @author HamaWhite
 */
public class SummarizeUtils {

    private SummarizeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static StuffDocumentsChain loadStuffChain(BaseLanguageModel llm) {
        return loadStuffChain(llm, StuffPrompt.PROMPT, "text", "\n\n");
    }

    public static StuffDocumentsChain loadStuffChain(BaseLanguageModel llm, BasePromptTemplate prompt,
            String documentVariableName, String documentSeparator) {
        LLMChain llmChain = new LLMChain(llm, prompt);
        return new StuffDocumentsChain(llmChain, StuffUtils.getDefaultDocumentPrompt(), documentVariableName,
                documentSeparator);
    }
}
