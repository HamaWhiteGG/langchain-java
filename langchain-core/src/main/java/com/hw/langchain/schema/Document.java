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

package com.hw.langchain.schema;

import com.google.common.collect.Maps;

import lombok.Data;

import java.util.Map;

/**
 * Interface for interacting with a document.
 *
 * @author HamaWhite
 */
@Data
public class Document {

    private String pageContent;

    private Map<String, Object> metadata;

    public Document(String pageContent, Map<String, Object> metadata) {
        this.pageContent = pageContent;
        this.metadata = metadata;
    }

    public Document(String pageContent) {
        this(pageContent, Maps.newHashMap());
    }
}
