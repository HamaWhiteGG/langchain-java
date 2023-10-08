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

package com.hw.langchain.chains.query.constructor.base;

import cn.hutool.core.collection.ListUtil;
import com.hw.langchain.chains.query.constructor.ir.Comparator;
import com.hw.langchain.chains.query.constructor.ir.Comparison;
import com.hw.langchain.chains.query.constructor.ir.Operator;
import com.hw.langchain.chains.query.constructor.ir.StructuredQuery;
import com.hw.langchain.schema.BaseOutputParser;
import com.hw.langchain.schema.OutputParserException;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hw.langchain.output.parsers.json.Json.parseAndCheckJsonMarkdown;

/**
 * @author HamaWhite
 */
public class StructuredQueryOutputParser extends BaseOutputParser<StructuredQuery> {

    @Override
    public StructuredQuery parse(String text) throws OutputParserException {
        try {
            List<String> expectedKeys = ListUtil.of("query", "filter");
            Map<String, Object> parsed = parseAndCheckJsonMarkdown(text, expectedKeys);
            Comparison filter = astParse(parsed.get("filter").toString());
            return new StructuredQuery(parsed.get("query").toString(), filter, 0);
        } catch (Exception e) {
            throw new OutputParserException("Parsing text\n" + text + "\nraised following error:\n" + e);
        }
    }

    /**
     * I couldn't find a direct Java equivalent of Lark, which is available in Python.
     * For the time being, let's manually parse the simpler ones.
     */
    public Comparison astParse(String filter) {
        // Remove leading and trailing spaces and split the string
        filter = filter.trim();
        String regex = "(\\w+)\\(\"([^\"]+)\", \"([^\"]+)\"\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filter);

        if (matcher.matches()) {
            // Parse the comparator
            String comparatorStr = matcher.group(1).toUpperCase();
            Comparator comparator = Comparator.valueOf(comparatorStr);
            // Parse the attribute and value
            String attribute = matcher.group(2);
            String value = matcher.group(3);
            return new Comparison(comparator, attribute, value);
        }
        throw new IllegalArgumentException("Invalid comparison string: " + filter);
    }

    public static StructuredQueryOutputParser fromComponents(List<Comparator> allowedComparators,
            List<Operator> allowedOperators) {
        // TODO: Finding the Java equivalent of Lark in Python, maybe ANTLR.
        return new StructuredQueryOutputParser();
    }
}
