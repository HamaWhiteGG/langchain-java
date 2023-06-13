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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly SerpApi calls, can be run manually.")
class GoogleSearchTest {

    /**
     * set the environment variables, export SERPAPI_API_KEY=xxx
     */
    @Test
    void testGoogleSearch() {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("q", "Coffee");
        GoogleSearch search = new GoogleSearch(parameter);

        JsonObject data = search.getJson();
        JsonArray organicResults = data.get("organic_results").getAsJsonArray();
        assertNotNull(organicResults, "The organicResults should not be null");
    }
}