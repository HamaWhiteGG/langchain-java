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

package com.hw.langchain.output.parsers.list;

import java.util.Arrays;
import java.util.List;

/**
 * Parse out comma separated lists.
 * @author HamaWhite
 */
public class CommaSeparatedListOutputParser extends ListOutputParser {

    @Override
    public List<String> parse(String text) {
        return Arrays.asList(text.trim().split(", "));
    }

    @Override
    public String getFormatInstructions() {
        return "Your response should be a list of comma-separated values, e.g., `foo, bar, baz`";
    }
}
