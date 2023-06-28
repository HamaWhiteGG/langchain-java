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

package com.hw.pinecone;

import com.hw.pinecone.entity.index.CreateIndexRequest;
import com.hw.pinecone.entity.index.Database;
import com.hw.pinecone.entity.index.IndexDescription;
import com.hw.pinecone.entity.index.Status;
import com.hw.pinecone.entity.vector.*;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.List;

import static com.hw.pinecone.entity.index.Metric.COSINE;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly Pinecone calls, can be run manually.")
class PineconeClientTest {

    private static PineconeClient client;

    private static IndexClient index;

    private static final String indexName = "index-temp";

    private static final String environment = "northamerica-northeast1-gcp";

    @BeforeAll
    static void setup() {
        client = PineconeClient.builder()
                .apiKey(System.getenv("PINECONE_API_KEY"))
                .environment(environment)
                .build()
                .init();

        // Create temporary index
        createTemporaryIndex();

        index = client.indexClient(indexName);
    }

    private static void createTemporaryIndex() {
        List<String> indexes = client.listIndexes();
        if (indexes.contains(indexName)) {
            return;
        }
        var request = CreateIndexRequest.builder()
                .name(indexName)
                .dimension(3)
                .build();
        client.createIndex(request);

        awaitIndexReady();
    }

    private static void awaitIndexReady() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    IndexDescription indexDescription = client.describeIndex(indexName);
                    return indexDescription != null && indexDescription.getStatus().isReady();
                });
    }

    @AfterAll
    static void cleanup() {
        // Delete temporary index
        client.deleteIndex(indexName);
        client.close();
    }

    @Test
    void testListIndexes() {
        List<String> indexes = client.listIndexes();
        assertTrue(indexes.contains(indexName));
    }

    @Test
    void testDescribeIndex() {
        IndexDescription indexDescription = client.describeIndex(indexName);
        assertNotNull(indexDescription);
        assertNotNull(indexDescription.getDatabase());
        assertNotNull(indexDescription.getStatus());

        // Assert database information
        Database database = indexDescription.getDatabase();
        assertAll(
                () -> assertEquals(indexName, database.getName()),
                () -> assertEquals(COSINE, database.getMetric()),
                () -> assertEquals(3, database.getDimension()),
                () -> assertEquals(1, database.getReplicas()),
                () -> assertEquals(1, database.getShards()),
                () -> assertEquals(1, database.getPods()),
                () -> assertEquals("p1.x1", database.getPodType()));

        // Assert status information
        Status status = indexDescription.getStatus();
        assertAll(
                () -> assertNotNull(status),
                () -> assertTrue(status.getWaiting().isEmpty()),
                () -> assertTrue(status.getCrashed().isEmpty()),
                () -> {
                    String host = String.format("%s-%s.svc.%s.pinecone.io", indexName, "b43e233", environment);
                    assertEquals(host, status.getHost());
                },
                () -> assertEquals(433, status.getPort()),
                () -> assertEquals("Ready", status.getState()),
                () -> assertTrue(status.isReady()));
    }

    @Test
    void testVectors() {
        Vector v1 = new Vector("v1", List.of(1F, 3F, 5F));
        Vector v2 = new Vector("v2", List.of(5F, 3F, 1F));
        UpsertRequest upsertRequest = new UpsertRequest(List.of(v1, v2));

        UpsertResponse upsertResponse = index.upsert(upsertRequest);
        assertNotNull(upsertResponse, "upsertResponse should not be null");

        QueryRequest queryRequest = QueryRequest.builder()
                .vector(List.of(1F, 2F, 2F))
                .topK(1)
                .build();

        QueryResponse queryResponse = index.query(queryRequest);
        assertNotNull(queryResponse, "queryResponse should not be null");

        FetchRequest fetchRequest = FetchRequest.builder()
                .ids(List.of("v1", "v2"))
                .build();
        FetchResponse fetchResponse = index.fetch(fetchRequest);
        assertNotNull(fetchResponse, "fetchResponse should not be null");
    }
}