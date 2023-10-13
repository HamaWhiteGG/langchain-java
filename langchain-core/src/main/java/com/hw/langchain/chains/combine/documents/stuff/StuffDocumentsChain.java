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

package com.hw.langchain.chains.combine.documents.stuff;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import com.hw.langchain.chains.combine.documents.base.BaseCombineDocumentsChain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.schema.Document;

import lombok.var;
import org.apache.commons.lang3.tuple.Pair;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hw.langchain.chains.combine.documents.base.BaseUtils.formatDocument;
import static com.hw.langchain.chains.combine.documents.stuff.StuffUtils.getDefaultDocumentPrompt;

/**
 * Chain that combines documents by stuffing into context.
 *
 * @author HamaWhite
 */
public class StuffDocumentsChain extends BaseCombineDocumentsChain {

    /**
     * LLM wrapper to use after formatting documents.
     */
    private final LLMChain llmChain;

    /**
     * Prompt to use to format each document.
     */
    private final BasePromptTemplate documentPrompt;

    /**
     * The variable name in the llmChain to put the documents in.
     * If only one variable in the llmChain, this need not be provided.
     */
    private String documentVariableName;

    /**
     * The string with which to join the formatted documents.
     */
    private final String documentSeparator;

    public StuffDocumentsChain(LLMChain llmChain, String documentVariableName) {
        this(llmChain, getDefaultDocumentPrompt(), documentVariableName, "\n\n");
    }

    public StuffDocumentsChain(LLMChain llmChain, BasePromptTemplate documentPrompt, String documentVariableName,
            String documentSeparator) {
        this.llmChain = llmChain;
        this.documentPrompt = documentPrompt;
        this.documentVariableName = documentVariableName;
        this.documentSeparator = documentSeparator;

        // Get default document variable name, if not provided.
        getDefaultDocumentVariableName();
    }

    /**
     * Get default document variable name, if not provided.
     */
    private void getDefaultDocumentVariableName() {
        List<String> llmChainVariables = llmChain.getPrompt().getInputVariables();
        if (documentVariableName == null) {
            if (llmChainVariables.size() == 1) {
                documentVariableName = llmChainVariables.get(0);
            } else {
                throw new IllegalArgumentException(
                        "documentVariableName must be provided if there are multiple llmChainVariables");
            }
        } else {
            if (!llmChainVariables.contains(documentVariableName)) {
                throw new IllegalArgumentException("documentVariableName " + documentVariableName
                        + " was not found in llmChain inputVariables: " + llmChainVariables);
            }
        }
    }

    private Map<String, Object> getInputs(List<Document> docs, Map<String, Object> kwargs) {
        // Format each document according to the prompt
        List<String> docStrings = docs.stream()
                .map(doc -> formatDocument(doc, documentPrompt))
                .collect(Collectors.toList());
        // Join the documents together to put them in the prompt.
        Map<String, Object> inputs = Maps.filterKeys(kwargs, llmChain.getPrompt().getInputVariables()::contains);
        inputs.put(documentVariableName, String.join(documentSeparator, docStrings));
        return inputs;
    }

    /**
     * Stuff all documents into one prompt and pass to LLM.
     */
    @Override
    public Pair<String, Map<String, String>> combineDocs(List<Document> docs, Map<String, Object> kwargs) {
        Map<String, Object> inputs = getInputs(docs, kwargs);
        // Call predict on the LLM.
        return Pair.of(llmChain.predict(inputs), MapUtil.empty());
    }

    @Override
    public Flux<Pair<String, Map<String, String>>> asyncCombineDocs(List<Document> docs, Map<String, Object> kwargs) {
        var inputs = getInputs(docs, kwargs);
        return llmChain.asyncPredict(inputs).map(s -> Pair.of(s, MapUtil.empty()));
    }

    @Override
    public String chainType() {
        return "stuff_documents_chain";
    }
}
