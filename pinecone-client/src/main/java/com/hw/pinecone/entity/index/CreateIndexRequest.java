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

package com.hw.pinecone.entity.index;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;

import static com.hw.pinecone.entity.index.Metric.COSINE;

/**
 * @author HamaWhite
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateIndexRequest implements Serializable {

    /**
     * The name of the index to be created.
     */
    @NotBlank
    @Size(max = 45)
    private String name;

    /**
     * The dimensions of the vectors to be inserted in the index
     */
    @NotNull
    private Integer dimension;

    /**
     * The distance metric to be used for similarity search.
     */
    @NotNull
    @Builder.Default
    private Metric metric = COSINE;

    /**
     * The number of pods for the index to use,including replicas.
     */
    private Integer pods;

    /**
     * The number of replicas. Replicas duplicate your index. They provide higher availability and throughput.
     */
    private Integer replicas;

    /**
     * The type of pod to use. One of s1, p1, or p2 appended with . and one of x1, x2, x4, or x8.
     */
    @JsonProperty("pod_type")
    private String podType;

    /**
     * Configuration for the behavior of Pinecone's internal metadata index. By default, all metadata is indexed;
     * when metadata_config is present, only specified metadata fields are indexed.
     * To specify metadata fields to index, provide a JSON object of the following form:
     * <p>
     * {"indexed": ["example_metadata_field"]}
     */
    @JsonProperty("metadata_config")
    private Map<String, Object> metadataConfig;

    /**
     * The name of the collection to create an index from.
     */
    @JsonProperty("source_collection")
    private String sourceCollection;
}
