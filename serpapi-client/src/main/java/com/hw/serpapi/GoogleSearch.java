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

package com.hw.serpapi;

import java.util.Map;

/**
 * Google Search Results using SerpApi
 *
 * @author HamaWhite
 */
public class GoogleSearch extends SerpApiSearch {

    /**
     * Constructor
     *
     * @param parameter     search parameter
     * @param serpapiApiKey secret API key
     */
    public GoogleSearch(Map<String, String> parameter, String serpapiApiKey) {
        super(parameter, serpapiApiKey, "google");
    }

    /**
     * Constructor
     */
    public GoogleSearch() {
        super("google");
    }

    /**
     * Constructor
     *
     * @param serpapiApiKey secret API key
     */
    public GoogleSearch(String serpapiApiKey) {
        super(serpapiApiKey, "google");
    }

    /**
     * Constructor
     *
     * @param parameter search parameter
     */
    public GoogleSearch(Map<String, String> parameter) {
        super(parameter, "google");
    }
}
