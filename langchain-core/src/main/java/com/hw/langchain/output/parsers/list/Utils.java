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

package com.hw.langchain.output.parsers.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
public class Utils {

    /**
     * "stop" is a special key that can be passed as input but is not used to format the prompt.
     */
    public static String getPromptInputKey(Map<String, Object> inputs, List<String> memoryVariables) {
        List<String> promptInputKeys = new ArrayList<>(inputs.keySet());
        promptInputKeys.removeAll(memoryVariables);
        promptInputKeys.remove("stop");

        if (promptInputKeys.size() != 1) {
            throw new IllegalArgumentException("One input key expected, got " + promptInputKeys.size());
        }

        return promptInputKeys.get(0);
    }
}
