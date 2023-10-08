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
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import com.hw.langchain.embeddings.base.Embeddings;
import com.hw.langchain.schema.Document;
import com.hw.langchain.vectorstores.base.VectorStore;

import lombok.var;
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
import io.milvus.param.index.DescribeIndexParam;
import io.milvus.response.DescIndexResponseWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.Builder;

import java.util.*;
import java.util.stream.Collectors;

import static com.hw.langchain.chains.query.constructor.JsonUtils.writeValueAsString;

/**
 * Initialize wrapper around the milvus vector database.
 *
 * @author HamaWhite
 */
@Builder
public class Milvus extends VectorStore {

    private static final Logger LOG = LoggerFactory.getLogger(Milvus.class);

    private static final String METRIC_TYPE = "metric_type";

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

    @Builder.Default
    private boolean dropOld = true;

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

    private Map<String, Object> searchParams;

    public Milvus init() {
        milvusClient = new MilvusServiceClient(connectParam);
        // default search params when one is not provided.
        initDefaultSearchParams();

        // if you need to drop old, drop it
        if (hasCollection() && dropOld) {
            milvusClient.dropCollection(
                    DropCollectionParam.newBuilder()
                            .withCollectionName(collectionName)
                            .build());
        }
        return this;
    }

    private void initDefaultSearchParams() {
        // Initialize mutable maps
        Map<String, Object> innerParams1 = Maps.newHashMap();
        innerParams1.put("nprobe", 10);

        Map<String, Object> innerParams2 = Maps.newHashMap();
        innerParams2.put("ef", 10);

        Map<String, Object> innerParams3 = Maps.newHashMap();
        innerParams3.put("nprobe", 10);
        innerParams3.put("ef", 10);

        // Initialize the main map
        defaultSearchParams = Maps.newHashMap();
        defaultSearchParams.put("IVF_FLAT", createInnerMap("L2", innerParams1));
        defaultSearchParams.put("IVF_SQ8", createInnerMap("L2", innerParams1));
        defaultSearchParams.put("IVF_PQ", createInnerMap("L2", innerParams1));
        defaultSearchParams.put("HNSW", createInnerMap("L2", innerParams2));
        defaultSearchParams.put("RHNSW_FLAT", createInnerMap("L2", innerParams2));
        defaultSearchParams.put("RHNSW_SQ", createInnerMap("L2", innerParams2));
        defaultSearchParams.put("RHNSW_PQ", createInnerMap("L2", innerParams2));
        defaultSearchParams.put("IVF_HNSW", createInnerMap("L2", innerParams3));
        defaultSearchParams.put("ANNOY", createInnerMap("L2", createInnerParams("search_k", 10)));
        defaultSearchParams.put("AUTOINDEX", createInnerMap("L2", Maps.newHashMap()));
    }

    private Map<String, Object> createInnerMap(String metricType, Map<String, Object> params) {
        Map<String, Object> innerMap = Maps.newHashMap();
        innerMap.put(METRIC_TYPE, metricType);
        innerMap.put("params", params);
        return innerMap;
    }

    private Map<String, Object> createInnerParams(String key, Object value) {
        Map<String, Object> innerParams = Maps.newHashMap();
        innerParams.put(key, value);
        return innerParams;
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
        createSearchParams();
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
                            .withTypeParams(MapUtil.of(Constant.VARCHAR_MAX_LENGTH, "65535"))
                            .build();
                    builder.addFieldType(fieldType);
                }
            });
        }
        // create the text field
        builder.addFieldType(FieldType.newBuilder()
                .withName(textField)
                .withDataType(DataType.VarChar)
                .withTypeParams(MapUtil.of(Constant.VARCHAR_MAX_LENGTH, "65535"))
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
        LOG.debug("meta value: {}", value);
        return DataType.VarChar;
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

    private DescIndexResponseWrapper.IndexDesc getIndex() {
        DescribeIndexParam requestParam = DescribeIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<DescribeIndexResponse> response = milvusClient.describeIndex(requestParam);
        if (response.getData() != null) {
            DescIndexResponseWrapper wrapper = new DescIndexResponseWrapper(response.getData());
            for (DescIndexResponseWrapper.IndexDesc desc : wrapper.getIndexDescriptions()) {
                if (desc.getFieldName().equals(vectorField)) {
                    return desc;
                }
            }
        }
        return null;
    }

    /**
     * Create a index on the collection
     */
    private void createIndex() {
        if (getIndex() == null) {
            Map<String, Object> extraParam = MapBuilder.create(new HashMap<String, Object>())
                    .put("M", 8)
                    .put("efConstruction", 64).map();
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
    }

    /**
     * Generate search params based on the current index type
     */
    private void createSearchParams() {
        DescIndexResponseWrapper.IndexDesc index = getIndex();
        if (index != null) {
            String indexType = index.getParams().get("index_type");
            String metricType = index.getParams().get(METRIC_TYPE);
            searchParams = defaultSearchParams.get(indexType);
            searchParams.put(METRIC_TYPE, metricType);
        }
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
            return ListUtil.of();
        }

        // if the collection hasn't been initialized yet, perform all steps to take so
        innerInit(embeddings, metadatas);

        // dict to hold all insert columns
        Map<String, List<?>> insertDict = Maps.newHashMap();
        insertDict.put(textField, texts);
        insertDict.put(vectorField, embeddings);

        // collect the metadata into the insert dict.
        if (metadatas != null) {
            for (var meta : metadatas) {
                meta.forEach((key, value) -> {
                    if (fields.contains(key)) {
                        @SuppressWarnings("unchecked")
                        List<Object> dict = (List<Object>) insertDict.get(key);
                        if (dict == null) {
                            dict = new ArrayList<>();
                            insertDict.put(key, dict);
                        }
                        dict.add(value);
                    }
                });
            }
        }

        // total insert count
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
    public void delete(List<String> ids) {
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
                .withMetricType(MetricType.valueOf(searchParams.get(METRIC_TYPE).toString()))
                .withOutFields(outputFields)
                .withTopK(k)
                .withVectors(ListUtil.of(embedding))
                .withVectorFieldName(vectorField)
                .withParams(writeValueAsString(searchParams.get("params")))
                .build();
        R<SearchResults> respSearch = milvusClient.search(searchParam);
        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());

        // organize results.
        List<Pair<Document, Float>> ret = new ArrayList<>();
        for (var result : wrapperSearch.getRowRecords()) {
            Map<String, Object> meta = Maps.newHashMap();
            for (String x : outputFields) {
                meta.put(x, result.get(x));
            }
            Document doc = new Document((String) meta.remove(textField), meta);
            Pair<Document, Float> pair = Pair.of(doc, (Float) result.get("distance"));
            ret.add(pair);
        }
        return ret;
    }

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
