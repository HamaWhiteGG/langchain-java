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
import com.hw.openai.entity.chat.ChatFunction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author HamaWhite
 */
class ChatParameterUtilsTest {

    @Test
    void testGenerateChatParameter() {
        ChatFunction.ChatParameter chatParameter = ChatParameterUtils.generate(Weather.class);
        String actual = JsonUtils.toJsonStringWithIndent(chatParameter);

        String expected = """
                {
                    "type" : "object",
                    "properties" : {
                        "location" : {
                            "type" : "string",
                            "description" : "The city and state, e.g. San Francisco, CA"
                        },
                        "unit" : {
                            "type" : "string",
                            "description" : "The temperature unit",
                            "enum" : [
                                "celsius",
                                "fahrenheit"
                            ]
                        }
                    },
                    "required" : [
                        "location"
                    ]
                }""";

        assertEquals(expected, actual);
    }

    public record Weather(
            @JsonProperty(
                    required = true) @JsonPropertyDescription("The city and state, e.g. San Francisco, CA") String location,

            @JsonPropertyDescription("The temperature unit") WeatherUnit unit) {
    }

    public enum WeatherUnit {
        CELSIUS,
        FAHRENHEIT
    }

}