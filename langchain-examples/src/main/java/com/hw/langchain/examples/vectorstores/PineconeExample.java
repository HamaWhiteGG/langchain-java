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

package com.hw.langchain.examples.vectorstores;

import com.hw.langchain.embeddings.openai.OpenAIEmbeddings;
import com.hw.langchain.schema.Document;
import com.hw.langchain.vectorstores.pinecone.Pinecone;
import com.hw.pinecone.PineconeClient;
import com.hw.pinecone.entity.index.CreateIndexRequest;
import com.hw.pinecone.entity.index.IndexDescription;
import com.hw.pinecone.entity.vector.DescribeIndexStatsRequest;

import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.List;

/**
 * export PINECONE_API_KEY=xxx
 * export PINECONE_ENV=xxx
 *
 * @author HamaWhite
 */
public class PineconeExample {

    private PineconeExample() {
    }

    public static final String INDEX_NAME = "langchain-demo";

    /**
     * Initializes the Pinecone client, creates the index if necessary, and performs the desired operations.
     *
     * @param namespace the namespace
     * @param docs      the list of docs
     * @return the initialized Pinecone instance
     */
    public static Pinecone initializePineconeIndex(String namespace, List<Document> docs) {
        var client = PineconeClient.builder().requestTimeout(30).build().init();
        createPineconeIndex(client);

        var embeddings = OpenAIEmbeddings.builder().requestTimeout(60).build().init();
        var pinecone = Pinecone.builder()
                .client(client)
                .indexName(INDEX_NAME)
                .namespace(namespace)
                .embeddingFunction(embeddings::embedQuery)
                .build().init();

        var request = new DescribeIndexStatsRequest();
        var response = pinecone.getIndex().describeIndexStats(request);
        if (!response.getNamespaces().containsKey(namespace)) {
            pinecone.fromDocuments(docs, embeddings);
        }
        return pinecone;
    }

    /**
     * If the index does not exist, it creates a new index with the specified name and dimension.
     * It also waits until the index is ready before returning.
     *
     * @param client the PineconeClient instance
     */
    public static void createPineconeIndex(PineconeClient client) {
        if (!client.listIndexes().contains(INDEX_NAME)) {
            // the text-embedding-ada-002 model has an output dimension of 1536.
            var request = CreateIndexRequest.builder()
                    .name(INDEX_NAME)
                    .dimension(1536)
                    .build();
            client.createIndex(request);
            awaitIndexReady(client);
        }
    }

    private static void awaitIndexReady(PineconeClient client) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    IndexDescription indexDescription = client.describeIndex(INDEX_NAME);
                    return indexDescription != null && indexDescription.getStatus().isReady();
                });
    }
}
