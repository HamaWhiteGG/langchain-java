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

package com.hw.langchain.document.loaders.directory;

import com.hw.langchain.document.loaders.base.BaseLoader;
import com.hw.langchain.document.loaders.text.TextLoader;
import com.hw.langchain.exception.LangChainException;
import com.hw.langchain.schema.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hw.langchain.utils.ThreadPoolUtil.createThreadPool;

/**
 * Load documents from a directory.
 *
 * @author HamaWhite
 */
@Builder
public class DirectoryLoader extends BaseLoader {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryLoader.class);

    /**
     * Path to directory.
     */
    private Path path;

    /**
     * Whether to load hidden files.
     */
    private boolean loadHidden;

    /**
     * Support UnstructuredFileLoader, TextLoader and BSHTMLLoader
     */
    @Builder.Default
    private Class<? extends BaseLoader> loaderCls = TextLoader.class;

    /**
     * Whether to recursively search for files.
     */
    private boolean recursive;

    /**
     * Whether to use multithreading.
     */
    private boolean useMultithreading;

    /**
     * The maximum number of threads to use.
     */
    @Builder.Default
    private int maxConcurrency = 4;

    /**
     * check if a path is visible.
     */
    private boolean isVisible(Path p) {
        String separator = FileSystems.getDefault().getSeparator();
        String[] parts = p.toAbsolutePath().toString().split(separator);
        for (String part : parts) {
            if (part.startsWith(".")) {
                return false;
            }
        }
        return true;
    }

    private void loadFile(Path item, Path path, List<Document> docs) {
        if (isVisible(path.relativize(item)) || loadHidden) {
            try {
                LOG.info("Loading file: {}", item);
                // Create an instance of the loader class and load sub docs
                List<Document> subDocs = loaderCls.getConstructor(String.class)
                        .newInstance(item.toString())
                        .load();
                docs.addAll(subDocs);
            } catch (Exception e) {
                throw new LangChainException(errorMessage(path.toString()), e);
            }
        }
    }

    @Override
    public List<Document> load() {
        checkArgument(path.toFile().exists(), "Directory not found: '%s'", path.toString());
        checkArgument(path.toFile().isDirectory(), "Expected directory, got file: '%s'", path.toString());
        try {
            List<Path> items;
            try (Stream<Path> stream = recursive ? Files.walk(path) : Files.list(path)) {
                items = stream.filter(p -> !p.toFile().isDirectory()).collect(Collectors.toList());
            }
            List<Document> docs = new ArrayList<>();
            if (useMultithreading) {
                ExecutorService executor = createThreadPool(maxConcurrency);
                List<CompletableFuture<Void>> futures = items.stream()
                        .map(item -> CompletableFuture.runAsync(() -> loadFile(item, path, docs), executor))
                        .collect(Collectors.toList());
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                executor.shutdown();
            } else {
                items.forEach(item -> loadFile(item, path, docs));
            }
            return docs;
        } catch (IOException e) {
            throw new LangChainException(errorMessage(path.toString()), e);
        }
    }
}
