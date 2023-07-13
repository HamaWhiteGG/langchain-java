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

import lombok.Data;

import java.util.Map;

/**
 * @author HamaWhite
 */
@Data
public class DescribeIndexStatsResponse {

    /**
     * A mapping for each namespace in the index from the namespace name to a summary of its contents.
     * If a metadata filter expression is present, the summary will reflect only vectors matching that expression.
     */
    private Map<String, Map<String, Object>> namespaces;

    /**
     * The dimension of the indexed vectors.
     */
    private Integer dimension;

    /**
     * The fullness of the index, regardless of whether a metadata filter expression was passed.
     * The granularity of this metric is 10%.
     */
    private Float indexFullness;

    /**
     * The total number of vectors in the index, regardless of whether a metadata filter expression was passed.
     */
    private Integer totalVectorCount;
}
