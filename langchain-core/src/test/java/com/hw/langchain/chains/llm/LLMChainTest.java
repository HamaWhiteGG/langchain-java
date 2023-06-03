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

package com.hw.langchain.chains.llm;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.prompts.prompt.PromptTemplate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @description: LLMChainTest
 * @author: HamaWhite
 */
class LLMChainTest {

    private static BaseLanguageModel llm;

    @BeforeAll
    public static void setup() throws SQLException {
        llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();
    }

    @Test
    void testLLMChainWithOneInputVariables() {
        PromptTemplate prompt = new PromptTemplate(List.of("product"),
                "What is a good name for a company that makes {product}?");

        Chain chain = new LLMChain(llm, prompt);
        String actual = chain.run("colorful socks");

        String expected = "\n\nSocktastic!";
        assertEquals(expected, actual);
    }

    @Test
    void testLLMChainWithMultipleInputVariables() {
        PromptTemplate prompt = new PromptTemplate(List.of("company", "product"),
                "What is a good name for {company} that makes {product}?");

        Chain chain = new LLMChain(llm, prompt);
        String actual = chain.run(Map.of("company", "ABC Startup", "product", "colorful socks"));

        String expected = "\n\nSocktastic!";
        assertEquals(expected, actual);
    }
}