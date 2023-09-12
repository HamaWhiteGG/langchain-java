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

package com.hw.pinecone.service;

import com.hw.pinecone.entity.vector.*;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.*;

import java.util.List;

/**
 * Vector Operations
 *
 * @author HamaWhite
 */
public interface VectorService {

    /**
     * The DescribeIndexStats operation returns statistics about the index's contents,
     * including the vector count per namespace and the number of dimensions.
     *
     * @param request the request object for describing index statistics
     * @return a Single emitting the response with index statistics
     */
    @POST("/describe_index_stats")
    Single<DescribeIndexStatsResponse> describeIndexStats(@Body DescribeIndexStatsRequest request);

    /**
     * The Query operation searches a namespace, using a query vector.
     * It retrieves the ids of the most similar items in a namespace, along with their similarity scores.
     *
     * @param request the QueryRequest containing the query vector and other parameters
     * @return a Single emitting a QueryResponse with the results of the query operation
     */
    @POST("/query")
    Single<QueryResponse> query(@Body QueryRequest request);

    /**
     * The Delete operation deletes vectors, by id, from a single namespace.
     * You can delete items by their id, from a single namespace.
     *
     * @param request the DeleteRequest containing the ids to delete
     */
    @POST("/vectors/delete")
    Completable delete(@Body DeleteRequest request);

    /**
     * The Fetch operation looks up and returns vectors, by ID, from a single namespace.
     * The returned vectors include the vector data and/or metadata.
     *
     * @param ids the vector ids to fetch.
     * @param namespace the namespace for the vectors.
     * @return a Single emitting the FetchResponse containing the fetched vectors
     */
    @GET("/vectors/fetch")
    Single<FetchResponse> fetch(@Query("ids") List<String> ids, @Query("namespace") String namespace);

    /**
     * The Upsert operation writes vectors into a namespace.
     * If a new value is upsert for an existing vector id, it will overwrite the previous value.
     *
     * @param request the UpsertRequest containing the vectors to be upsert
     * @return a Single emitting an UpsertResponse indicating the result of the upsert operation
     */
    @POST("/vectors/upsert")
    Single<UpsertResponse> upsert(@Body UpsertRequest request);
}
