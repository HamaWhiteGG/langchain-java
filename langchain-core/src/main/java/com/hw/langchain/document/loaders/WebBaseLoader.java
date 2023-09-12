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

package com.hw.langchain.document.loaders;

import com.google.common.collect.Maps;
import com.hw.langchain.document.loaders.base.BaseLoader;
import com.hw.langchain.exception.LangChainException;
import com.hw.langchain.schema.Document;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
public class WebBaseLoader extends BaseLoader {

    private final List<String> webUrls;

    public WebBaseLoader(List<String> webUrls) {
        this.webUrls = webUrls;
    }

    @Override
    public List<Document> load() {
        List<Document> documents = new ArrayList<>(webUrls.size());
        for (String url : webUrls) {
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
                Map<String, Object> metadata = buildMetadata(doc, url);

                documents.add(new Document(doc.wholeText(), metadata));
            } catch (IOException e) {
                throw new LangChainException(errorMessage(url), e);
            }
        }
        return documents;
    }

    private Map<String, Object> buildMetadata(org.jsoup.nodes.Document doc, String url) {
        Map<String, Object> metadata = Maps.newHashMap();
        metadata.put("source", url);

        Element title = doc.select("title").first();
        if (title != null) {
            metadata.put("title", title.text());
        }
        Element description = doc.select("meta[name=description]").first();
        metadata.put("description", description != null ? description.attr("content") : "No description found.");

        Element html = doc.select("html").first();
        metadata.put("language", html != null ? html.attr("lang") : "No language found.");
        return metadata;
    }
}
