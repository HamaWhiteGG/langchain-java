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
import com.hw.langchain.prompts.base.BasePromptTemplate;

/**
 * @author HamaWhite
 */
public abstract class BasePromptSelector {

    /**
     * Get default prompt for a language model.
     *
     * @param llm The BaseLanguageModel object.
     * @return The BasePromptTemplate object representing the default prompt.
     */
    public abstract BasePromptTemplate getPrompt(BaseLanguageModel llm);
}
