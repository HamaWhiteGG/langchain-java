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
import com.hw.langchain.chains.query.constructor.ir.Visitor;
import com.hw.langchain.exception.LangChainException;
import com.hw.langchain.retrievers.self.query.pinecone.PineconeTranslator;
import com.hw.langchain.vectorstores.base.VectorStore;
import com.hw.langchain.vectorstores.pinecone.Pinecone;

import java.util.Map;

/**
 * @author HamaWhite
 */
public class BaseUtils {

    private BaseUtils() {
    }

    private static final Map<Class<? extends VectorStore>, Class<? extends Visitor>> BUILTIN_TRANSLATORS = MapUtil.of(
            Pinecone.class, PineconeTranslator.class);

    /**
     * Get the translator class corresponding to the vector store class.
     *
     * @param vectorStore the VectorStore instance
     * @return the Visitor instance
     * @throws IllegalArgumentException if the vector store type is not supported
     */
    public static Visitor getBuiltinTranslator(VectorStore vectorStore) {
        Class<? extends VectorStore> vectorStoreCls = vectorStore.getClass();
        if (!BUILTIN_TRANSLATORS.containsKey(vectorStoreCls)) {
            throw new IllegalArgumentException("Self query retriever with Vector Store type " +
                    vectorStoreCls.getName() + " not supported.");
        }
        try {
            return BUILTIN_TRANSLATORS.get(vectorStoreCls).getConstructor().newInstance();
        } catch (Exception e) {
            throw new LangChainException(e);
        }
    }
}
