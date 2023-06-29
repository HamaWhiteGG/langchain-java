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

package com.hw.pinecone.entity.vector;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
@Data
public class ScoredVector {

    /**
     * This is the vector's unique id.
     */
    private String id;

    /**
     * This is a measure of similarity between this vector and the query vector.
     * The higher the score, the more they are similar.
     */
    private float score;

    /**
     * This is the vector data, if it is requested.
     */
    private List<Float> values;

    /**
     * This is the sparse data, if it is requested.
     */
    @JsonProperty("sparse_values")
    private SparseValues sparseValues;

    /**
     * This is the metadata, if it is requested.
     */
    public Map<String, Object> metadata;
}
