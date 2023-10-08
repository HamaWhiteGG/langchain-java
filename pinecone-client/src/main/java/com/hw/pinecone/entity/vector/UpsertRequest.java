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

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author HamaWhite
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpsertRequest implements Serializable {

    /**
     * An array containing the vectors to upsert. Recommended batch limit is 100 vectors.
     */
    @NotEmpty
    private List<Vector> vectors;

    /**
     * This is the namespace name where you upsert vectors.
     */
    private String namespace;

    public UpsertRequest(List<Vector> vectors) {
        this.vectors = vectors;
    }

    public UpsertRequest(List<Vector> vectors, String namespace) {
        this.vectors = vectors;
        this.namespace = namespace;
    }
}
