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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Vector implements Serializable {

    /**
     * This is the vector's unique id.
     */
    private String id;

    /**
     * This is the vector data included in the request.
     */
    private List<Float> values;

    /**
     * Vector sparse data. Represented as a list of indices and a list of corresponded values,
     * which must be the same length.
     */
    @JsonProperty("sparse_values")
    private SparseValues sparseValues;

    /**
     * This is the metadata included in the request.
     */
    public Map<String, Object> metadata;

    public Vector(String id, List<Float> values) {
        this.id = id;
        this.values = values;
    }

    public Vector(String id, List<Float> values, Map<String, Object> metadata) {
        this.id = id;
        this.values = values;
        this.metadata = metadata;
    }
}
