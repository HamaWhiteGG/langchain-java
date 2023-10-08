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

import com.google.common.collect.Maps;
import com.hw.langchain.embeddings.base.Embeddings;
import com.hw.langchain.schema.Document;
import com.hw.langchain.vectorstores.base.VectorStore;
import com.hw.pinecone.IndexClient;
import com.hw.pinecone.PineconeClient;
import com.hw.pinecone.entity.vector.*;
import com.hw.pinecone.entity.vector.Vector;

import lombok.var;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hw.langchain.vectorstores.utils.Nd4jUtils.createFromList;
import static com.hw.langchain.vectorstores.utils.Utils.maximalMarginalRelevance;

/**
 * @author HamaWhite
 */
@Builder
public class Pinecone extends VectorStore {

    private static final Logger LOG = LoggerFactory.getLogger(Pinecone.class);

    private PineconeClient client;

    @Getter
    private IndexClient index;

    private String indexName;

    private Function<String, List<Float>> embeddingFunction;

    @Builder.Default
    private String textKey = "text";

    @Builder.Default
    private Integer batchSize = 32;

    @Builder.Default
    private String namespace = "";

    /**
     * Validate parameters and init client
     */
    public Pinecone init() {
        List<String> indexes = client.listIndexes();

        if (indexes.contains(indexName)) {
            this.index = client.indexClient(indexName);
        } else if (indexes.isEmpty()) {
            throw new IllegalStateException(
                    "No active indexes found in your Pinecone project. Please check your API key and environment.");
        } else {
            String similarIndexes = String.join(", ", indexes);
            throw new IllegalArgumentException(String.format(
                    "Index '%s' not found in your Pinecone project. Did you mean one of the following indexes: %s",
                    indexName, similarIndexes));
        }
        return this;
    }

    @Override
    public List<String> addTexts(List<String> texts, List<Map<String, Object>> metadatas) {
        return null;
    }

    @Override
    public void delete(List<String> ids) {
        DeleteRequest deleteRequest = DeleteRequest.builder()
                .ids(ids)
                .namespace(namespace)
                .build();
        index.delete(deleteRequest);
    }

    /**
     * Return pinecone documents most similar to query, along with scores.
     *
     * @param query  Text to look up documents similar to.
     * @param k      Number of Documents to return. Defaults to 4.
     * @param filter Dictionary of argument(s) to filter on metadata
     * @return List of Documents most similar to the query and score for each
     */
    private List<Pair<Document, Float>> similaritySearchWithScore(String query, int k, Map<String, Object> filter) {
        List<Float> queryObj = embeddingFunction.apply(query);
        QueryRequest queryRequest = QueryRequest.builder()
                .vector(queryObj)
                .topK(k)
                .filter(filter)
                .namespace(namespace)
                .includeMetadata(true)
                .build();
        QueryResponse results = index.query(queryRequest);

        List<Pair<Document, Float>> docs = new ArrayList<>();
        for (var res : results.getMatches()) {
            var metadata = res.getMetadata();
            if (metadata.containsKey(textKey)) {
                var text = metadata.remove(textKey).toString();
                Document document = new Document(text, metadata);
                docs.add(Pair.of(document, res.getScore()));
            } else {
                LOG.warn("Found document with no `{}` key. Skipping.", textKey);
            }
        }
        return docs;
    }

    /**
     * Return pinecone documents most similar to query.
     *
     * @param query  Text to look up documents similar to.
     * @param k      Number of Documents to return. Defaults to 4.
     * @param filter Dictionary of argument(s) to filter on metadata
     * @return List of Documents most similar to the query and score for each
     */
    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        List<Pair<Document, Float>> docsAndScores = similaritySearchWithScore(query, k, filter);
        return docsAndScores.stream().map(Pair::getLeft).collect(Collectors.toList());
    }

    @Override
    protected List<Pair<Document, Float>> innerSimilaritySearchWithRelevanceScores(String query, int k) {
        return null;
    }

    @Override
    public List<Document> similarSearchByVector(List<Float> embedding, int k, Map<String, Object> kwargs) {
        return null;
    }

    @Override
    public List<Document> maxMarginalRelevanceSearch(String query, int k, int fetchK, float lambdaMult) {
        List<Float> embedding = embeddingFunction.apply(query);
        return maxMarginalRelevanceSearchByVector(embedding, k, fetchK, lambdaMult);
    }

    @Override
    public List<Document> maxMarginalRelevanceSearchByVector(List<Float> embedding, int k, int fetchK,
            float lambdaMult) {
        QueryRequest queryRequest = QueryRequest.builder()
                .vector(embedding)
                .topK(fetchK)
                .namespace(namespace)
                .includeValues(true)
                .includeMetadata(true)
                .build();
        QueryResponse results = index.query(queryRequest);

        List<Integer> mmrSelected = maximalMarginalRelevance(
                createFromList(embedding),
                results.getMatches().stream().map(ScoredVector::getValues).collect(Collectors.toList()),
                k,
                lambdaMult);

        checkNotNull(mmrSelected, "mmrSelected must not be null");
        List<Map<String, Object>> selected = mmrSelected.stream()
                .map(i -> results.getMatches().get(i).getMetadata())
                .collect(Collectors.toList());

        return selected.stream()
                .map(metadata -> new Document(metadata.remove(textKey).toString(), metadata))
                .collect(Collectors.toList());
    }

    @Override
    public int fromTexts(List<String> texts, Embeddings embedding, List<Map<String, Object>> metadatas) {
        int total = 0;
        for (int i = 0; i < texts.size(); i += batchSize) {
            // set end position of batch
            int iEnd = Math.min(i + batchSize, texts.size());
            // get batch of texts and ids
            List<String> linesBatch = texts.subList(i, iEnd);
            // create ids if not provided
            List<String> idsBatch = createIdsBatch(iEnd - i);
            // create embeddings
            var embeds = embedding.embedDocuments(linesBatch);
            // prepare metadata and upsert batch
            var metadata = createMetadata(linesBatch, metadatas, i, iEnd);
            List<Vector> vectors = createVectors(idsBatch, embeds, metadata);
            // upsert to Pinecone
            var response = index.upsert(new UpsertRequest(vectors, namespace));
            total += response.getUpsertedCount();
        }
        return total;
    }

    private List<String> createIdsBatch(int batchSize) {
        return Stream.generate(UUID::randomUUID)
                .limit(batchSize)
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> createMetadata(List<String> linesBatch, List<Map<String, Object>> metadatas,
            int start, int end) {
        List<Map<String, Object>> metadata = new ArrayList<>();
        if (metadatas != null) {
            metadata.addAll(metadatas.subList(start, end));
        } else {
            for (int i = 0; i < linesBatch.size(); i++) {
                metadata.add(Maps.newHashMap());
            }
        }
        for (int j = 0; j < linesBatch.size(); j++) {
            metadata.get(j).put(textKey, linesBatch.get(j));
        }
        return metadata;
    }

    private List<Vector> createVectors(List<String> idsBatch, List<List<Float>> embeds,
            List<Map<String, Object>> metadata) {
        return IntStream.range(0, idsBatch.size())
                .mapToObj(k -> new Vector(idsBatch.get(k), embeds.get(k), metadata.get(k)))
                .collect(Collectors.toList());
    }
}
