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

package com.hw.langchain.vectorstores.pinecone;

import com.hw.langchain.document.loaders.text.TextLoader;
import com.hw.langchain.embeddings.openai.OpenAIEmbeddings;
import com.hw.langchain.text.splitter.CharacterTextSplitter;
import com.hw.pinecone.PineconeClient;
import com.hw.pinecone.entity.index.CreateIndexRequest;
import com.hw.pinecone.entity.index.IndexDescription;

import lombok.var;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;

import java.time.Duration;

import static com.hw.langchain.vectorstores.base.SearchType.MMR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <a href="https://python.langchain.com/docs/modules/data_connection/vectorstores/integrations/pinecone">pinecone</a>
 *
 * @author HamaWhite
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Test requires costly OpenAI and Pinecone calls, can be run manually.")
public class PineconeTest {

    public static final String INDEX_NAME = "langchain-demo";

    private final String query = "What did the president say about Ketanji Brown Jackson";

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
     * Ensures index is created.
     * If the index does not exist, it creates a new index with the specified name and dimension.
     * It also waits until the index is ready before returning.
     */
    private void ensureIndexCreated() {
        if (!client.listIndexes().contains(INDEX_NAME)) {
            // the text-embedding-ada-002 model has an output dimension of 1536.
            var request = CreateIndexRequest.builder()
                    .name(INDEX_NAME)
                    .dimension(1536)
                    .build();
            client.createIndex(request);

            awaitIndexReady();
        }
    }

    private void awaitIndexReady() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    IndexDescription indexDescription = client.describeIndex(INDEX_NAME);
                    return indexDescription != null && indexDescription.getStatus().isReady();
                });
    }

    @Test
    @Order(1)
    public void testFromDocuments() {
        String filePath = "../docs/extras/modules/state_of_the_union.txt";
        var loader = new TextLoader(filePath);
        var documents = loader.load();
        var textSplitter = CharacterTextSplitter.builder().chunkSize(1000).chunkOverlap(0).build();
        var docs = textSplitter.splitDocuments(documents);

        // ensures index is created.
        ensureIndexCreated();

        var pinecone = createPinecone();
        int total = pinecone.fromDocuments(docs, embeddings);
        assertEquals(docs.size(), total);
    }

    @Test
    @Order(2)
    void testSimilaritySearch() {
        var pinecone = createPinecone();
        var docs = pinecone.similaritySearch(query);

        String expected =
                "" +
                        "     Tonight. I call on the Senate to: Pass the Freedom to Vote Act. Pass the John Lewis Voting Rights Act. And while you’re at it, pass the Disclose Act so Americans can know who is funding our elections.s\n" +
                        "\n" +
                        "                        Tonight, I’d like to honor someone who has dedicated his life to serve this country: Justice Stephen Breyer—an Army veteran, Constitutional scholar, and retiring Justice of the United States Supreme Court. Justice Breyer, thank you for your service.s\n" +
                        "\n" +
                        "                        One of the most serious constitutional responsibilities a President has is nominating someone to serve on the United States Supreme Court.s\n" +
                        "\n" +
                        "                        And I did that 4 days ago, when I nominated Circuit Court of Appeals Judge Ketanji Brown Jackson. One of our nation’s top legal minds, who will continue Justice Breyer’s legacy of excellence." +
                        "";

        assertThat(docs).isNotNull().hasSize(4);
        assertThat(docs.get(0).getPageContent()).isEqualTo(expected);
    }

    @Test
    @Order(3)
    void testGetRelevantDocuments() {
        var pinecone = createPinecone();
        var retriever = pinecone.asRetriever(MMR);

        var matchedDocs = retriever.getRelevantDocuments(query);
        assertThat(matchedDocs).isNotNull().hasSize(4);
    }

    @Test
    @Order(4)
    void testMaxMarginalRelevanceSearch() {
        var pinecone = createPinecone();
        var foundDocs = pinecone.maxMarginalRelevanceSearch(query, 2, 10, 0.5f);
        assertThat(foundDocs).isNotNull().hasSize(2);
    }
}