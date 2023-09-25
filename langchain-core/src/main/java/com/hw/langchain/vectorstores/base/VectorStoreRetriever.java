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

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.hw.langchain.schema.BaseRetriever;
import com.hw.langchain.schema.Document;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hw.langchain.vectorstores.base.SearchType.SIMILARITY_SCORE_THRESHOLD;

/**
 * @author HamaWhite
 */
public class VectorStoreRetriever implements BaseRetriever {

    private final VectorStore vectorstore;

    private final SearchType searchType;

    private final Map<String, Object> searchKwargs;

    public VectorStoreRetriever(VectorStore vectorstore, SearchType searchType) {
        this(vectorstore, searchType, null);
    }

    public VectorStoreRetriever(VectorStore vectorstore, SearchType searchType, Map<String, Object> searchKwargs) {
        this.vectorstore = vectorstore;
        this.searchType = searchType;
        this.searchKwargs = searchKwargs;

        validateSearchType();
    }

    private void validateSearchType() {
        if (SIMILARITY_SCORE_THRESHOLD.equals(searchType)) {
            Object scoreThreshold = searchKwargs.get("score_threshold");
            if (!(scoreThreshold instanceof Float)) {
                throw new IllegalArgumentException(
                        "`score_threshold` is not specified with a float value(0~1) in `searchKwargs`.");
            }
        }
    }

    @Override
    public List<Document> getRelevantDocuments(String query) {
        switch (searchType) {
            case SIMILARITY :
                return vectorstore.similaritySearch(query, MapUtil.empty());
            case SIMILARITY_SCORE_THRESHOLD :
                return vectorstore.similaritySearchWithRelevanceScores(query)
                    .stream()
                    .map(Pair::getLeft)
                    .collect(Collectors.toList());
            case MMR :
                return vectorstore.maxMarginalRelevanceSearch(query);
            default:
                return Lists.newArrayList();
        }
    }

    /**
     * Add documents to vectorStore.
     */
    public List<String> addDocuments(List<Document> documents, Map<String, Object> kwargs) {
        return vectorstore.addDocuments(documents, kwargs);
    }
}
