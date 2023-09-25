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

package com.hw.langchain.prompts.prompt;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <a href="https://python.langchain.com/en/latest/modules/prompts/prompt_templates/getting_started.html">Prompt Templates Started </a>
 *
 * PromptTemplateTest
 * @author HamaWhite
 */
class PromptTemplateTest {

    @Test
    void testPromptTemplate() {
        String template = "" +
                "I want you to act as a naming consultant for new companies.\n" +
                "                What is a good name for a company that makes {product}?" +
                "";

        PromptTemplate prompt = new PromptTemplate(ListUtil.of("product"), template);
        String actual = prompt.format(MapUtil.of("product", "colorful socks"));

        String expected = "" +
                " I want you to act as a naming consultant for new companies.\n" +
                "                What is a good name for a company that makes colorful socks?" +
                "";
        assertEquals(expected, actual);
    }

    /**
     * An example prompt with no input variables.
     */
    @Test
    void testPromptWithNoInputVariables() {
        PromptTemplate noInputPrompt = new PromptTemplate(ListUtil.of(), "Tell me a joke.");

        String actual = noInputPrompt.format(MapUtil.empty());
        String expected = "Tell me a joke.";
        assertEquals(expected, actual);
    }

    /**
     * An example prompt with one input variable.
     */
    @Test
    void testPromptWithOneInputVariables() {
        PromptTemplate oneInputPrompt = new PromptTemplate(ListUtil.of("adjective"),
                "Tell me a {adjective} joke.");

        String actual = oneInputPrompt.format(MapUtil.of("adjective", "funny"));
        String expected = "Tell me a funny joke.";
        assertEquals(expected, actual);
    }

    /**
     * An example prompt with multiple input variable.
     */
    @Test
    void testPromptWithMultipleInputVariables() {
        PromptTemplate oneInputPrompt =
                new PromptTemplate(ListUtil.of("adjective", "content"),
                        "Tell me a {adjective} joke about {content}.");

        String actual = oneInputPrompt.format(MapBuilder.create(new HashMap<String, Object>())
                .put("adjective", "funny")
                .put("content", "chickens").map());
        String expected = "Tell me a funny joke about chickens.";
        assertEquals(expected, actual);
    }

    @Test
    void testInferInputVariablesFromTemplate() {
        String template = "Tell me a {adjective} joke about {content}.";

        PromptTemplate promptTemplate = PromptTemplate.fromTemplate(template);
        assertEquals(ListUtil.of("adjective", "content"), promptTemplate.getInputVariables());

        String actual = promptTemplate.format(MapBuilder.create(new HashMap<String, Object>())
                .put("adjective", "funny")
                .put("content", "chickens").map());
        String expected = "Tell me a funny joke about chickens.";
        assertEquals(expected, actual);
    }
}