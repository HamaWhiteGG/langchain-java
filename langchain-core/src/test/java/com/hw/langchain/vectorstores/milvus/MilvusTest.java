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

package com.hw.langchain.vectorstores.milvus;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.embeddings.base.Embeddings;
import com.hw.langchain.schema.Document;
import com.hw.langchain.vectorstores.fake.embeddings.FakeEmbeddings;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.milvus.param.ConnectParam;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.vectorstores.fake.embeddings.FakeEmbeddings.FAKE_TEXTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Milvus functionality.
 * <p>
 * See the following documentation for how to run a Milvus instance:
 * <a href="https://milvus.io/docs/install_standalone-docker.md">install_standalone-docker</a>
 *
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI and Milvus environment, can be run manually.")
class MilvusTest {

    private Milvus milvusFromTexts(List<Map<String, Object>> metadatas, boolean dropOld) {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost("127.0.0.1")
                .withPort(19530)
                .build();

        Embeddings embedding = new FakeEmbeddings();
        Milvus milvus = Milvus.builder()
                .embeddingFunction(embedding)
                .connectParam(connectParam)
                .collectionName("LangChainCollection_1")
                .dropOld(dropOld)
                .build()
                .init();
        milvus.fromTexts(FAKE_TEXTS, embedding, metadatas);
        return milvus;
    }

    /**
     * Test end to end construction and search.
     */
    @Test
    void testMilvus() {
        Milvus docSearch = milvusFromTexts(ListUtil.of(), true);
        List<Document> output = docSearch.similaritySearch("foo", 1, MapUtil.empty());
        assertEquals(ListUtil.of(new Document("foo")), output);
    }
}