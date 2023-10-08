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

package com.hw.langchain.vectorstores.fake.embeddings;

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.embeddings.base.Embeddings;

import java.util.ArrayList;
import java.util.List;

/**
 * Fake embeddings functionality for testing.
 *
 * @author HamaWhite
 */
public class FakeEmbeddings implements Embeddings {

    public static final List<String> FAKE_TEXTS = ListUtil.of("foo", "bar", "baz");

    /**
     * Return simple embeddings. Embeddings encode each text as its index.
     */
    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        List<List<Float>> embeddings = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            List<Float> embedding = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                embedding.add(1.0f);
            }
            embedding.add((float) i);
            embeddings.add(embedding);
        }
        return embeddings;
    }

    /**
     * Return constant query embeddings. Embeddings are identical to embedDocuments(texts).get(0).
     * Distance to each text will be that text's index, as it was passed to embedDocuments.
     */
    @Override
    public List<Float> embedQuery(String text) {
        List<Float> embedding = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            embedding.add(1.0f);
        }
        embedding.add(0.0f);
        return embedding;
    }
}
