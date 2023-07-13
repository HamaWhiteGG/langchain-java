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

package com.hw.langchain.chains.query.constructor.ir;

import java.util.List;
import java.util.Map;

/**
 * Defines interface for IR translation using visitor pattern.
 *
 * @author HamaWhite
 */

public abstract class Visitor {

    protected List<Comparator> allowedComparators;

    protected List<Operator> allowedOperators;

    protected Visitor(List<Comparator> allowedComparators, List<Operator> allowedOperators) {
        this.allowedComparators = allowedComparators;
        this.allowedOperators = allowedOperators;
    }

    public List<Comparator> getAllowedComparators() {
        return allowedComparators;
    }

    public List<Operator> getAllowedOperators() {
        return allowedOperators;
    }

    /**
     * Translates an Operation expression.
     *
     * @param operation the Operation expression to translate
     * @return a map containing the translated result
     */
    public abstract Map<String, Object> visitOperation(Operation operation);

    /**
     * Translates a Comparison expression.
     *
     * @param comparison the Comparison expression to translate
     * @return a map containing the translated result
     */
    public abstract Map<String, Object> visitComparison(Comparison comparison);

    /**
     * Translates a StructuredQuery expression.
     *
     * @param structuredQuery the StructuredQuery expression to translate
     * @return a map containing the translated result
     */
    public abstract Map<String, Object> visitStructuredQuery(StructuredQuery structuredQuery);
}
