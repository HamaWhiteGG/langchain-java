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

import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Maps;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.schema.Document;

import lombok.var;
import org.apache.commons.lang3.tuple.Pair;

import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base interface for chains combining documents.
 *
 * @author HamaWhite
 */
public abstract class BaseCombineDocumentsChain extends Chain {

    protected String inputKey = "input_documents";

    protected String outputKey = "output_text";

    @Override
    public List<String> inputKeys() {
        return ListUtil.of(inputKey);
    }

    @Override
    public List<String> outputKeys() {
        return ListUtil.of(outputKey);
    }

    /**
     * Return the prompt length given the documents passed in.
     * Returns None if the method does not depend on the prompt length.
     */
    public Optional<Integer> promptLength(List<Document> docs, Map<String, Object> kwargs) {
        return Optional.empty();
    }

    /**
     * Combine documents into a single string.
     */
    public abstract Pair<String, Map<String, String>> combineDocs(List<Document> docs, Map<String, Object> kwargs);

    /**
     * Combine documents into a single string async.
     */
    public abstract Flux<Pair<String, Map<String, String>>> asyncCombineDocs(List<Document> docs,
            Map<String, Object> kwargs);

    @Override
    protected Map<String, String> innerCall(Map<String, Object> inputs) {
        @SuppressWarnings("unchecked")
        var docs = (List<Document>) inputs.get(inputKey);

        Map<String, Object> otherKeys = Maps.filterKeys(inputs, key -> !key.equals(inputKey));
        var result = this.combineDocs(docs, otherKeys);

        HashMap<String, String> extraReturnDict = new HashMap<>(result.getRight());
        extraReturnDict.put(outputKey, result.getLeft());
        return extraReturnDict;
    }

    @Override
    protected Flux<Map<String, String>> asyncInnerCall(Map<String, Object> inputs) {
        @SuppressWarnings("unchecked")
        var docs = (List<Document>) inputs.get(inputKey);

        Map<String, Object> otherKeys = Maps.filterKeys(inputs, key -> !key.equals(inputKey));
        var result = this.asyncCombineDocs(docs, otherKeys);

        return result.map(pair -> {
            var extraReturnDict = new HashMap<>(pair.getRight());
            extraReturnDict.put(outputKey, pair.getLeft());
            return extraReturnDict;
        });
    }
}
