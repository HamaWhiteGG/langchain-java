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

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.chains.prompt.selector.BasePromptSelector;
import com.hw.langchain.chains.prompt.selector.ConditionalPromptSelector;
import com.hw.langchain.chains.prompt.selector.PromptSelectorUtils;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.prompts.chat.ChatPromptTemplate;
import com.hw.langchain.prompts.chat.HumanMessagePromptTemplate;
import com.hw.langchain.prompts.chat.SystemMessagePromptTemplate;
import com.hw.langchain.prompts.prompt.PromptTemplate;

import com.hw.langchain.utils.ResourceBundleUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @author HamaWhite
 */
public class StuffPrompt {

    private StuffPrompt() {
    }

    private static final String PROMPT_TEMPLATE = ResourceBundleUtils.getString("prompt.chain.staff.template");

    public static final PromptTemplate PROMPT = new PromptTemplate(ListUtil.of("context", "question"), PROMPT_TEMPLATE);

    private static final String SYSTEM_TEMPLATE = ResourceBundleUtils.getString("prompt.chain.staff.system.template");

    private static final List<?> MESSAGES = ListUtil.of(
            SystemMessagePromptTemplate.fromTemplate(SYSTEM_TEMPLATE),
            HumanMessagePromptTemplate.fromTemplate("{question}"));

    private static final BasePromptTemplate CHAT_PROMPT = ChatPromptTemplate.fromMessages(MESSAGES);

    public static final BasePromptSelector PROMPT_SELECTOR =
            new ConditionalPromptSelector(PROMPT, ListUtil.of(Pair.of(PromptSelectorUtils::isChatModel, CHAT_PROMPT)));

}
