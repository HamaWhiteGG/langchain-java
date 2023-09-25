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

package com.hw.langchain.output.parsers.json;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.langchain.schema.OutputParserException;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HamaWhite
 */
public class Json {

    private Json() {

    }

    private static final Pattern PATTERN = Pattern.compile("```(json)?(.*?)```", Pattern.DOTALL);

    /**
     * Parse a JSON string from a Markdown string.
     *
     * @param jsonString The Markdown string.
     * @return The parsed JSON object as a Python dictionary.
     */
    public static JsonNode parseJsonMarkdown(String jsonString) {
        // Try to find JSON string within triple backticks
        Matcher matcher = PATTERN.matcher(jsonString);

        // If match found, use the content within the backticks,otherwise assume the entire string is a JSON string
        String jsonStr = matcher.find() ? matcher.group(2) : jsonString;

        // Strip whitespace and newlines from the start and end
        jsonStr = StrUtil.strip(jsonStr, " ");
        jsonStr = StrUtil.strip(jsonStr, "\n");
        try {
            // Parse the JSON string into a JsonNode
            return new ObjectMapper().readTree(jsonStr);
        } catch (JsonProcessingException e) {
            throw new OutputParserException("Got invalid JSON object. Error: " + e.getMessage());
        }
    }

    /**
     * Parse a JSON string from a Markdown string and check that it contains the expected keys.
     *
     * @param markdown The Markdown string.
     * @param expectedKeys The expected keys in the JSON string.
     * @return The parsed JSON object as a JsonNode.
     */
    public static Map<String, Object> parseAndCheckJsonMarkdown(String markdown, List<String> expectedKeys) {
        JsonNode jsonNode = parseJsonMarkdown(markdown);
        for (String key : expectedKeys) {
            if (!jsonNode.has(key)) {
                throw new OutputParserException(String.format(
                        "Got invalid return object. Expected key `%s` to be present, but got %s", key, jsonNode));
            }
        }
        return new ObjectMapper().convertValue(jsonNode, new TypeReference<Map<String, Object>>() {});
    }
}
