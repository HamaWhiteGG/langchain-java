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

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author HamaWhite
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryRequest implements Serializable {

    /**
     * The query vector. This should be the same length as the dimension of the index being queried.
     * Each query() request can contain only one of the parameters id or vector.
     */
    private List<Float> vector;

    /**
     * The unique ID of the vector to be used as a query vector.
     * Each query() request can contain only one of the parameters queries, vector, or id.
     */
    private String id;

    /**
     * The number of results to return for each query.
     */
    @NotNull
    @Builder.Default
    private Integer topK = 10;

    /**
     * The filter to apply. You can use vector metadata to limit your search.
     */
    private Object filter;

    /**
     * The namespace to query.
     */
    private String namespace;

    /**
     * Indicates whether vector values are included in the response.
     */
    private boolean includeValues;

    /**
     * Indicates whether metadata is included in the response as well as the ids.
     */
    private boolean includeMetadata;
}