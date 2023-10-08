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

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.schema.BaseOutputParser;
import lombok.var;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hw.langchain.output.parsers.FormatInstructions.STRUCTURED_FORMAT_INSTRUCTIONS;
import static com.hw.langchain.output.parsers.json.Json.parseAndCheckJsonMarkdown;
import static com.hw.langchain.prompts.utils.FormatUtils.formatTemplate;

/**
 * @author HamaWhite
 */
public class StructuredOutputParser extends BaseOutputParser<Map<String, Object>> {

    private static final String LINE_TEMPLATE = "\t\"{name}\": {type}  // {description}";

    private final transient List<ResponseSchema> responseSchemas;

    public StructuredOutputParser(List<ResponseSchema> responseSchemas) {
        this.responseSchemas = responseSchemas;
    }

    public static StructuredOutputParser fromResponseSchemas(List<ResponseSchema> responseSchemas) {
        return new StructuredOutputParser(responseSchemas);
    }

    private String getSubString(ResponseSchema schema) {
        Map<String, Object> kwargs = MapBuilder.create(new HashMap<String, Object>())
                .put("name", schema.getName())
                .put("description", schema.getDescription())
                .put("type", schema.getType()).map();
        return formatTemplate(LINE_TEMPLATE, kwargs);
    }

    @Override
    public Map<String, Object> parse(String text) {
        var expectedKeys = responseSchemas.stream()
                .map(ResponseSchema::getName)
                .collect(Collectors.toList());
        return parseAndCheckJsonMarkdown(text, expectedKeys);
    }

    @Override
    public String getFormatInstructions() {
        var schemaStr = responseSchemas.stream().map(this::getSubString).collect(Collectors.joining("\n"));
        return formatTemplate(STRUCTURED_FORMAT_INSTRUCTIONS, MapUtil.of("format", schemaStr));
    }
}
