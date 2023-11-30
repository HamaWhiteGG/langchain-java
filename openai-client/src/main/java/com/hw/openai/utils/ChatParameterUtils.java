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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hw.openai.entity.chat.ChatFunction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate a structured ChatParameter object based on a given Java class.
 * It leverages Jackson's annotations to customize the JSON output per field.
 * <p>
 * The class is responsible for inspecting a Java class for specific annotations,
 * then generating a corresponding ChatParameter which follows certain schema conventions,
 * such as the inclusion of types, descriptions, and required fields.
 * <p>
 * Note: This utility requires that the Java class be annotated with Jackson annotations.
 *
 * @author HamaWhite
 */
public class ChatParameterUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Generates a {@link ChatFunction.ChatParameter} instance that represents the given class
     * schema in the form of a ChatParameter object.
     * <p>
     * This method analyzes all declared fields of the provided class and their annotations
     * to produce the appropriate JSON Schema based representation.
     *
     * @param clazz The class to be converted into a ChatParameter representation.
     * @return The {@link ChatFunction.ChatParameter} instance representing the JSON Schema of
     * the provided class.
     */
    public static ChatFunction.ChatParameter generate(Class<?> clazz) {
        ChatFunction.ChatParameter chatParameter = new ChatFunction.ChatParameter();

        chatParameter.setType("object");
        ObjectNode properties = OBJECT_MAPPER.createObjectNode();
        List<String> required = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            JsonPropertyDescription descriptionAnnotation = field.getAnnotation(JsonPropertyDescription.class);

            String fieldName = jsonProperty != null && !jsonProperty.value().isEmpty()
                    ? jsonProperty.value()
                    : field.getName();

            ObjectNode fieldNode = OBJECT_MAPPER.createObjectNode();
            fieldNode.put("type", mapJavaTypeToJsonType(field.getType()));
            if (descriptionAnnotation != null) {
                fieldNode.put("description", descriptionAnnotation.value());
            }

            // Enum specific logic
            if (field.getType().isEnum()) {
                ArrayNode enumNode = fieldNode.putArray("enum");
                for (Object enumConstant : field.getType().getEnumConstants()) {
                    enumNode.add(enumConstant.toString().toLowerCase());
                }
            }
            properties.set(fieldName, fieldNode);

            if (jsonProperty != null && jsonProperty.required()) {
                required.add(fieldName);
            }
        }

        chatParameter.setProperties(properties);
        chatParameter.setRequired(required);
        return chatParameter;
    }

    /**
     * Maps a Java class type to its corresponding JSON Schema data type.
     * <p>
     * This is a utility method responsible for converting Java types to JSON Schema
     * compatible type strings, considering common data types and their JSON equivalents.
     *
     * @param type The Java Class type to be converted.
     * @return A string representing the equivalent JSON Schema data type.
     */
    private static String mapJavaTypeToJsonType(Class<?> type) {
        if (type.isEnum() || CharSequence.class.isAssignableFrom(type)) {
            return "string";
        } else if (Number.class.isAssignableFrom(type) || type == int.class ||
                type == long.class || type == double.class || type == float.class) {
            return "number";
        } else if (type == boolean.class || Boolean.class.isAssignableFrom(type)) {
            return "boolean";
        } else if (type.isArray() || List.class.isAssignableFrom(type)) {
            return "array";
        } else {
            return "object";
        }
    }
}
