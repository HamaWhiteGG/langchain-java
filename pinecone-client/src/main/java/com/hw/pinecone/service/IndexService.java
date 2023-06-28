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

import com.hw.pinecone.entity.index.CreateIndexRequest;
import com.hw.pinecone.entity.index.IndexDescription;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.*;

import java.util.List;

/**
 * Index Operations
 *
 * @author HamaWhite
 */
public interface IndexService {

    /**
     * This operation returns a list of your Pinecone indexes.
     *
     * @return A Single that emits a list of strings representing the Pinecone indexes.
     */
    @GET("/databases")
    Single<List<String>> listIndexes();

    /**
     * This operation creates a Pinecone index.
     *
     * @param request create index request
     * @return  a Single wrapping the response body
     */
    @POST("/databases")
    Single<ResponseBody> createIndex(@Body CreateIndexRequest request);

    /**
     * Get a description of an index.
     *
     * @param name the name of the index
     * @return A Single that emits a description of the index
     */
    @GET("/databases/{indexName}")
    Single<IndexDescription> describeIndex(@Path("indexName") String name);

    /**
     * This operation deletes an existing index.
     *
     * @param name the name of the index
     * @return a Single wrapping the response body
     */
    @DELETE("/databases/{indexName}")
    Single<ResponseBody> deleteIndex(@Path("indexName") String name);

}
