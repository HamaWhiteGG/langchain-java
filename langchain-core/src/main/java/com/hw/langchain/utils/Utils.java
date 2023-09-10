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

package com.hw.langchain.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author HamaWhite
 */
public class Utils {

    /**
     * Retrieves the value from the dictionary or environment variable.
     *
     * @param data         The dictionary data.
     * @param key          The key to lookup in the dictionary.
     * @param envKey       The key to lookup in the environment variables.
     * @param defaultValue The default value(s) to return if the key is not found or the value is null or empty.
     * @return The retrieved value. If the key is found and the value is not null or empty, it is returned.
     * Otherwise, the value from the environment variable is returned if it is not null or empty.
     * If the default value(s) are provided, the first default value is returned.
     * If none of the above conditions are met, null is returned.
     */
    public static String getFromDictOrEnv(Map<String, Object> data, String key, String envKey, String... defaultValue) {
        if (data.containsKey(key) && data.get(key) != null) {
            return data.get(key).toString();
        } else {
            return getFromEnv(key, envKey, defaultValue);
        }
    }

    /**
     * Retrieves the value from the dictionary
     * @param data
     * @param key
     * @return
     */
    public static String getFromDict(Map<String, Object> data, String key) {
        if (data.containsKey(key) && data.get(key) != null) {
            return data.get(key).toString();
        }
        return null;
    }

    /**
     * Retrieves the value from the environment variable or the default value.
     *
     * @param key          The key to lookup in the environment variables.
     * @param envKey       The key to lookup in the environment variables.
     * @param defaultValue The default value(s) to return if the environment variable is not found or its value is null or empty.
     * @return The retrieved value. If the environment variable is not null or empty, it is returned.
     * If the default value(s) are provided, the first default value is returned.
     * If none of the above conditions are met, an {@code IllegalArgumentException} is thrown.
     * @throws IllegalArgumentException if the environment variable is not found and no default value is provided.
     */
    public static String getFromEnv(String key, String envKey, String... defaultValue) {
        String envValue = System.getenv(envKey);
        if (StringUtils.isNotEmpty(envValue)) {
            return envValue;
        } else if (defaultValue.length > 0) {
            return defaultValue[0];
        } else {
            throw new IllegalArgumentException(
                    String.format(
                            "Did not find %s, please add an environment variable `%s` which contains it, or pass `%s` as a named parameter.",
                            key, envKey, key));
        }
    }

    /**
     * Retrieves the value from the original value, environment variable, or default value.
     *
     * @param originalValue The original value to check.
     * @param envKey        The key to lookup in the environment variables.
     * @param defaultValue  The default value(s) to return if the original value and environment variable are empty or null.
     * @return The retrieved value. If the original value is not empty, it is returned.
     * If the environment variable is not empty, it is returned.
     * If the default value(s) are provided, the first default value is returned.
     * If none of the above conditions are met, null is returned.
     */
    public static String getOrEnvOrDefault(String originalValue, String envKey, String... defaultValue) {
        if (StringUtils.isNotEmpty(originalValue)) {
            return originalValue;
        }
        return getFromEnv(envKey, envKey, defaultValue);
    }
}
