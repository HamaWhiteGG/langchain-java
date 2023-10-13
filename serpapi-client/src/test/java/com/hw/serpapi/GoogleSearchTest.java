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
 * Please set the environment variables, export SERPAPI_API_KEY=xxx.
 * <p>
 * Due to <a href="https://github.com/serpapi/google-search-results-java/issues/22">Cant get this artifact from jitpack</a>, therefore copy its code to this project.
 *
 * @author HamaWhite
 */
@Disabled("Test requires costly SerpApi calls, can be run manually.")
class GoogleSearchTest {

    @Test
    void testReturnOrganicResults() {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("q", "Coffee");
        SerpApiSearch search = new GoogleSearch(parameter);
        JsonObject result = search.getJson();
        JsonArray organicResults = result.getAsJsonArray("organic_results");
        assertNotNull(organicResults, "The organicResults should not be null");
    }

    @Test
    void testReturnSearchResultSnippet() {
        SerpApiSearch search = new GoogleSearch();

        Map<String, String> parameter = new HashMap<>();
        parameter.put("engine", "google");
        parameter.put("google_domain", "google.com");
        parameter.put("gl", "us");
        parameter.put("hl", "en");
        parameter.put("q", "High temperature in SF yesterday");

        search.setParameter(parameter);

        JsonObject result = search.getJson();
        String searchResult = result.getAsJsonArray("organic_results")
                .get(0)
                .getAsJsonObject()
                .get("snippet")
                .getAsString();
        assertNotNull(searchResult, "The searchResult should not be null");
    }
}