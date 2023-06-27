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

package com.hw.langchain.vectorstores.base;

import com.hw.langchain.schema.BaseRetriever;
import com.hw.langchain.schema.Document;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
public class VectorStoreRetriever implements BaseRetriever {

    private static final List<String> ALLOWED_SEARCH_TYPES = List.of(
            "similarity",
            "similarity_score_threshold",
            "mmr");

    private VectorStore vectorstore;

    private String searchType = "similarity";

    private Map<String, Object> searchKwargs;

    public VectorStoreRetriever(VectorStore vectorstore, Map<String, Object> searchKwargs) {
        this.vectorstore = vectorstore;
        this.searchKwargs = searchKwargs;

        validateSearchType();
    }

    private void validateSearchType() {
        if (!ALLOWED_SEARCH_TYPES.contains(searchType)) {
            throw new IllegalArgumentException(
                    "searchType of " + searchType + " not allowed. Valid values are: " + ALLOWED_SEARCH_TYPES);
        }
        if ("similarity_score_threshold".equals(searchType)) {
            Object scoreThreshold = searchKwargs.get("score_threshold");
            if (!(scoreThreshold instanceof Float)) {
                throw new IllegalArgumentException(
                        "`score_threshold` is not specified with a float value(0~1) in `searchKwargs`.");
            }
        }
    }

    @Override
    public List<Document> getRelevantDocuments(String query) {
        return switch (searchType) {
            case "similarity" -> vectorstore.similaritySearch(query, searchKwargs);
            case "similarity_score_threshold" -> vectorstore.similaritySearchWithRelevanceScores(query, searchKwargs)
                    .stream()
                    .map(Pair::getLeft)
                    .toList();
            case "mmr" -> vectorstore.maxMarginalRelevanceSearch(query, searchKwargs);
            default -> throw new IllegalArgumentException("searchType of " + searchType + " not allowed.");
        };
    }

    /**
     * Add documents to vectorStore.
     */
    public List<String> addDocuments(List<Document> documents, Map<String, Object> kwargs) {
        return vectorstore.addDocuments(documents, kwargs);
    }
}
