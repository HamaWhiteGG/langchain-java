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

package com.hw.langchain.utilities;

import com.google.gson.JsonObject;
import com.hw.langchain.utils.Utils;
import com.hw.serpapi.GoogleSearch;
import com.hw.serpapi.SerpApiSearch;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper around SerpAPI.
 * <p>
 * Use the environment variable SERPAPI_API_KEY set with your API key, or pass
 * serpapi_api_key as a named parameter to the constructor.
 * <p>
 * <a href="https://serpapi.com/integrations/java">SerpApi in Java </a>
 *
 * @author HamaWhite
 */
public class SerpAPIWrapper {

    private static final String ANSWER_BOX = "answer_box";
    private static final String SPORTS_RESULTS = "sports_results";
    private static final String SHOPPING_RESULTS = "shopping_results";
    private static final String KNOWLEDGE_GRAPH = "knowledge_graph";
    private static final String ORGANIC_RESULTS = "organic_results";

    private SerpApiSearch searchEngine;

    private Map<String, String> params;

    private String serpapiApiKey;

    public static SerpAPIWrapper of(Map<String, Object> kwargs) {
        return new SerpAPIWrapper(kwargs);
    }

    public SerpAPIWrapper(Map<String, Object> kwargs) {
        this.serpapiApiKey = Utils.getFromDictOrEnv(kwargs, "serpapi_api_key", "SERPAPI_API_KEY");
        // Initialize with default values
        this.params = new HashMap<>();
        params.put("engine", "google");
        params.put("google_domain", "google.com");
        params.put("gl", "us");
        params.put("hl", "en");

        this.searchEngine = new GoogleSearch(serpapiApiKey);
    }

    /**
     * Run query through SerpAPI and parse result.
     */
    public String run(String query) {
        params.put("q", query);
        searchEngine.setParameter(params);
        JsonObject result = searchEngine.getJson();
        return processResponse(result);
    }

    public static String processResponse(JsonObject res) {
        if (res.has("error")) {
            throw new IllegalArgumentException("Got error from SerpAPI: " + res.get("error").getAsString());
        }
        if (res.has(ANSWER_BOX)
                && res.getAsJsonObject(ANSWER_BOX).has("answer")) {
            return res.getAsJsonObject(ANSWER_BOX).get("answer").getAsString();
        } else if (res.has(ANSWER_BOX)
                && res.getAsJsonObject(ANSWER_BOX).has("snippet")) {
            return res.getAsJsonObject(ANSWER_BOX).get("snippet").getAsString();
        } else if (res.has(ANSWER_BOX)
                && res.getAsJsonObject(ANSWER_BOX).has("snippet_highlighted_words")) {
            return res.getAsJsonObject(ANSWER_BOX)
                    .getAsJsonArray("snippet_highlighted_words")
                    .get(0)
                    .getAsString();
        } else if (res.has(SPORTS_RESULTS)
                && res.getAsJsonObject(SPORTS_RESULTS).has("game_spotlight")) {
            return res.getAsJsonObject(SPORTS_RESULTS).get("game_spotlight").getAsString();
        } else if (res.has(SHOPPING_RESULTS)
                && res.getAsJsonArray(SHOPPING_RESULTS).size() > 0
                && res.getAsJsonArray(SHOPPING_RESULTS).get(0).getAsJsonObject().has("title")) {
            return res.getAsJsonArray(SHOPPING_RESULTS).asList().subList(0, 3).toString();
        } else if (res.has(KNOWLEDGE_GRAPH)
                && res.getAsJsonObject(KNOWLEDGE_GRAPH).has("description")) {
            return res.getAsJsonObject(KNOWLEDGE_GRAPH).get("description").getAsString();
        } else if (res.has(ORGANIC_RESULTS)
                && res.getAsJsonArray(ORGANIC_RESULTS).size() > 0
                && res.getAsJsonArray(ORGANIC_RESULTS).get(0).getAsJsonObject().has("snippet")) {
            return res.getAsJsonArray(ORGANIC_RESULTS)
                    .get(0)
                    .getAsJsonObject()
                    .get("snippet")
                    .getAsString();
        } else if (res.has(ORGANIC_RESULTS)
                && res.getAsJsonArray(ORGANIC_RESULTS).size() > 0
                && res.getAsJsonArray(ORGANIC_RESULTS).get(0).getAsJsonObject().has("link")) {
            return res.getAsJsonArray(ORGANIC_RESULTS)
                    .get(0)
                    .getAsJsonObject()
                    .get("link")
                    .getAsString();
        } else {
            return "No good search result found";
        }
    }
}
