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
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.chains.query.constructor.ir.Comparator;
import com.hw.langchain.chains.query.constructor.ir.Operator;
import com.hw.langchain.chains.query.constructor.schema.AttributeInfo;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.prompts.few.shot.FewShotPromptTemplate;
import lombok.var;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hw.langchain.chains.query.constructor.JsonUtils.toJsonStringWithIndent;
import static com.hw.langchain.chains.query.constructor.prompt.Prompt.*;
import static com.hw.langchain.prompts.utils.FormatUtils.formatTemplate;

/**
 * @author HamaWhite
 */
public class BaseUtils {

    private BaseUtils() {
    }

    public static String formatAttributeInfo(List<AttributeInfo> infos) {
        Map<String, Map<String, Object>> infoMaps = Maps.newHashMap();
        for (AttributeInfo info : infos) {
            Map<String, Object> map = new ObjectMapper().convertValue(info, new TypeReference<Map<String, Object>>() {});
            infoMaps.put((String) map.remove("name"), map);
        }
        return toJsonStringWithIndent(infoMaps, 4)
                .replace("{", "{{")
                .replace("}", "}}");
    }

    private static BasePromptTemplate getPrompt(String documentContents, List<AttributeInfo> attributeInfo,
            List<Map<String, Object>> examples, List<Comparator> allowedComparators, List<Operator> allowedOperators,
            boolean enableLimit) {
        String attributeStr = formatAttributeInfo(attributeInfo);
        allowedComparators = allowedComparators != null ? allowedComparators : ListUtil.of(Comparator.values());
        allowedOperators = allowedOperators != null ? allowedOperators : ListUtil.of(Operator.values());

        String schema;
        if (enableLimit) {
            schema = formatTemplate(SCHEMA_WITH_LIMIT, createTemplateArguments(allowedComparators, allowedOperators));
            examples = examples != null ? examples : EXAMPLES_WITH_LIMIT;
        } else {
            schema = formatTemplate(DEFAULT_SCHEMA, createTemplateArguments(allowedComparators, allowedOperators));
            examples = examples != null ? examples : DEFAULT_EXAMPLES;
        }

        String prefix = formatTemplate(DEFAULT_PREFIX, MapUtil.of("schema", schema));
        String suffix = formatTemplate(DEFAULT_SUFFIX, MapBuilder.create(new HashMap<String, Object>())
                .put("i", examples.size() + 1)
                .put("content", documentContents)
                .put("attributes", attributeStr).map());

        var outputParser = StructuredQueryOutputParser.fromComponents(allowedComparators, allowedOperators);
        return new FewShotPromptTemplate(examples, EXAMPLE_PROMPT, prefix, suffix, ListUtil.of("query"), "\n\n",
                outputParser);
    }

    private static Map<String, Object> createTemplateArguments(List<Comparator> allowedComparators,
            List<Operator> allowedOperators) {
        return MapBuilder.create(new HashMap<String, Object>())
                .put("allowed_comparators", String.join(" | ", allowedComparators.stream().map(Comparator::value).toArray(String[]::new)))
                .put("allowed_operators", String.join(" | ", allowedOperators.stream().map(Operator::value).toArray(String[]::new))).map();
    }

    /**
     * Load a query constructor chain.
     *
     * @param llm                BaseLanguageModel to use for the chain.
     * @param documentContents   The contents of the document to be queried.
     * @param attributeInfo      A list of AttributeInfo objects describing the attributes of the document.
     * @param examples           Optional list of examples to use for the chain.
     * @param allowedComparators A list of allowed comparators.
     * @param allowedOperators   A list of allowed operators.
     * @param enableLimit        Whether to enable the limit operator. Defaults to False.
     * @return A LLMChain that can be used to construct queries.
     */
    public static LLMChain loadQueryConstructorChain(BaseLanguageModel llm, String documentContents,
            List<AttributeInfo> attributeInfo, List<Map<String, Object>> examples, List<Comparator> allowedComparators,
            List<Operator> allowedOperators, boolean enableLimit) {
        BasePromptTemplate prompt =
                getPrompt(documentContents, attributeInfo, examples, allowedComparators, allowedOperators, enableLimit);
        return new LLMChain(llm, prompt);
    }
}
