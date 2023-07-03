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

package com.hw.langchain.output.parsers.structured;

import com.fasterxml.jackson.databind.JsonNode;
import com.hw.langchain.schema.BaseOutputParser;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.output.parsers.FormatInstructions.STRUCTURED_FORMAT_INSTRUCTIONS;
import static com.hw.langchain.output.parsers.json.Json.parseAndCheckJsonMarkdown;
import static com.hw.langchain.prompts.utils.FormatUtils.formatTemplate;

/**
 * @author HamaWhite
 */
public class StructuredOutputParser extends BaseOutputParser<JsonNode> {

    private static final String LINE_TEMPLATE = "\t\"{name}\": {type}  // {description}";

    private final List<ResponseSchema> responseSchemas;

    public StructuredOutputParser(List<ResponseSchema> responseSchemas) {
        this.responseSchemas = responseSchemas;
    }

    public static StructuredOutputParser fromResponseSchemas(List<ResponseSchema> responseSchemas) {
        return new StructuredOutputParser(responseSchemas);
    }

    private String getSubString(ResponseSchema schema) {
        Map<String, Object> kwargs = Map.of("name", schema.getName(),
                "description", schema.getDescription(),
                "type", schema.getType());
        return formatTemplate(LINE_TEMPLATE, kwargs);
    }

    @Override
    public JsonNode parse(String text) {
        var expectedKeys = responseSchemas.stream()
                .map(ResponseSchema::getName)
                .toList();
        return parseAndCheckJsonMarkdown(text, expectedKeys);
    }

    @Override
    public String getFormatInstructions() {
        var schemaStr = String.join("\n", responseSchemas.stream().map(this::getSubString).toList());
        return formatTemplate(STRUCTURED_FORMAT_INSTRUCTIONS, Map.of("format", schemaStr));
    }
}
