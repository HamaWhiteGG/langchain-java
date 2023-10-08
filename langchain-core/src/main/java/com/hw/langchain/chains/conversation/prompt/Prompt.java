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

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.utils.ResourceBundleUtils;

/**
 * @author HamaWhite
 */
public class Prompt {

    private static String DEFAULT_TEMPLATE = ResourceBundleUtils.getString("prompt.chain.chat.template");

    public static PromptTemplate PROMPT = new PromptTemplate(ListUtil.of("history", "input"), DEFAULT_TEMPLATE);
}
