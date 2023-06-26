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

package com.hw.langchain.text.splitter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author HamaWhite
 */
public class TextSplitterUtils {

    private TextSplitterUtils() {
    }

    public static List<String> splitTextWithRegex(String text, String separator, boolean keepSeparator) {
        List<String> splits = new ArrayList<>();

        if (StringUtils.isNotEmpty(separator)) {
            if (keepSeparator) {
                // The parentheses in the pattern keep the delimiters in the result.
                Pattern pattern = Pattern.compile(String.format("(%s)", Pattern.quote(separator)));
                String[] parts = pattern.split(text, -1);
                for (int i = 1; i < parts.length; i += 2) {
                    splits.add(parts[i] + parts[i + 1]);
                }
                if (parts.length % 2 == 0) {
                    splits.add(parts[parts.length - 1]);
                }
                splits.add(0, parts[0]);
            } else {
                splits = List.of(text.split(separator));
            }
        } else {
            splits = List.of(text);
        }
        return splits.stream().filter(StringUtils::isNotEmpty).toList();
    }
}
