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

import com.hw.pinecone.entity.index.CreateIndexCmd;
import com.hw.pinecone.entity.index.Database;
import com.hw.pinecone.entity.index.IndexDescription;
import com.hw.pinecone.entity.index.Status;

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

    private PineconeClient client;

    private final String indexName = "index-temp";

    private final String environment = "northamerica-northeast1-gcp";

    @BeforeEach
    void setup() {
        client = PineconeClient.builder()
                .apiKey(System.getenv("PINECONE_API_KEY"))
                .environment(environment)
                .build()
                .init();

        // Create temporary index
        createTemporaryIndex();
    }

    private void createTemporaryIndex() {
        var command = CreateIndexCmd.builder()
                .name(indexName)
                .dimension(5)
                .build();
        client.createIndex(command);
    }

    @AfterEach
    void cleanup() {
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
        assertTimeoutPreemptively(Duration.ofSeconds(120), () -> {
            IndexDescription indexDescription = null;
            while (indexDescription == null || !indexDescription.getStatus().isReady()) {
                Thread.sleep(3000);
                indexDescription = client.describeIndex(indexName);
            }


            assertNotNull(indexDescription);
            assertNotNull(indexDescription.getDatabase());
            assertNotNull(indexDescription.getStatus());

            // Assert database information
            Database database = indexDescription.getDatabase();
            assertAll(
                    () -> assertEquals(indexName, database.getName()),
                    () -> assertEquals(COSINE, database.getMetric()),
                    () -> assertEquals(5, database.getDimension()),
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
        });
    }
}