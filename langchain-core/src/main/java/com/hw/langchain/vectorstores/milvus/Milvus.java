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
import io.milvus.grpc.DataType;
import io.milvus.param.ConnectParam;
import io.milvus.param.Constant;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import lombok.Builder;

import java.util.List;
import java.util.Map;

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

    private MilvusClient milvusClient;

    public Milvus init() {
        milvusClient = new MilvusServiceClient(connectParam);

        // If need to drop old, drop it
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
                .withName("text")
                .withDataType(DataType.VarChar)
                .withTypeParams(Map.of(Constant.VARCHAR_MAX_LENGTH, "65535"))
                .build());
        // create the primary key field
        builder.addFieldType(FieldType.newBuilder()
                .withName("pk")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build());
        // create the vector field, supports binary or float vectors
        builder.addFieldType(FieldType.newBuilder()
                .withName("vector")
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

    @Override
    public List<String> addTexts(List<String> texts, List<Map<String, Object>> metadatas) {
        List<List<Float>> embeddings = embeddingFunction.embedDocuments(texts);
        if (embeddings.isEmpty()) {
            LOG.warn("Nothing to insert, skipping.");
            return List.of();
        }
        if (!hasCollection()) {
            createCollection(embeddings, metadatas);
        }

        return List.of();
    }

    @Override
    public boolean delete(List<String> ids) {
        return false;
    }

    @Override
    public List<Document> similaritySearch(String query, int k, Map<String, Object> filter) {
        return null;
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
