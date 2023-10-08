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

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.document.loaders.WebBaseLoader;
import com.hw.langchain.llms.openai.OpenAIChat;
import com.hw.langchain.schema.Document;

import lombok.var;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <a href="https://python.langchain.com/docs/use_cases/summarization">Summarization use cases</a>
 *
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class SummarizeUtilsTest {

    private static BaseLanguageModel llm;

    private static List<Document> docs;

    @BeforeAll
    static void setUp() {
        llm = OpenAIChat.builder()
                .temperature(0)
                .model("gpt-3.5-turbo-16k")
                .build()
                .init();

        var loader = new WebBaseLoader(ListUtil.of("https://lilianweng.github.io/posts/2023-06-23-agent/"));
        docs = loader.load();
    }

    @Test
    void testLoadStuffChain() {
        var chain = SummarizeUtils.loadStuffChain(llm);
        var actual = chain.run(docs);

        var expected =
                "The article discusses the concept of building autonomous agents powered by large language models " +
                        "(LLMs). It explores the components of such agents, including planning, memory, and tool " +
                        "use. The article provides case studies and proof-of-concept examples of LLM-powered agents, " +
                        "as well as challenges and limitations associated with their development.";
        assertEquals(expected, actual);
    }
}