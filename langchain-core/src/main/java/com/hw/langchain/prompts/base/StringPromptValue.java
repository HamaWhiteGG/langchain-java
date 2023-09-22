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

package com.hw.langchain.prompts.base;

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.HumanMessage;
import com.hw.langchain.schema.PromptValue;

import java.util.List;

/**
 * StringPromptValue
 * @author HamaWhite
 */
public class StringPromptValue implements PromptValue {

    private final String text;

    public StringPromptValue(String text) {
        this.text = text;
    }

    @Override
    public List<BaseMessage> toMessages() {
        return ListUtil.of(new HumanMessage(text));
    }

    /**
     * Return prompt as string.
     */
    @Override
    public String toString() {
        return text;
    }
}
