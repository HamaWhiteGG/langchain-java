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

package com.hw.openai.function;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.hw.openai.utils.JsonUtils.convertFromJsonStr;

/**
 * @author HamaWhite
 */
public class FunctionExecutor {

    private static final Map<String, Function<?, ?>> FUNCTION_MAP = new HashMap<>(16);

    public static <T> void register(String functionName, Function<T, ?> function) {
        FUNCTION_MAP.put(functionName, function);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R execute(String functionName, Class<T> clazz, String arguments) throws ClassCastException {
        Function<T, R> function = (Function<T, R>) FUNCTION_MAP.get(functionName);
        if (function == null) {
            throw new IllegalArgumentException("No function registered with name: " + functionName);
        }
        T input = convertFromJsonStr(arguments, clazz);
        return function.apply(input);
    }
}
