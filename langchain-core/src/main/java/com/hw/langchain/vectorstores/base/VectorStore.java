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

import com.hw.langchain.embeddings.base.Embeddings;
import com.hw.langchain.schema.Document;

import lombok.var;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hw.langchain.vectorstores.base.SearchType.SIMILARITY;

/**
 * @author HamaWhite
 */
public abstract class VectorStore {

    private static final Logger LOG = LoggerFactory.getLogger(VectorStore.class);

    /**
     * Run more texts through the embeddings and add to the vectorStore.
     *
     * @param texts     Iterable of strings to add to the vectorStore.
     * @param metadatas list of metadatas associated with the texts.
     * @return List of ids from adding the texts into the vectorStore.
     */
    public abstract List<String> addTexts(List<String> texts, List<Map<String, Object>> metadatas);

    /**
     * Delete by vector ID.
     *
     * @param ids List of ids to delete.
     */
    public abstract void delete(List<String> ids);

    /**
     * Run more documents through the embeddings and add to the vectorStore.
     *
     * @param documents Documents to add to the vectorStore.
     * @param kwargs    vectorStore specific parameters
     * @return List of IDs of the added texts.
     */
    public List<String> addDocuments(List<Document> documents, Map<String, Object> kwargs) {
        var texts = documents.stream().map(Document::getPageContent).collect(Collectors.toList());
        var metadatas = documents.stream().map(Document::getMetadata).collect(Collectors.toList());
        return addTexts(texts, metadatas);
    }

    public List<Document> search(String query, SearchType searchType, Map<String, Object> filter) {
        switch (searchType) {
            case SIMILARITY :
                return similaritySearch(query, filter);
            case MMR :
                return maxMarginalRelevanceSearch(query);
            default :
                throw new IllegalArgumentException(
                    "searchType of " + searchType + " not allowed. Expected searchType to be 'similarity' or 'mmr'.");
        }
    }

    /**
     * Returns the documents most similar to the given query.
     *
     * @param query  the input text
     * @return a list of tuples containing the documents and their similarity scores
     */
    public List<Document> similaritySearch(String query) {
        return similaritySearch(query, null);
    }

    /**
     * Returns the documents most similar to the given query.
     *
     * @param query  the input text
     * @param filter a filter to apply to the search
     * @return a list of tuples containing the documents and their similarity scores
     */
    public List<Document> similaritySearch(String query, Map<String, Object> filter) {
        return similaritySearch(query, 4, filter);
    }

    /**
     * Returns the documents most similar to the given query.
     *
     * @param query  the input text
     * @param k      the number of documents to return
     * @param filter a filter to apply to the search
     * @return a list of tuples containing the documents and their similarity scores
     */
    public abstract List<Document> similaritySearch(String query, int k, Map<String, Object> filter);

    /**
     * Return docs and relevance scores in the range [0, 1]. 0 is dissimilar, 1 is most similar.
     * @param query input text
     */
    public List<Pair<Document, Float>> similaritySearchWithRelevanceScores(String query) {
        return similaritySearchWithRelevanceScores(query, 4);
    }

    /**
     * Return docs and relevance scores in the range [0, 1]. 0 is dissimilar, 1 is most similar.
     *
     * @param query input text
     * @param k     Number of Documents to return.
     * @return List of Tuples of (doc, similarityScore)
     */
    public List<Pair<Document, Float>> similaritySearchWithRelevanceScores(String query, int k) {
        List<Pair<Document, Float>> docsAndSimilarities = innerSimilaritySearchWithRelevanceScores(query, k);

        // Check relevance scores and filter by threshold
        if (docsAndSimilarities.stream().anyMatch(pair -> pair.getRight() < 0.0f || pair.getRight() > 1.0f)) {
            LOG.warn("Relevance scores must be between 0 and 1, got {} ", docsAndSimilarities);
        }
        return docsAndSimilarities;
    }

    /**
     * Return docs and relevance scores, normalized on a scale from 0 to 1. 0 is dissimilar, 1 is most similar.
     *
     * @param query input text
     * @param k     Number of Documents to return.
     * @return List of Tuples of (doc, similarityScore)
     */
    protected abstract List<Pair<Document, Float>> innerSimilaritySearchWithRelevanceScores(String query, int k);

    /**
     * Return docs most similar to embedding vector.
     *
     * @param embedding Embedding to look up documents similar to.
     * @param k         Number of Documents to return. Defaults to 4.
     * @param kwargs    kwargs to be passed to similarity search
     * @return List of Documents most similar to the query vector.
     */
    public abstract List<Document> similarSearchByVector(List<Float> embedding, int k, Map<String, Object> kwargs);

    public List<Document> maxMarginalRelevanceSearch(String query) {
        return maxMarginalRelevanceSearch(query, 4, 20, 0.5f);
    }

    /**
     * Return docs selected using the maximal marginal relevance.
     * Maximal marginal relevance optimizes for similarity to query AND diversity among selected documents.
     *
     * @param query      Text to look up documents similar to.
     * @param k          Number of Documents to return.
     * @param fetchK     Number of Documents to fetch to pass to MMR algorithm.
     * @param lambdaMult Number between 0 and 1 that determines the degree of diversity among the results with 0
     *                   corresponding to maximum diversity and 1 to minimum diversity.
     * @return List of Documents selected by maximal marginal relevance.
     */
    public abstract List<Document> maxMarginalRelevanceSearch(String query, int k, int fetchK, float lambdaMult);

    public List<Document> maxMarginalRelevanceSearchByVector(List<Float> embedding) {
        return maxMarginalRelevanceSearchByVector(embedding, 4, 20, 0.5f);
    }

    /**
     * Return docs selected using the maximal marginal relevance.
     * Maximal marginal relevance optimizes for similarity to query AND diversity among selected documents.
     *
     * @param embedding  Embedding to look up documents similar to.
     * @param k          Number of Documents to return.
     * @param fetchK     Number of Documents to fetch to pass to MMR algorithm.
     * @param lambdaMult Number between 0 and 1 that determines the degree of diversity among the results with 0 corresponding
     *                   to maximum diversity and 1 to minimum diversity.
     * @return List of Documents selected by maximal marginal relevance.
     */
    public abstract List<Document> maxMarginalRelevanceSearchByVector(List<Float> embedding, int k, int fetchK,
            float lambdaMult);

    /**
     * Return VectorStore initialized from documents and embeddings.
     */
    public int fromDocuments(List<Document> documents, Embeddings embedding) {
        List<String> texts = documents.stream().map(Document::getPageContent).collect(Collectors.toList());
        List<Map<String, Object>> metadatas = documents.stream().map(Document::getMetadata).collect(Collectors.toList());
        return fromTexts(texts, embedding, metadatas);
    }

    /**
     * Initializes and returns a VectorStore from the given texts, embeddings, and metadata.
     *
     * @param texts      the list of texts
     * @param embedding  the embeddings for the texts
     * @param metadatas  the list of metadata associated with the texts
     * @return the initialized VectorStore
     */
    public abstract int fromTexts(List<String> texts, Embeddings embedding, List<Map<String, Object>> metadatas);

    public VectorStoreRetriever asRetriever() {
        return asRetriever(SIMILARITY);
    }

    public VectorStoreRetriever asRetriever(SearchType searchType) {
        return new VectorStoreRetriever(this, searchType);
    }
}