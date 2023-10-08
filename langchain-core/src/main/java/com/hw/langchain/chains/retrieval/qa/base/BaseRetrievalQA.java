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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.combine.documents.base.BaseCombineDocumentsChain;
import com.hw.langchain.chains.query.constructor.JsonUtils;
import com.hw.langchain.schema.Document;

import lombok.var;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
public abstract class BaseRetrievalQA extends Chain {

    /**
     * Chain to use to combine the documents.
     */
    private final BaseCombineDocumentsChain combineDocumentsChain;

    private final String inputKey = "query";

    private final String outputKey = "result";

    /**
     * Return the source documents.
     */
    private boolean returnSourceDocuments;

    protected BaseRetrievalQA(BaseCombineDocumentsChain combineDocumentsChain) {
        this.combineDocumentsChain = combineDocumentsChain;
    }

    @Override
    public List<String> inputKeys() {
        return ListUtil.of(inputKey);
    }

    @Override
    public List<String> outputKeys() {
        List<String> outputKeys = Lists.newArrayList(outputKey);
        if (returnSourceDocuments) {
            outputKeys.add("source_documents");
        }
        return outputKeys;
    }

    /**
     * Get documents to do question answering over.
     */
    public abstract List<Document> getDocs(String question);

    /**
     * Run getRelevantText and llm on input query.
     */
    @Override
    protected Map<String, String> innerCall(Map<String, Object> inputs) {
        var question = inputs.get(inputKey).toString();

        List<Document> docs = getDocs(question);
        inputs.put("input_documents", docs);
        if (!inputs.containsKey("question")) {
            inputs.put("question", question);
        }
        String answer = combineDocumentsChain.run(inputs);

        Map<String, String> result = Maps.newHashMap();
        result.put(outputKey, answer);
        if (this.returnSourceDocuments) {
            result.put("source_documents", JsonUtils.toJsonStringWithIndent(docs, 4));
        }
        return result;
    }

    @Override
    protected Flux<Map<String, String>> asyncInnerCall(Map<String, Object> inputs) {
        var question = inputs.get(inputKey).toString();

        List<Document> docs = getDocs(question);
        inputs.put("input_documents", docs);
        if (!inputs.containsKey("question")) {
            inputs.put("question", question);
        }
        Flux<String> answer = combineDocumentsChain.asyncRun(inputs);
        return answer.map(s -> MapUtil.of(outputKey, s));
    }
}
