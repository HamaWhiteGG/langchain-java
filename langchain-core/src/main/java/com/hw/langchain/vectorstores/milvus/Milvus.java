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

import com.hw.langchain.embeddings.base.Embeddings;
import com.hw.langchain.schema.Document;
import com.hw.langchain.vectorstores.base.VectorStore;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.*;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hw.langchain.chains.query.constructor.JsonUtils.writeValueAsString;

/**
 * Initialize wrapper around the milvus vector database.
 *
 * @author HamaWhite
 */
@Builder
public class Milvus extends VectorStore {

    private static final Logger LOG = LoggerFactory.getLogger(Milvus.class);

    /**
     * Function used to embed the text.
     */
    private Embeddings embeddingFunction;

    /**
     * Parameters for client connection.
     */
    private ConnectParam connectParam;

    /**
     * Which Milvus collection to use.
     */
    @Builder.Default
    private String collectionName = "LangChainCollection";

    /**
     * The consistency level to use for a collection.
     */
    @Builder.Default
    private ConsistencyLevelEnum consistencyLevel = ConsistencyLevelEnum.STRONG;

    private boolean dropOld;

    @Builder.Default
    private int batchSize = 1000;

    private MilvusClient milvusClient;

    /**
     * In order for a collection to be compatible, pk needs to be auto-id and int
     */
    @Builder.Default
    private String primaryField = "pk";

    /**
     * In order for compatibility, the text field will need to be called "text"
     */
    @Builder.Default
    private String textField = "text";

    /**
     * In order for compatibility, the vector field needs to be called "vector"
     */
    @Builder.Default
    private String vectorField = "vector";

    @Builder.Default
    private List<String> fields = new ArrayList<>();

    private Map<String, Map<String, Object>> defaultSearchParams;

    public Milvus init() {
        milvusClient = new MilvusServiceClient(connectParam);

        // default search params when one is not provided.
        defaultSearchParams = Map.of(
                "IVF_FLAT", Map.of("metric_type", "L2", "params", Map.of("nprobe", 10)),
                "IVF_SQ8", Map.of("metric_type", "L2", "params", Map.of("nprobe", 10)),
                "IVF_PQ", Map.of("metric_type", "L2", "params", Map.of("nprobe", 10)),
                "HNSW", Map.of("metric_type", "L2", "params", Map.of("ef", 10)),
                "RHNSW_FLAT", Map.of("metric_type", "L2", "params", Map.of("ef", 10)),
                "RHNSW_SQ", Map.of("metric_type", "L2", "params", Map.of("ef", 10)),
                "RHNSW_PQ", Map.of("metric_type", "L2", "params", Map.of("ef", 10)),
                "IVF_HNSW", Map.of("metric_type", "L2", "params", Map.of("nprobe", 10, "ef", 10)),
                "ANNOY", Map.of("metric_type", "L2", "params", Map.of("search_k", 10)),
                "AUTOINDEX", Map.of("metric_type", "L2", "params", Map.of()));

        // if need to drop old, drop it
        if (hasCollection() && dropOld) {
            milvusClient.dropCollection(
                    DropCollectionParam.newBuilder()
                            .withCollectionName(collectionName)
                            .build());
        }
        return this;
    }

    private boolean hasCollection() {
        HasCollectionParam requestParam = HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        return milvusClient.hasCollection(requestParam).getData();
    }

    private void innerInit(List<List<Float>> embeddings, List<Map<String, Object>> metadatas) {
        if (CollectionUtils.isNotEmpty(embeddings)) {
            createCollection(embeddings, metadatas);
        }
        extractFields();
        createIndex();
        load();
    }

    public void createCollection(List<List<Float>> embeddings, List<Map<String, Object>> metadatas) {
        CreateCollectionParam.Builder builder = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withEnableDynamicField(true);

        // determine embedding dim
        int dim = embeddings.get(0).size();
        // determine metadata schema
        if (CollectionUtils.isNotEmpty(metadatas)) {
            // create FieldSchema for each entry in metadata.
            metadatas.get(0).forEach((key, value) -> {
                // infer the corresponding datatype of the metadata
                DataType dataType = inferDataTypeByData(value);
                // dataType isn't compatible
                if (dataType == DataType.UNRECOGNIZED || dataType == DataType.None) {
                    LOG.error("Failure to create collection, unrecognized dataType for key: {}", key);
                    throw new IllegalArgumentException("Unrecognized datatype for " + key + ".");
                } else {
                    FieldType fieldType = FieldType.newBuilder()
                            .withName(key)
                            .withDataType(dataType)
                            .build();
                    builder.addFieldType(fieldType);
                }
            });
        }
        // create the text field
        builder.addFieldType(FieldType.newBuilder()
                .withName(textField)
                .withDataType(DataType.VarChar)
                .withTypeParams(Map.of(Constant.VARCHAR_MAX_LENGTH, "65535"))
                .build());
        // create the primary key field
        builder.addFieldType(FieldType.newBuilder()
                .withName(primaryField)
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build());
        // create the vector field, supports binary or float vectors
        builder.addFieldType(FieldType.newBuilder()
                .withName(vectorField)
                .withDataType(DataType.FloatVector)
                .withDimension(dim)
                .build());

        // create the collection
        milvusClient.createCollection(builder.build());
    }

    private DataType inferDataTypeByData(Object value) {
        // TODO: Find corresponding method in Java
        return DataType.valueOf(value.toString());
    }

    /**
     * Grab the existing fields from the Collection
     */
    private void extractFields() {
        R<DescribeCollectionResponse> response = milvusClient.describeCollection(
                // Return the name and schema of the collection.
                DescribeCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());

        CollectionSchema schema = response.getData().getSchema();
        for (FieldSchema x : schema.getFieldsList()) {
            fields.add(x.getName());
        }
        // since primary field is auto-id, no need to track it
        fields.remove(primaryField);
    }

    private Map<String, Object> getIndex() {
        return null;
    }

    /**
     * Create a index on the collection
     */
    private void createIndex() {
        Map<String, Object> extraParam = Map.of("M", 8, "efConstruction", 64);
        CreateIndexParam requestParam = CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName(vectorField)
                .withIndexType(IndexType.HNSW)
                .withMetricType(MetricType.L2)
                .withExtraParam(writeValueAsString(extraParam))
                .withSyncMode(false)
                .build();
        milvusClient.createIndex(requestParam);
        LOG.info("Successfully created an index on collection: {}", collectionName);
    }

    /**
     * Generate search params based on the current index type
     */
    private void createSearchParams() {

    }

    /**
     * Load the collection if available.
     */
    private void load() {
        LoadCollectionParam requestParam = LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        milvusClient.loadCollection(requestParam);
    }

    @Override
    public List<String> addTexts(List<String> texts, List<Map<String, Object>> metadatas) {
        List<List<Float>> embeddings = embeddingFunction.embedDocuments(texts);
        if (embeddings.isEmpty()) {
            LOG.warn("Nothing to insert, skipping.");
            return List.of();
        }

        // if the collection hasn't been initialized yet, perform all steps to take so
        innerInit(embeddings, metadatas);

        // dict to hold all insert columns
        Map<String, List<?>> insertDict = Map.of(textField, texts, vectorField, embeddings);
        int totalCount = embeddings.size();
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < totalCount; i += batchSize) {
            // grab end index
            int end = Math.min(i + batchSize, totalCount);
            // convert map to batch list for insertion
            List<InsertParam.Field> insertFields = new ArrayList<>();
            for (String field : fields) {
                insertFields.add(new InsertParam.Field(field, insertDict.get(field).subList(i, end)));
            }
            // insert into the collection.
            InsertParam insertParam = InsertParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFields(insertFields)
                    .build();
            var res = milvusClient.insert(insertParam);
            pks.addAll(res.getData().getIDs().getStrId().getDataList());
        }
        return pks;
    }

    @Override
    public boolean delete(List<String> ids) {
        return false;
    }

    private List<Pair<Document, Float>> similaritySearchWithScore(String query, int k, Map<String, Object> filter) {
        // embed the query text.
        List<Float> embedding = embeddingFunction.embedQuery(query);

        // determine result metadata fields.
        List<String> outputFields = new ArrayList<>(fields);
        outputFields.remove(vectorField);

        // perform the search.
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withConsistencyLevel(consistencyLevel)
                .withMetricType(MetricType.L2)
                .withOutFields(outputFields)
                .withTopK(k)
                .withVectors(List.of(embedding))
                .withVectorFieldName(vectorField)
                // .withParams(SEARCH_PARAM)
                .build();
        R<SearchResults> respSearch = milvusClient.search(searchParam);
        return null;
    }

    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        List<Pair<Document, Float>> docsAndScores = similaritySearchWithScore(query, k, filter);
        return docsAndScores.stream().map(Pair::getLeft).toList();
    }

    @Override
    protected List<Pair<Document, Float>> _similaritySearchWithRelevanceScores(String query, int k) {
        return null;
    }

    @Override
    public List<Document> similarSearchByVector(List<Float> embedding, int k, Map<String, Object> kwargs) {
        return null;
    }

    @Override
    public List<Document> maxMarginalRelevanceSearch(String query, int k, int fetchK, float lambdaMult) {
        return null;
    }

    @Override
    public List<Document> maxMarginalRelevanceSearchByVector(List<Float> embedding, int k, int fetchK,
            float lambdaMult) {
        return null;
    }

    @Override
    public int fromTexts(List<String> texts, Embeddings embedding, List<Map<String, Object>> metadatas) {
        return addTexts(texts, metadatas).size();
    }
}
