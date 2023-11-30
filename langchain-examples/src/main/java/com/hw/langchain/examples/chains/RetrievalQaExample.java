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

import com.hw.langchain.chains.retrieval.qa.base.RetrievalQa;
import com.hw.langchain.document.loaders.text.TextLoader;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.text.splitter.CharacterTextSplitter;

import static com.hw.langchain.chains.ChainType.STUFF;
import static com.hw.langchain.examples.utils.PrintUtils.println;
import static com.hw.langchain.examples.vectorstores.PineconeExample.*;

/**
 * <a href="https://python.langchain.com/docs/modules/chains/popular/vector_db_qa">Retrieval QA</a>
 * <p>
 * export PINECONE_API_KEY=xxx
 * export PINECONE_ENV=xxx
 *
 * @author HamaWhite
 */
public class RetrievalQaExample {

    public static final String NAMESPACE = "txt";

    public static void main(String[] args) {
        var filePath = "data/extras/modules/state_of_the_union.txt";
        var loader = new TextLoader(filePath);
        var documents = loader.load();
        var textSplitter = CharacterTextSplitter.builder().chunkSize(1000).chunkOverlap(0).build();
        var docs = textSplitter.splitDocuments(documents);

        var pinecone = initializePineconeIndex(NAMESPACE, docs);
        var llm = OpenAI.builder().temperature(0).requestTimeout(30).build().init();
        var qa = RetrievalQa.fromChainType(llm, STUFF, pinecone.asRetriever());

        var query = "What did the president say about Ketanji Brown Jackson";
        var result = qa.run(query);
        println(result);
    }
}
