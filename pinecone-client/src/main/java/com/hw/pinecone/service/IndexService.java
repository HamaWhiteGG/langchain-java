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

import com.hw.pinecone.entity.index.CreateIndexCmd;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
    @GET("databases")
    Single<List<String>> listIndexes();

    /**
     * This operation creates a Pinecone index.
     *
     * @param command create index command
     */
    @POST("databases")
    Single<Void> createIndex(@Body CreateIndexCmd command);
}
