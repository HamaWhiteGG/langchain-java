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

package com.hw.langchain.utilities.serpapi;

import com.hw.langchain.utils.Utils;

import java.util.Map;

/**
 * Wrapper around SerpAPI.
 * <p>
 * To use, you should have the google-search-results python package installed,
 * and the environment variable SERPAPI_API_KEY set with your API key, or pass
 * serpapi_api_key as a named parameter to the constructor.
 *
 * @author HamaWhite
 */
public class SerpAPIWrapper {

    private Object searchEngine;

    private Map<String, Object> params;

    private String serpapiApiKey;

    public static SerpAPIWrapper of(Map<String, Object> kwargs) {
        return new SerpAPIWrapper(kwargs);
    }

    public SerpAPIWrapper(Map<String, Object> kwargs) {
        this.serpapiApiKey = Utils.getFromDictOrEnv(kwargs, "serpapi_api_key", "SERPAPI_API_KEY");
        // Initialize with default values
        this.params = Map.of("engine", "google",
                "google_domain", "google.com",
                "gl", "us",
                "hl", "en");
    }

    /**
     * Run query through SerpAPI and parse result.
     */
    public String run(String query) {
        return "";
    }
}
