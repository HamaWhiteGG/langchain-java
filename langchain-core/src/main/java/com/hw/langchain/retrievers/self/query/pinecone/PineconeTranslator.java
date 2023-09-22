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

package com.hw.langchain.retrievers.self.query.pinecone;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Maps;
import com.hw.langchain.chains.query.constructor.ir.*;

import java.util.List;
import java.util.Map;

/**
 * Logic for converting internal query language elements to valid filters.
 *
 * @author HamaWhite
 */
public class PineconeTranslator extends Visitor {

    public PineconeTranslator() {
        super(null, ListUtil.of(Operator.AND, Operator.OR));
    }

    private String formatFunc(StringEnum<?> func) {
        return "$" + func.value();
    }

    @Override
    public Map<String, Object> visitOperation(Operation operation) {
        return MapUtil.empty();
    }

    @Override
    public Map<String, Object> visitComparison(Comparison comparison) {
        return MapUtil.of(
                comparison.getAttribute(),
                MapUtil.of(formatFunc(comparison.getComparator()), comparison.getValue()));
    }

    @Override
    public Map<String, Object> visitStructuredQuery(StructuredQuery structuredQuery) {
        if (structuredQuery.getFilter() != null) {
            return structuredQuery.getFilter().accept(this);
        }
        return Maps.newHashMap();
    }
}
