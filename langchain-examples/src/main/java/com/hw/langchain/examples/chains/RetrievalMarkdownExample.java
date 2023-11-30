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

package com.hw.langchain.examples.chains;

import com.hw.langchain.chains.query.constructor.schema.AttributeInfo;
import com.hw.langchain.chains.retrieval.qa.base.RetrievalQa;
import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.document.loaders.notion.NotionDirectoryLoader;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.retrievers.self.query.base.SelfQueryRetriever;
import com.hw.langchain.schema.Document;
import com.hw.langchain.text.splitter.MarkdownHeaderTextSplitter;
import com.hw.langchain.text.splitter.RecursiveCharacterTextSplitter;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static com.hw.langchain.examples.utils.PrintUtils.println;
import static com.hw.langchain.examples.vectorstores.PineconeExample.*;

/**
 * <a href="https://python.langchain.com/docs/use_cases/question_answering/document-context-aware-QA">Context aware text splitting and QA/Chat</a>
 * <p>
 * export PINECONE_API_KEY=xxx
 * export PINECONE_ENV=xxx
 *
 * @author HamaWhite
 */
public class RetrievalMarkdownExample {

    public static final String NAMESPACE = "markdown";

    public static void main(String[] args) {
        // Load Notion page as a markdown file
        String path = "data/extras/use_cases/question_answering/notion_db/";
        var loader = new NotionDirectoryLoader(path);
        var docs = loader.load();
        var mdFile = docs.get(0).getPageContent();

        // Let's create groups based on the section headers in our page
        List<Pair<String, String>> headersToSplitOn = List.of(Pair.of("###", "Section"));
        MarkdownHeaderTextSplitter markdownSplitter = new MarkdownHeaderTextSplitter(headersToSplitOn);
        List<Document> mdHeaderSplits = markdownSplitter.splitText(mdFile);

        // Define our text splitter
        var textSplitter = RecursiveCharacterTextSplitter.builder()
                .chunkSize(500)
                .chunkOverlap(0)
                .keepSeparator(true)
                .build();
        var allSplits = textSplitter.splitDocuments(mdHeaderSplits);

        // Build pinecone and keep the metadata
        var vectorStore = initializePineconeIndex(NAMESPACE, allSplits);

        // Define our metadata
        var metadataFieldInfo = List.of(
                new AttributeInfo("Section", "Part of the document that the text comes from",
                        "string or list[string]"));
        var documentContentDescription = "Major sections of the document";

        // Define self query retriever
        var llm = OpenAI.builder().temperature(0).requestTimeout(30).build().init();
        var retriever = SelfQueryRetriever.fromLLM(llm, vectorStore, documentContentDescription, metadataFieldInfo);

        // create chat or Q+A apps that are aware of the explicit document structure.
        var chat = ChatOpenAI.builder().temperature(0).build().init();
        var qaChain = RetrievalQa.fromChainType(chat, retriever);
        var result = qaChain.run("Summarize the Testing section of the document");
        println(result);
    }
}
