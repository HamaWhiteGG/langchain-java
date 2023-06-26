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

package com.hw.langchain.vectorstores.pinecone;

import com.hw.langchain.document.loaders.text.TextLoader;
import com.hw.langchain.embeddings.openai.OpenAIEmbeddings;
import com.hw.langchain.text.splitter.CharacterTextSplitter;

import org.junit.jupiter.api.Test;

/**
 * <a href="https://python.langchain.com/docs/modules/data_connection/vectorstores/integrations/pinecone">pinecone</a>
 *
 * @author HamaWhite
 */
class PineconeTest {

    @Test
    void testPinecone() {
        String filePath = "../docs/extras/modules/state_of_the_union.txt";
        var loader = new TextLoader(filePath);
        var documents = loader.load();

        var textSplitter = CharacterTextSplitter.builder()
                .chunkSize(1000)
                .chunkOverlap(0)
                .build();
        var docs = textSplitter.splitDocuments(documents);

        var embeddings = new OpenAIEmbeddings();

        System.out.println("end");
    }

}