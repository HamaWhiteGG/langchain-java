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

package com.hw.pinecone;

import com.hw.pinecone.entity.vector.*;
import com.hw.pinecone.service.VectorService;

/**
 * @author HamaWhite
 */
public class IndexClient {

    private final VectorService vectorService;

    public IndexClient(VectorService vectorService) {
        this.vectorService = vectorService;
    }

    /**
     * The DescribeIndexStats operation returns statistics about the index's contents,
     * including the vector count per namespace and the number of dimensions.
     *
     * @param request the request object for describing index statistics
     * @return a DescribeIndexStatsResponse with index statistics
     */
    public DescribeIndexStatsResponse describeIndexStats(DescribeIndexStatsRequest request) {
        return vectorService.describeIndexStats(request).blockingGet();
    }

    /**
     * The Query operation searches a namespace, using a query vector.
     * It retrieves the ids of the most similar items in a namespace, along with their similarity scores.
     *
     * @param request the QueryRequest containing the query vector and other parameters
     * @return a QueryResponse with the results of the query operation
     */
    public QueryResponse query(QueryRequest request) {
        return vectorService.query(request).blockingGet();
    }

    /**
     * The Delete operation deletes vectors, by id, from a single namespace.
     * You can delete items by their id, from a single namespace.
     *
     * @param request the DeleteRequest containing the ids to delete
     */
    public void delete(DeleteRequest request) {
        vectorService.delete(request).blockingAwait();
    }

    /**
     * The Fetch operation looks up and returns vectors, by ID, from a single namespace.
     * The returned vectors include the vector data and/or metadata.
     *
     * @param request the FetchRequest object containing the parameters for the fetch operation
     * @return a FetchResponse containing the fetched vectors
     */
    public FetchResponse fetch(FetchRequest request) {
        return vectorService.fetch(request.getIds(), request.getNamespace()).blockingGet();
    }

    /**
     * The Upsert operation writes vectors into a namespace.
     * If a new value is upsert for an existing vector id, it will overwrite the previous value.
     *
     * @param request the UpsertRequest containing the vectors to be upsert
     * @return  an UpsertResponse indicating the result of the upsert operation
     */
    public UpsertResponse upsert(UpsertRequest request) {
        return vectorService.upsert(request).blockingGet();
    }
}
