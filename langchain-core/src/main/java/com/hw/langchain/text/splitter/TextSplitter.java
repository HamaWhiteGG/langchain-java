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

package com.hw.langchain.text.splitter;

import cn.hutool.core.util.StrUtil;
import com.hw.langchain.schema.BaseDocumentTransformer;
import com.hw.langchain.schema.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Interface for splitting text into chunks.
 *
 * @author HamaWhite
 */
@SuperBuilder
public abstract class TextSplitter implements BaseDocumentTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(TextSplitter.class);

    /**
     * Maximum size of chunks to return.
     */
    @Builder.Default
    protected int chunkSize = 4000;

    /**
     * Overlap in characters between chunks.
     */
    @Builder.Default
    protected int chunkOverlap = 200;

    /**
     * Function that measures the length of given chunks.
     */
    @Builder.Default
    protected Function<String, Integer> lengthFunction = String::length;

    /**
     * Whether or not to keep the separator in the chunks.
     */
    protected boolean keepSeparator;

    /**
     * If `true`, includes chunk's start index in metadata
     */
    protected boolean addStartIndex;

    /**
     * Split text into multiple components.
     */
    public abstract List<String> splitText(String text);

    /**
     * Create documents from a list of texts.
     */
    public List<Document> createDocuments(List<String> texts, List<Map<String, Object>> metadatas) {
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            int index = -1;
            for (String chunk : splitText(text)) {
                Map<String, Object> metadata = new HashMap<>(metadatas.get(i));
                if (addStartIndex) {
                    index = text.indexOf(chunk, index + 1);
                    metadata.put("start_index", index);
                }
                Document newDoc = new Document(chunk, metadata);
                documents.add(newDoc);
            }
        }
        return documents;
    }

    /**
     * Split documents.
     */
    public List<Document> splitDocuments(List<Document> documents) {
        List<String> texts = new ArrayList<>();
        List<Map<String, Object>> metadatas = new ArrayList<>();
        for (Document doc : documents) {
            texts.add(doc.getPageContent());
            metadatas.add(doc.getMetadata());
        }
        return createDocuments(texts, metadatas);
    }

    private String joinDocs(List<String> docs, String separator) {
        String text = StrUtil.strip(String.join(separator, docs), " ");
        return text.isEmpty() ? null : text;
    }

    /**
     * We now want to combine these smaller pieces into medium size chunks to send to the LLM.
     */
    protected List<String> mergeSplits(List<String> splits, String separator) {
        int separatorLength = lengthFunction.apply(separator);

        List<String> docs = new ArrayList<>();
        List<String> currentDoc = new ArrayList<>();
        int total = 0;
        for (String d : splits) {
            int length = lengthFunction.apply(d);
            if (total + length + (!currentDoc.isEmpty() ? separatorLength : 0) > chunkSize) {
                if (total > chunkSize) {
                    LOG.warn("Created a chunk of size {}, which is longer than the specified {}", total, chunkSize);
                }
                if (!currentDoc.isEmpty()) {
                    String doc = joinDocs(currentDoc, separator);
                    if (doc != null) {
                        docs.add(doc);
                    }
                    // we have a larger chunk than in the chunk overlap or if we still have any chunks and the length is
                    // long
                    while (total > chunkOverlap
                            || (total + length + (!currentDoc.isEmpty() ? separatorLength : 0) > chunkSize
                                    && total > 0)) {
                        total -= lengthFunction.apply(currentDoc.get(0))
                                + (currentDoc.size() > 1 ? separatorLength : 0);
                        currentDoc.remove(0);
                    }
                }
            }
            currentDoc.add(d);
            total += length + (currentDoc.size() > 1 ? separatorLength : 0);
        }

        String doc = joinDocs(currentDoc, separator);
        if (doc != null) {
            docs.add(doc);
        }
        return docs;
    }

    /**
     * Transform sequence of documents by splitting them.
     */
    @Override
    public List<Document> transformDocuments(List<Document> documents, Map<String, Object> kwargs) {
        return this.splitDocuments(documents);
    }
}
