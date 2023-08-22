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

package com.hw.langchain.llms;

import java.util.List;

/**
 * @author HamaWhite
 */
public class Utils {

    private Utils() {
        // private constructor to hide the implicit public one
        throw new IllegalStateException("Utility class");
    }

    /**
     * Cuts off the text as soon as any stop words occur.
     *
     * @param text The input text to be processed.
     * @param stop List of stop words to identify cut-off points.
     * @return The processed text after enforcing stop tokens.
     */
    public static String enforceStopTokens(String text, List<String> stop) {
        String[] parts = text.split(String.join("|", stop));
        return parts[0];
    }
}
