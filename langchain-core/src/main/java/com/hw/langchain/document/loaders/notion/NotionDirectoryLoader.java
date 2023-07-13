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

package com.hw.langchain.document.loaders.notion;

import com.hw.langchain.document.loaders.base.BaseLoader;
import com.hw.langchain.exception.LangChainException;
import com.hw.langchain.schema.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Loader that loads Notion directory dump.
 *
 * @author HamaWhite
 */
public class NotionDirectoryLoader extends BaseLoader {

    private final String filePath;

    public NotionDirectoryLoader(String path) {
        this.filePath = path;
    }

    @Override
    public List<Document> load() {
        try (Stream<Path> pathStream = Files.walk(Path.of(filePath))) {
            return pathStream
                    .filter(p -> p.toString().endsWith(".md"))
                    .flatMap(this::processFile)
                    .toList();
        } catch (IOException e) {
            throw new LangChainException(errorMessage(filePath), e);
        }
    }

    private Stream<Document> processFile(Path path) {
        try {
            String text = Files.readString(path);
            Map<String, Object> metadata = Map.of("source", path.toString());
            return Stream.of(new Document(text, metadata));
        } catch (IOException e) {
            throw new LangChainException(errorMessage(path.toString()), e);
        }
    }
}
