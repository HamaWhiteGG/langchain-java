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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import com.hw.langchain.schema.Document;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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
        String markdownDocument = "" +
                "# Foo\n" +
                "\n" +
                "                    ## Bar\n" +
                "\n" +
                "                Hi this is Jim\n" +
                "\n" +
                "                Hi this is Joe\n" +
                "\n" +
                "                 ## Baz\n" +
                "\n" +
                "                 Hi this is Molly" +
                "";

        List<Pair<String, String>> headersToSplitOn = ListUtil.of(
                Pair.of("#", "Header 1"),
                Pair.of("##", "Header 2"));

        MarkdownHeaderTextSplitter markdownSplitter = new MarkdownHeaderTextSplitter(headersToSplitOn);
        List<Document> output = markdownSplitter.splitText(markdownDocument);

        List<Document> expectedOutput = ListUtil.of(
                new Document(
                        "Hi this is Jim  \nHi this is Joe",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Bar").map()),
                new Document(
                        "Hi this is Molly",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Baz").map()));
        assertEquals(expectedOutput, output);
    }

    /**
     * Test markdown splitter by header: Case 2.
     */
    @Test
    void testMdHeaderTextSplitter2() {
        String markdownDocument = "" +
                "# Foo\n" +
                "\n" +
                "                    ## Bar\n" +
                "\n" +
                "                Hi this is Jim\n" +
                "\n" +
                "                Hi this is Joe\n" +
                "\n" +
                "                 ### Boo\n" +
                "\n" +
                "                 Hi this is Lance\n" +
                "\n" +
                "                 ## Baz\n" +
                "\n" +
                "                 Hi this is Molly" +
                "";

        List<Pair<String, String>> headersToSplitOn = ListUtil.of(
                Pair.of("#", "Header 1"),
                Pair.of("##", "Header 2"),
                Pair.of("###", "Header 3"));

        MarkdownHeaderTextSplitter markdownSplitter = new MarkdownHeaderTextSplitter(headersToSplitOn);
        List<Document> output = markdownSplitter.splitText(markdownDocument);

        List<Document> expectedOutput = ListUtil.of(
                new Document(
                        "Hi this is Jim  \nHi this is Joe",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Bar").map()),
                new Document(
                        "Hi this is Lance",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Bar")
                                .put("Header 3", "Boo").map()),
                new Document(
                        "Hi this is Molly",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Baz").map()));
        assertEquals(expectedOutput, output);
    }

    /**
     * Test markdown splitter by header: Case 3.
     */
    @Test
    void testMdHeaderTextSplitter3() {
        String markdownDocument = "" +
                "# Foo\n" +
                "\n" +
                "                    ## Bar\n" +
                "\n" +
                "                Hi this is Jim\n" +
                "\n" +
                "                Hi this is Joe\n" +
                "\n" +
                "                 ### Boo\n" +
                "\n" +
                "                 Hi this is Lance\n" +
                "\n" +
                "                 #### Bim\n" +
                "\n" +
                "                 Hi this is John\n" +
                "\n" +
                "                 ## Baz\n" +
                "\n" +
                "                 Hi this is Molly" +
                "";

        List<Pair<String, String>> headersToSplitOn = ListUtil.of(
                Pair.of("#", "Header 1"),
                Pair.of("##", "Header 2"),
                Pair.of("###", "Header 3"),
                Pair.of("####", "Header 4"));

        MarkdownHeaderTextSplitter markdownSplitter = new MarkdownHeaderTextSplitter(headersToSplitOn);
        List<Document> output = markdownSplitter.splitText(markdownDocument);

        List<Document> expectedOutput = ListUtil.of(
                new Document(
                        "Hi this is Jim  \nHi this is Joe",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Bar").map()),
                new Document(
                        "Hi this is Lance",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Bar")
                                .put( "Header 3", "Boo").map()),
                new Document(
                        "Hi this is John",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Bar")
                                .put("Header 3", "Boo")
                                .put("Header 4", "Bim").map()),
                new Document(
                        "Hi this is Molly",
                        MapBuilder.create(new HashMap<String, Object>())
                                .put("Header 1", "Foo")
                                .put("Header 2", "Baz").map()));
        assertEquals(expectedOutput, output);
    }
}