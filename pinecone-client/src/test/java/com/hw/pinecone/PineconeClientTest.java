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

import cn.hutool.core.collection.ListUtil;
import com.hw.pinecone.entity.index.CreateIndexRequest;
import com.hw.pinecone.entity.index.Database;
import com.hw.pinecone.entity.index.IndexDescription;
import com.hw.pinecone.entity.index.Status;
import com.hw.pinecone.entity.vector.*;

import lombok.var;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;

import javax.swing.plaf.ListUI;
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

    private static final String INDEX_NAME = "index-temp";

    private static final String NAMESPACE = "namespace-temp";

    @BeforeAll
    static void setup() {
        client = PineconeClient.builder()
                .requestTimeout(60)
                .build()
                .init();

        // Ensures that a temporary index is created.
        ensureTemporaryIndexCreated();

        index = client.indexClient(INDEX_NAME);
    }

    /**
     * Ensures that a temporary index is created.
     * If the index does not exist, it creates a new index with the specified name and dimension.
     * It also waits until the index is ready before returning.
     */
    private static void ensureTemporaryIndexCreated() {
        if (!client.listIndexes().contains(INDEX_NAME)) {
            var request = CreateIndexRequest.builder()
                    .name(INDEX_NAME)
                    .dimension(3)
                    .build();
            client.createIndex(request);

            awaitIndexReady();
        }
    }

    private static void awaitIndexReady() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(5))
                .until(() -> {
                    IndexDescription indexDescription = client.describeIndex(INDEX_NAME);
                    return indexDescription != null && indexDescription.getStatus().isReady();
                });
    }

    @AfterAll
    static void cleanup() {
        // Delete temporary index
        client.deleteIndex(INDEX_NAME);
        client.close();
    }

    @Test
    void testListIndexes() {
        List<String> indexes = client.listIndexes();
        assertTrue(indexes.contains(INDEX_NAME));
    }

    @Test
    void testDescribeIndex() {
        IndexDescription indexDescription = client.describeIndex(INDEX_NAME);
        assertNotNull(indexDescription);
        assertNotNull(indexDescription.getDatabase());
        assertNotNull(indexDescription.getStatus());

        // Assert database information
        Database database = indexDescription.getDatabase();
        assertAll(
                () -> assertEquals(INDEX_NAME, database.getName()),
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
                    String host = String.format("%s-%s.svc.%s.pinecone.io", INDEX_NAME, "b43e233",
                            System.getenv("PINECONE_ENV"));
                    assertEquals(host, status.getHost());
                },
                () -> assertEquals(433, status.getPort()),
                () -> assertEquals("Ready", status.getState()),
                () -> assertTrue(status.isReady()));
    }

    @Test
    void testVectors() {
        Vector v1 = new Vector("v1", ListUtil.of(1F, 3F, 5F));
        Vector v2 = new Vector("v2", ListUtil.of(5F, 3F, 1F));
        UpsertRequest upsertRequest = new UpsertRequest(ListUtil.of(v1, v2), NAMESPACE);

        UpsertResponse upsertResponse = index.upsert(upsertRequest);
        assertNotNull(upsertResponse, "upsertResponse should not be null");

        DescribeIndexStatsRequest statsRequest = new DescribeIndexStatsRequest();
        DescribeIndexStatsResponse statsResponse = index.describeIndexStats(statsRequest);
        assertNotNull(statsResponse, "statsResponse should not be null");
        assertTrue(statsResponse.getNamespaces().containsKey(NAMESPACE));

        QueryRequest queryRequest = QueryRequest.builder()
                .vector(ListUtil.of(1F, 2F, 2F))
                .topK(1)
                .namespace(NAMESPACE)
                .build();

        QueryResponse queryResponse = index.query(queryRequest);
        assertNotNull(queryResponse, "queryResponse should not be null");

        FetchRequest fetchRequest = FetchRequest.builder()
                .ids(ListUtil.of("v1", "v2"))
                .namespace(NAMESPACE)
                .build();
        FetchResponse fetchResponse = index.fetch(fetchRequest);
        assertNotNull(fetchResponse, "fetchResponse should not be null");
        assertEquals(2, fetchResponse.getVectors().size());

        DeleteRequest deleteRequest = DeleteRequest.builder()
                .ids(ListUtil.of("v1"))
                .namespace(NAMESPACE)
                .build();
        index.delete(deleteRequest);

        fetchResponse = index.fetch(fetchRequest);
        assertEquals(1, fetchResponse.getVectors().size());
    }
}