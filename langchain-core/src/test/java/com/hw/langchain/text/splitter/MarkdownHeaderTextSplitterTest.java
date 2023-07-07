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

import com.hw.langchain.schema.Document;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author HamaWhite
 */
class MarkdownHeaderTextSplitterTest {

    /**
     * Test markdown splitter by header: Case 1.
     */
    @Test
    void testMdHeaderTextSplitter1() {
        String markdownDocument = """
                # Foo

                    ## Bar

                Hi this is Jim

                Hi this is Joe

                 ## Baz

                 Hi this is Molly
                """;

        List<Pair<String, String>> headersToSplitOn = List.of(
                Pair.of("#", "Header 1"),
                Pair.of("##", "Header 2"));

        MarkdownHeaderTextSplitter markdownSplitter = new MarkdownHeaderTextSplitter(headersToSplitOn);
        List<Document> output = markdownSplitter.splitText(markdownDocument);

        List<Document> expectedOutput = List.of(
                new Document(
                        "Hi this is Jim  \nHi this is Joe",
                        Map.of("Header 1", "Foo", "Header 2", "Bar")),
                new Document(
                        "Hi this is Molly",
                        Map.of("Header 1", "Foo", "Header 2", "Baz")));
        assertEquals(expectedOutput, output);
    }

    /**
     * Test markdown splitter by header: Case 2.
     */
    @Test
    void testMdHeaderTextSplitter2() {
        String markdownDocument = """
                # Foo

                    ## Bar

                Hi this is Jim

                Hi this is Joe

                 ### Boo

                 Hi this is Lance

                 ## Baz

                 Hi this is Molly
                """;

        List<Pair<String, String>> headersToSplitOn = List.of(
                Pair.of("#", "Header 1"),
                Pair.of("##", "Header 2"),
                Pair.of("###", "Header 3"));

        MarkdownHeaderTextSplitter markdownSplitter = new MarkdownHeaderTextSplitter(headersToSplitOn);
        List<Document> output = markdownSplitter.splitText(markdownDocument);

        List<Document> expectedOutput = List.of(
                new Document(
                        "Hi this is Jim  \nHi this is Joe",
                        Map.of("Header 1", "Foo", "Header 2", "Bar")),
                new Document(
                        "Hi this is Lance",
                        Map.of("Header 1", "Foo", "Header 2", "Bar", "Header 3", "Boo")),
                new Document(
                        "Hi this is Molly",
                        Map.of("Header 1", "Foo", "Header 2", "Baz")));
        assertEquals(expectedOutput, output);
    }

    /**
     * Test markdown splitter by header: Case 3.
     */
    @Test
    void testMdHeaderTextSplitter3() {
        String markdownDocument = """
                # Foo

                    ## Bar

                Hi this is Jim

                Hi this is Joe

                 ### Boo

                 Hi this is Lance

                 #### Bim

                 Hi this is John

                 ## Baz

                 Hi this is Molly
                """;

        List<Pair<String, String>> headersToSplitOn = List.of(
                Pair.of("#", "Header 1"),
                Pair.of("##", "Header 2"),
                Pair.of("###", "Header 3"),
                Pair.of("####", "Header 4"));

        MarkdownHeaderTextSplitter markdownSplitter = new MarkdownHeaderTextSplitter(headersToSplitOn);
        List<Document> output = markdownSplitter.splitText(markdownDocument);

        List<Document> expectedOutput = List.of(
                new Document(
                        "Hi this is Jim  \nHi this is Joe",
                        Map.of("Header 1", "Foo", "Header 2", "Bar")),
                new Document(
                        "Hi this is Lance",
                        Map.of("Header 1", "Foo", "Header 2", "Bar", "Header 3", "Boo")),
                new Document(
                        "Hi this is John",
                        Map.of("Header 1", "Foo", "Header 2", "Bar", "Header 3", "Boo", "Header 4", "Bim")),
                new Document(
                        "Hi this is Molly",
                        Map.of("Header 1", "Foo", "Header 2", "Baz")));
        assertEquals(expectedOutput, output);
    }
}