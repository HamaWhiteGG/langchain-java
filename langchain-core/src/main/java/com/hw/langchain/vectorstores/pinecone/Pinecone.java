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

import com.hw.langchain.schema.Document;
import com.hw.langchain.vectorstores.base.VectorStore;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author HamaWhite
 */
public class Pinecone extends VectorStore {

    private Object index;

    private Function<String, float[]> embeddingFunction;

    private String textKey;

    private String namespace;

    @Override
    public List<String> addTexts(List<String> texts, List<Map<String, Object>> metadatas, Map<String, Object> kwargs) {
        return null;
    }

    @Override
    public boolean delete(List<String> ids) {
        return false;
    }

    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> kwargs) {
        return null;
    }

    @Override
    protected List<Pair<Document, Float>> _similaritySearchWithRelevanceScores(String query, int k,
            Map<String, Object> kwargs) {
        return null;
    }

    @Override
    public List<Document> similarSearchByVector(List<Float> embedding, int k, Map<String, Object> kwargs) {
        return null;
    }

    @Override
    public List<Document> maxMarginalRelevanceSearch(String query, int k, int fetchK, float lambdaMult,
            Map<String, Object> kwargs) {
        return null;
    }
}
