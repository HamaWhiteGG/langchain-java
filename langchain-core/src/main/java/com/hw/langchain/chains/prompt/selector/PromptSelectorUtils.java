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

package com.hw.langchain.chains.prompt.selector;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chat.models.base.BaseChatModel;
import com.hw.langchain.llms.base.BaseLLM;

/**
 * @author HamaWhite
 */
public class PromptSelectorUtils {

    private PromptSelectorUtils() {
    }

    /**
     * Check if the language model is a LLM.
     *
     * @param llm The language model to check.
     * @return true if the language model is a BaseLLM model, false otherwise.
     */
    public static boolean isLLM(BaseLanguageModel llm) {
        return llm instanceof BaseLLM;
    }

    /**
     * Check if the language model is a chat model.
     *
     * @param llm The language model to check.
     * @return true if the language model is a BaseChatModel model, false otherwise.
     */
    public static boolean isChatModel(BaseLanguageModel llm) {
        return llm instanceof BaseChatModel;
    }
}
