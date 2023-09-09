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

package com.hw.langchain.examples.chains;

import com.hw.langchain.chains.summarize.SummarizeUtils;
import com.hw.langchain.document.loaders.WebBaseLoader;
import com.hw.langchain.llms.openai.OpenAIChat;

import java.util.List;

import static com.hw.langchain.examples.utils.PrintUtils.println;

/**
 * <a href="https://python.langchain.com/docs/use_cases/summarization">Summarization use cases</a>
 *
 * @author HamaWhite
 */
public class SummarizationExample {

    public static void main(String[] args) {
        var llm = OpenAIChat.builder()
                .temperature(0)
                .model("gpt-3.5-turbo-16k")
                .build()
                .init();

        var loader = new WebBaseLoader(List.of("https://lilianweng.github.io/posts/2023-06-23-agent/"));
        var docs = loader.load();

        var chain = SummarizeUtils.loadStuffChain(llm);
        var result = chain.run(docs);

        println(result);
    }
}
