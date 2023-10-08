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

package com.hw.langchain.prompts.chat;

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.exception.LangChainException;
import com.hw.langchain.prompts.base.StringPromptTemplate;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.schema.BaseMessage;

import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
public abstract class BaseStringMessagePromptTemplate extends BaseMessagePromptTemplate {

    protected StringPromptTemplate prompt;

    public static <T extends BaseStringMessagePromptTemplate> T fromTemplate(Class<T> cls, String template) {
        StringPromptTemplate prompt = PromptTemplate.fromTemplate(template);
        try {
            T instance = cls.getDeclaredConstructor().newInstance();
            instance.setPrompt(prompt);
            return instance;
        } catch (Exception e) {
            throw new LangChainException("Failed to create instance of BaseStringMessagePromptTemplate", e);
        }
    }

    /**
     * To a BaseMessage.
     */
    public abstract BaseMessage format(Map<String, Object> kwargs);

    @Override
    public List<BaseMessage> formatMessages(Map<String, Object> kwargs) {
        return ListUtil.of(this.format(kwargs));
    }

    @Override
    public List<String> inputVariables() {
        return prompt.getInputVariables();
    }

    public void setPrompt(StringPromptTemplate prompt) {
        this.prompt = prompt;
    }
}
