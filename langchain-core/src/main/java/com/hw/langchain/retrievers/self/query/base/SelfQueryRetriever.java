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

package com.hw.langchain.retrievers.self.query.base;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.chains.query.constructor.ir.StructuredQuery;
import com.hw.langchain.chains.query.constructor.ir.Visitor;
import com.hw.langchain.chains.query.constructor.schema.AttributeInfo;
import com.hw.langchain.schema.BaseRetriever;
import com.hw.langchain.schema.Document;
import com.hw.langchain.vectorstores.base.SearchType;
import com.hw.langchain.vectorstores.base.VectorStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.chains.query.constructor.base.BaseUtils.loadQueryConstructorChain;
import static com.hw.langchain.retrievers.self.query.base.BaseUtils.getBuiltinTranslator;
import static com.hw.langchain.vectorstores.base.SearchType.SIMILARITY;

/**
 * Retriever that wraps around a vector store and uses an LLM to generate the vector store queries.
 *
 * @author HamaWhite
 */
public class SelfQueryRetriever implements BaseRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(SelfQueryRetriever.class);

    /**
     * The underlying vector store from which documents will be retrieved.
     */
    private final VectorStore vectorStore;

    /**
     * The LLMChain for generating the vector store queries.
     */
    private final LLMChain llmChain;

    /**
     * The search type to perform on the vector store.
     * Default value: "similarity"
     */
    private final SearchType searchType;

    /**
     * Keyword arguments to pass in to the vector store search.
     */
    private final Map<String, Object> searchKwargs;

    /**
     * Translator for turning internal query language into vectorstore search params.
     */
    private final Visitor structuredQueryTranslator;

    /**
     * Use original query instead of the revised new query from LLM.
     */
    private final boolean useOriginalQuery;

    public SelfQueryRetriever(VectorStore vectorStore, LLMChain llmChain, Visitor structuredQueryTranslator,
            boolean useOriginalQuery) {
        this(vectorStore, llmChain, SIMILARITY, Maps.newHashMap(), structuredQueryTranslator, useOriginalQuery);
    }

    public SelfQueryRetriever(VectorStore vectorStore, LLMChain llmChain, SearchType searchType,
            Map<String, Object> searchKwargs, Visitor structuredQueryTranslator, boolean useOriginalQuery) {
        this.vectorStore = vectorStore;
        this.llmChain = llmChain;
        this.searchType = searchType;
        this.searchKwargs = searchKwargs;
        this.structuredQueryTranslator = structuredQueryTranslator;
        this.useOriginalQuery = useOriginalQuery;
    }

    @Override
    public List<Document> getRelevantDocuments(String query) {
        Map<String, Object> inputs = llmChain.prepInputs(MapUtil.of("query", query));
        StructuredQuery structuredQuery = llmChain.predictAndParse(inputs);
        LOG.info("Structured Query: {}", structuredQuery);

        Map<String, Object> filter = structuredQueryTranslator.visitStructuredQuery(structuredQuery);
        return vectorStore.search(structuredQuery.getQuery(), searchType, filter);
    }

    public static SelfQueryRetriever fromLLM(BaseLanguageModel llm, VectorStore vectorStore, String documentContents,
            List<AttributeInfo> metadataFieldInfo) {
        Visitor structuredQueryTranslator = getBuiltinTranslator(vectorStore);
        return fromLLM(llm, vectorStore, documentContents, metadataFieldInfo, structuredQueryTranslator, false, false);
    }

    public static SelfQueryRetriever fromLLM(BaseLanguageModel llm, VectorStore vectorStore, String documentContents,
            List<AttributeInfo> metadataFieldInfo, Visitor structuredQueryTranslator,
            boolean enableLimit, boolean useOriginalQuery) {
        LLMChain llmChain = loadQueryConstructorChain(llm, documentContents, metadataFieldInfo, null,
                structuredQueryTranslator.getAllowedComparators(), structuredQueryTranslator.getAllowedOperators(),
                enableLimit);
        return new SelfQueryRetriever(vectorStore, llmChain, structuredQueryTranslator, useOriginalQuery);
    }
}
