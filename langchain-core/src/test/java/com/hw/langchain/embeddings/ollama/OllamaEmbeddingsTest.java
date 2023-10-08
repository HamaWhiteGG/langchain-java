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

package com.hw.langchain.embeddings.ollama;

import lombok.var;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <a href="https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion">Ollama API reference</a>
 *
 * @author HamaWhite
 */
@Disabled("Test requires Ollama environment, can be run manually.")
class OllamaEmbeddingsTest {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaEmbeddingsTest.class);

    @Test
    void testOllamaEmbeddings() {
        var embeddings = OllamaEmbeddings.builder()
                .baseUrl("http://localhost:11434")
                .model("llama2")
                .build()
                .init();

        var result = embeddings.embedQuery("Here is an article about llamas...");
        LOG.info("Embeddings result: \n{}", result);

        assertNotNull(result);
        assertEquals(4096, result.size());
    }
}