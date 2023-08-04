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

package com.hw.langchain.schema;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lingjue@ubuntu
 * @since 8/1/23 7:01 PM
 */
@Data
public class AsyncLLMResult {

    /**
     * List of the things generated. This is List<List<Generation>> because each input could have multiple generations.
     */
    private List<? extends Generation> generations;

    /**
     * For arbitrary LLM provider specific output.
     */
    private Map<String, Object> llmOutput;

    public AsyncLLMResult(List<? extends Generation> generations, Map<String, Object> llmOutput) {
        this.generations = generations;
        this.llmOutput = llmOutput;
    }

}
