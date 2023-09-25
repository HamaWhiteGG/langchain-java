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

package com.hw.langchain.prompts.utils;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hw.langchain.prompts.utils.FormatUtils.findVariables;
import static com.hw.langchain.prompts.utils.FormatUtils.formatTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author HamaWhite
 */
class FormatUtilsTest {

    @Test
    void testFormatTemplate() {
        String template = "{{\n{format}\n}}";
        Map<String, Object> kwargs = MapUtil.of("format", "value");

        String expected = "{\nvalue\n}";
        String actual = formatTemplate(template, kwargs);

        assertEquals(expected, actual);
    }

    @Test
    void testFormatTemplateWithValidTemplate() {
        String template = "Hello, {name}! Today is {day}.";
        Map<String, Object> kwargs = MapBuilder.create(new HashMap<String, Object>())
                .put("name", "John")
                .put("day", "Monday").map();

        String expected = "Hello, John! Today is Monday.";
        String actual = formatTemplate(template, kwargs);

        assertEquals(expected, actual);
    }

    @Test
    void testFormatTemplateWithInvalidTemplate() {
        String template = "Hello, {name}! Today is {day}.";
        Map<String, Object> kwargs = MapUtil.of("name", "John");

        String expected = "Hello, John! Today is {day}.";
        String actual = formatTemplate(template, kwargs);

        assertEquals(expected, actual);
    }

    @Test
    void testFormatTemplateWithDoubleCurlyBraces() {
        String template = "Hello, {{{name}}}!";
        Map<String, Object> kwargs = MapUtil.of("name", "John");

        // but python is 'Hello, {John}!'
        String expected = "Hello, {John}!";
        String actual = formatTemplate(template, kwargs);
        assertEquals(expected, actual);
    }

    @Test
    void testFormatTemplateWithQuadrupleCurlyBraces() {
        String template = "Hello, {{{{name}}}}!";
        Map<String, Object> kwargs = MapUtil.of("name", "John");

        // python is 'Hello, {{name}}!'
        String expected = "Hello, {John}!";
        String actual = formatTemplate(template, kwargs);
        assertEquals(expected, actual);
    }

    @Test
    void testFindVariables() {
        String input = "" +
                "{{\n" +
                "                  \"action\": $TOOL_NAME,\n" +
                "                  \"action_input\": $INPUT\n" +
                "                }}" +
                "";

        List<String> actualVariables = findVariables(input);
        assertEquals(0, actualVariables.size());
    }

    @Test
    void testFindVariablesWithEmptyString() {
        String input = "";
        List<String> variables = findVariables(input);

        assertEquals(0, variables.size());
    }

    @Test
    void testFindVariablesWithNoVariables() {
        String input = "Hello, world!";
        List<String> variables = findVariables(input);

        assertEquals(0, variables.size());
    }

    @Test
    void testFindVariablesWithSingleVariable() {
        String input = "Hello, {name}!";
        List<String> variables = findVariables(input);

        assertEquals(1, variables.size());
        assertEquals("name", variables.get(0));
    }

    @Test
    void testFindVariablesWithMultipleVariables() {
        String input = "Hello, {name}! Today is {day}.";
        List<String> variables = findVariables(input);

        assertEquals(2, variables.size());
        assertEquals("name", variables.get(0));
        assertEquals("day", variables.get(1));
    }
}