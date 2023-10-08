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

package com.hw.langchain.chains.query.constructor;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hw.langchain.exception.LangChainException;

/**
 * @author HamaWhite
 */
public class JsonUtils {

    private JsonUtils() {
        // private constructor to hide the implicit public one
        throw new IllegalStateException("Utility class");
    }

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    public static String toJsonStringWithIndent(Object object, int indent) {
        try {
            ObjectWriter writer = OBJECT_MAPPER.writer(getPrettyPrinter(indent));
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new LangChainException("Failed to format attribute info.", e);
        }
    }

    public static String toJsonStringWithIndent(Object object) {
        return toJsonStringWithIndent(object, 4);
    }

    public static String writeValueAsString(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new LangChainException("Failed to format attribute info.", e);
        }
    }

    public static <T> T convertFromJsonStr(String jsonStr, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            throw new LangChainException("Failed to deserialize json str", e);
        }
    }

    public static <T> T convertFromJsonStr(String jsonStr, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, typeReference);
        } catch (JsonProcessingException e) {
            throw new LangChainException("Failed to deserialize json str", e);
        }
    }

    private static DefaultPrettyPrinter getPrettyPrinter(int indent) {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance);
        printer.indentObjectsWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withIndent(StrUtil.repeat(" ", indent)));
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withIndent(StrUtil.repeat(" ", indent)));
        return printer;
    }
}
