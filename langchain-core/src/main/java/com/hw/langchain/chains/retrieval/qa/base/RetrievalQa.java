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

package com.hw.langchain.chains.retrieval.qa.base;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.combine.documents.base.BaseCombineDocumentsChain;
import com.hw.langchain.chains.question.answering.ChainType;
import com.hw.langchain.schema.BaseRetriever;
import com.hw.langchain.schema.Document;

import java.util.List;

import static com.hw.langchain.chains.question.answering.ChainType.STUFF;
import static com.hw.langchain.chains.question.answering.init.Init.loadQaChain;

/**
 * Chain for question-answering against an index.
 *
 * @author HamaWhite
 */
public class RetrievalQa extends BaseRetrievalQA {

    private final BaseRetriever retriever;

    public RetrievalQa(BaseCombineDocumentsChain combineDocumentsChain, BaseRetriever retriever) {
        super(combineDocumentsChain);
        this.retriever = retriever;
    }

    /**
     * Load chain from chain type.
     */
    public static BaseRetrievalQA fromChainType(BaseLanguageModel llm, BaseRetriever retriever) {
        return fromChainType(llm, STUFF, retriever);
    }

    /**
     * Load chain from chain type.
     */
    public static BaseRetrievalQA fromChainType(BaseLanguageModel llm, ChainType chainType, BaseRetriever retriever) {
        BaseCombineDocumentsChain combineDocumentsChain = loadQaChain(llm, chainType);
        return new RetrievalQa(combineDocumentsChain, retriever);
    }

    @Override
    public List<Document> getDocs(String question) {
        return retriever.getRelevantDocuments(question);
    }

    @Override
    public String chainType() {
        return "retrieval_qa";
    }
}
