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
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.hw.langchain.text.splitter.TextSplitterUtils.splitTextWithRegex;

/**
 * Implementation of splitting text that looks at characters.
 * Recursively tries to split by different characters to find one that works.
 *
 * @author HamaWhite
 */
@SuperBuilder
public class RecursiveCharacterTextSplitter extends TextSplitter {

    @Builder.Default
    protected List<String> separators = ListUtil.of("\n\n", "\n", " ", "");

    /**
     * Split incoming text and return chunks.
     */
    public List<String> innerSplitText(String text, List<String> separators) {
        // Get appropriate separator to use
        String separator = separators.get(separators.size() - 1);
        List<String> newSeparators = new ArrayList<>();
        boolean foundSeparator = false;
        for (int i = 0; i < separators.size() && !foundSeparator; i++) {
            String temp = separators.get(i);
            if (temp.isEmpty()) {
                separator = temp;
                foundSeparator = true;
            } else if (Pattern.compile(temp).matcher(text).find()) {
                separator = temp;
                newSeparators = separators.subList(i + 1, separators.size());
                foundSeparator = true;
            }
        }
        return recursivelySplitLongerTexts(text, separator, newSeparators);
    }

    private List<String> recursivelySplitLongerTexts(String text, String separator, List<String> newSeparators) {
        List<String> finalChunks = new ArrayList<>();
        List<String> splits = splitTextWithRegex(text, separator, this.keepSeparator);

        // Now go merging things, recursively splitting longer texts.
        List<String> tempGoodSplits = new ArrayList<>();
        String tempSeparator = this.keepSeparator ? "" : separator;
        for (String s : splits) {
            if (lengthFunction.apply(s) < this.chunkSize) {
                tempGoodSplits.add(s);
            } else {
                if (!tempGoodSplits.isEmpty()) {
                    List<String> mergedText = mergeSplits(tempGoodSplits, tempSeparator);
                    finalChunks.addAll(mergedText);
                    tempGoodSplits.clear();
                }
                if (newSeparators.isEmpty()) {
                    finalChunks.add(s);
                } else {
                    List<String> otherInfo = innerSplitText(s, newSeparators);
                    finalChunks.addAll(otherInfo);
                }
            }
        }
        if (!tempGoodSplits.isEmpty()) {
            List<String> mergedText = mergeSplits(tempGoodSplits, tempSeparator);
            finalChunks.addAll(mergedText);
        }
        return finalChunks;
    }

    @Override
    public List<String> splitText(String text) {
        return innerSplitText(text, this.separators);
    }
}
