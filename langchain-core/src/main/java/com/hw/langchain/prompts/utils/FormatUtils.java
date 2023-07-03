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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HamaWhite
 */
public class FormatUtils {

    private static final Pattern PATTERN = Pattern.compile("\\{([^{}]+)}(?!})");

    private FormatUtils() {
    }

    /**
     * Formats the given template string by replacing variables with corresponding values.
     *
     * @param template the template string to format
     * @param kwargs   a map of variables and their corresponding values
     * @return the formatted string
     */
    public static String formatTemplate(String template, Map<String, Object> kwargs) {
        String result = template;
        for (Map.Entry<String, Object> entry : kwargs.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue().toString();
            result = result.replace(placeholder, value);
        }

        // In Python format() method, the curly braces '{{}}' are used to represent the output '{}'.
        return result.replace("{{", "{").replace("}}", "}");
    }

    /**
     * Finds all variables enclosed in curly braces '{' and '}' in the input string,
     * excluding variables enclosed in double curly braces '{{' or more.
     *
     * @param input the input string to search for variables
     * @return a list of variables found in the input string
     */
    public static List<String> findVariables(String input) {
        List<String> variables = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(input);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }
}
