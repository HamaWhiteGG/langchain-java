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

package com.hw.openai.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hw.openai.utils.JsonUtils.convertFromJsonStr;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author HamaWhite
 */
class JsonUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtilsTest.class);

    @Test
    void TestConvertFromJsonStr() {
        String arguments = """
                {
                  "location": "Boston, MA"
                }""";

        Weather weather = convertFromJsonStr(arguments, Weather.class);
        LOG.info("result: {}", weather);
        assertThat(weather).isNotNull();
    }

    record Weather(
            @JsonProperty(
                    required = true) @JsonPropertyDescription("The city and state, e.g. San Francisco, CA") String location,

            @JsonPropertyDescription("The temperature unit") WeatherUnit unit) {
    }

    enum WeatherUnit {
        CELSIUS,
        FAHRENHEIT
    }
}
