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

package com.hw.langchain.chains.combine.documents.base;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.schema.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author HamaWhite
 */
public class BaseUtils {

    private BaseUtils() {
    }

    /**
     * Format a document into a string based on a prompt template.
     */
    public static String formatDocument(Document doc, BasePromptTemplate prompt) {
        Map<String, Object> baseInfo = Maps.newHashMap();
        baseInfo.put("page_content", doc.getPageContent());
        baseInfo.putAll(doc.getMetadata());

        Set<String> missingMetadata = Sets.newHashSet(prompt.getInputVariables());
        missingMetadata.removeAll(baseInfo.keySet());

        if (!missingMetadata.isEmpty()) {
            List<String> requiredMetadata = prompt.getInputVariables().stream()
                    .filter(iv -> !"page_content".equals(iv))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException(
                    "Document prompt requires documents to have metadata variables: " + requiredMetadata
                            + ". Received document with missing metadata: " + missingMetadata + ".");
        }
        Map<String, Object> documentInfo = Maps.filterKeys(baseInfo, prompt.getInputVariables()::contains);
        return prompt.format(documentInfo);
    }
}
