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
import com.google.common.collect.Maps;
import com.hw.langchain.schema.Document;

import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of splitting markdown files based on specified headers.
 *
 * @author HamaWhite
 */
public class MarkdownHeaderTextSplitter {

    /**
     * Headers we want to track
     */
    private final List<Pair<String, String>> headersToSplitOn;

    /**
     * Return each line w/ associated headers
     */
    private final boolean returnEachLine;

    public MarkdownHeaderTextSplitter(List<Pair<String, String>> headersToSplitOn) {
        this(headersToSplitOn, false);
    }

    public MarkdownHeaderTextSplitter(List<Pair<String, String>> headersToSplitOn, boolean returnEachLine) {
        // Output line-by-line or aggregated into chunks w/ common headers
        this.returnEachLine = returnEachLine;

        // Given the headers we want to split on, (e.g., "#, ##, etc") order by length
        this.headersToSplitOn = headersToSplitOn.stream()
                .sorted(Comparator.<Pair<String, String>>comparingInt(e -> e.getKey().length()).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Combine lines with common metadata into chunks.
     *
     * @param lines Line of text / associated header metadata
     * @return List of Document chunks
     */
    public List<Document> aggregateLinesToChunks(List<LineType> lines) {
        List<LineType> aggregatedChunks = new ArrayList<>();

        for (LineType line : lines) {
            if (!aggregatedChunks.isEmpty()
                    && (aggregatedChunks.get(aggregatedChunks.size() - 1).getMetadata().equals(line.getMetadata()))) {
                // If the last line in the aggregated list has the same metadata as the current line,
                // append the current content to the last line's content
                LineType lastChunk = aggregatedChunks.get(aggregatedChunks.size() - 1);
                lastChunk.setContent(lastChunk.getContent() + "  \n" + line.getContent());
            } else {
                // Otherwise, append the current line to the aggregated list
                aggregatedChunks.add(line);
            }
        }
        return aggregatedChunks.stream()
                .map(chunk -> new Document(chunk.getContent(), chunk.getMetadata()))
                .collect(Collectors.toList());
    }

    /**
     * Split markdown file.
     *
     * @param text Markdown file
     * @return List of Document chunks
     */
    public List<Document> splitText(String text) {
        List<LineType> linesWithMetadata = new ArrayList<>();
        // Content and metadata of the chunk currently being processed
        List<String> currentContent = new ArrayList<>();
        Map<String, Object> currentMetadata = Maps.newHashMap();
        // Keep track of the nested header structure
        List<HeaderType> headerStack = new ArrayList<>();
        Map<String, String> initialMetadata = Maps.newHashMap();

        // Split the input text by newline character ("\n").
        String[] lines = text.split("\n");
        for (String line : lines) {
            String strippedLine = StrUtil.strip(line, " ");
            // Check each line against each of the header types (e.g., #, ##)
            boolean foundHeader = processLine(strippedLine, linesWithMetadata, currentContent, currentMetadata,
                    headerStack, initialMetadata);

            if (!foundHeader && !strippedLine.isEmpty()) {
                currentContent.add(strippedLine);
            } else if (!currentContent.isEmpty()) {
                linesWithMetadata.add(new LineType(String.join("\n", currentContent), new HashMap<>(currentMetadata)));
                currentContent.clear();
            }
            currentMetadata = new HashMap<>(initialMetadata);
        }

        return processOutput(linesWithMetadata, currentContent, currentMetadata);
    }

    private boolean processLine(String strippedLine, List<LineType> linesWithMetadata, List<String> currentContent,
            Map<String, Object> currentMetadata, List<HeaderType> headerStack, Map<String, String> initialMetadata) {
        for (var pair : headersToSplitOn) {
            String sep = pair.getLeft();
            String name = pair.getValue();
            if (isHeaderToSplitOn(strippedLine, sep)) {
                // Ensure we are tracking the header as metadata
                if (name != null) {
                    // Get the current header level
                    int currentHeaderLevel = StringUtils.countMatches(sep, "#");
                    // Pop out headers of lower or same level from the stack
                    while (!headerStack.isEmpty()
                            && headerStack.get(headerStack.size() - 1).getLevel() >= currentHeaderLevel) {
                        // We have encountered a new header at the same or higher level
                        HeaderType poppedHeader = headerStack.remove(headerStack.size() - 1);
                        // Clear the metadata for the popped header in initialMetadata
                        initialMetadata.remove(poppedHeader.getName());
                    }
                    // Push the current header to the stack
                    HeaderType header =
                            new HeaderType(currentHeaderLevel, name, StrUtil.strip(strippedLine.substring(sep.length()), " "));
                    headerStack.add(header);
                    // Update initialMetadata with the current header
                    initialMetadata.put(name, header.getData());
                }
                // Add the previous line to the linesWithMetadata only if currentContent is not empty
                if (!currentContent.isEmpty()) {
                    linesWithMetadata
                            .add(new LineType(String.join("\n", currentContent), new HashMap<>(currentMetadata)));
                    currentContent.clear();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Check if line starts with a header that we intend to split on.
     * Header with no text OR header is followed by space Both are valid conditions that sep is being used a header.
     */
    private boolean isHeaderToSplitOn(String strippedLine, String sep) {
        return strippedLine.startsWith(sep) &&
                (strippedLine.length() == sep.length() || strippedLine.charAt(sep.length()) == ' ');
    }

    private List<Document> processOutput(List<LineType> linesWithMetadata, List<String> currentContent,
            Map<String, Object> currentMetadata) {
        if (!currentContent.isEmpty()) {
            linesWithMetadata.add(new LineType(String.join("\n", currentContent), currentMetadata));
        }
        // linesWithMetadata has each line with associated header metadata aggregate these into chunks based on common
        // metadata
        if (!returnEachLine) {
            return aggregateLinesToChunks(linesWithMetadata);
        } else {
            return linesWithMetadata.stream()
                    .map(chunk -> new Document(chunk.getContent(), chunk.getMetadata()))
                    .collect(Collectors.toList());
        }
    }
}
