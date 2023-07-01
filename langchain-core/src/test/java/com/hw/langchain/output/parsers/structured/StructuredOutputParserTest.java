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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.langchain.schema.OutputParserException;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author HamaWhite
 */
class StructuredOutputParserTest {

    @Test
    void testParse() {
        var responseSchemas = List.of(
                new ResponseSchema("name", "desc"),
                new ResponseSchema("age", "desc"));
        var parser = StructuredOutputParser.fromResponseSchemas(responseSchemas);

        var text = "```json\n{\"name\": \"John\", \"age\": 30}\n```";
        var result = parser.parse(text);

        var expectedResult = new ObjectMapper().createObjectNode()
                .put("name", "John")
                .put("age", 30);

        assertEquals(expectedResult, result);
    }

    @Test
    void testInvalidJsonInput() {
        var responseSchemas = List.of(
                new ResponseSchema("name", "desc"),
                new ResponseSchema("age", "desc"));
        var parser = StructuredOutputParser.fromResponseSchemas(responseSchemas);

        var text = "```json\n{\"name\": \"John\"}\n```";
        assertThrows(OutputParserException.class, () -> parser.parse(text));
    }

}