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

import java.util.List;
import java.util.Map;

/**
 * Base interface for memory in chains.
 *
 * @author HamaWhite
 */
public interface BaseMemory {

    /**
     * Input keys this memory class will load dynamically
     */
    List<String> memoryVariables();

    /**
     * Return key-value pairs given the text input to the chain.
     * If None, return all memories
     */
    Map<String, Object> loadMemoryVariables(Map<String, Object> inputs);

    /**
     * Save the context of this model run to memory.
     */
    void saveContext(Map<String, Object> inputs, Map<String, String> outputs);

    /**
     * Clear memory contents.
     */
    void clear();
}
