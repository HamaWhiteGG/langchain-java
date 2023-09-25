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

package com.hw.langchain.chains.retrieval.qa.base;

import com.hw.langchain.embeddings.openai.OpenAIEmbeddings;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.vectorstores.pinecone.Pinecone;
import com.hw.langchain.vectorstores.pinecone.PineconeTest;
import com.hw.pinecone.PineconeClient;

import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.hw.langchain.chains.ChainType.STUFF;
import static com.hw.langchain.vectorstores.pinecone.PineconeTest.INDEX_NAME;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <a href="https://python.langchain.com/docs/modules/chains/popular/vector_db_qa">Retrieval QA</a>
 *
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI and Pinecone calls, can be run manually.")
class RetrievalQATest {

    private OpenAIEmbeddings embeddings;

    private PineconeClient client;

    @BeforeEach
    void setup() {
        client = PineconeClient.builder()
                .requestTimeout(30)
                .build()
                .init();

        embeddings = OpenAIEmbeddings.builder()
                .model("text-embedding-ada-002")
                .requestTimeout(60)
                .build()
                .init();
    }

    private Pinecone createPinecone() {
        return Pinecone.builder()
                .client(client)
                .indexName(INDEX_NAME)
                .embeddingFunction(embeddings::embedQuery)
                .build()
                .init();
    }

    /**
     * Please run the {@link PineconeTest#testFromDocuments()} to write the text data into the Pinecone index.
     */
    @Test
    void testRetrievalQAFromPinecone() {
        var pinecone = createPinecone();

        var llm = OpenAI.builder().temperature(0).requestTimeout(30).build().init();
        var qa = RetrievalQa.fromChainType(llm, STUFF, pinecone.asRetriever());

        String query = "What did the president say about Ketanji Brown Jackson";
        var actual = qa.run(query);

        var expected = " The president said that Ketanji Brown Jackson is one of the nation's top legal minds, a " +
                "former top litigator in private practice, a former federal public defender, and from a family of " +
                "public school educators and police officers. He also said that she is a consensus builder and has " +
                "received a broad range of support from the Fraternal Order of Police to former judges appointed by " +
                "Democrats and Republicans.";
        assertEquals(expected, actual);
    }
}