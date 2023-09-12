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

import java.io.Serializable;
import java.util.List;

/**
 * @author HamaWhite
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteRequest implements Serializable {

    /**
     * Vectors to delete.
     */
    private List<String> ids;

    /**
     * This indicates that all vectors in the namespace should be deleted.
     * Not supported by projects on the gcp-starter environment.
     */
    private boolean deleteAll;

    /**
     * The namespace to delete vectors from, if applicable.
     */
    private String namespace;

    /**
     * If specified, the metadata filter here will be used to select the vectors to delete. This is mutually exclusive
     * with specifying IDs to delete in the ids param or using delete_all=True.
     */
    private Object filter;
}
