/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hw.serpapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.hw.restapi.AggregateAPI;
import com.hw.restapi.RestApiRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
class AggregateAPITest {

    @Test
    void testZipCodeByPhone() {

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("phone", "13062587304");
        RestApiRequest search = new AggregateAPI(parameter, "48c723e9562a7be8646bae283c8bc90d");
        search.setParameter(parameter);

        JsonObject result = search.getResultByPath("/mobile/get");
        System.out.println(result);
    }

    @Test
    void testCityWeatherByCity() {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("city", "南京");
        RestApiRequest search = new AggregateAPI(parameter, "3b347817181dfd748144fdbe9f0c80f6");
        search.setParameter(parameter);
        JsonObject result = search.getResultByPath("/simpleWeather/query");
        System.out.println(result);
    }

    @Test
    void testCityLifeByCity() {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("city", "南京");
        RestApiRequest search = new AggregateAPI(parameter, "3b347817181dfd748144fdbe9f0c80f6");
        JsonObject result = search.getResultByPath("/simpleWeather/life");
        System.out.println(result);
    }
}
