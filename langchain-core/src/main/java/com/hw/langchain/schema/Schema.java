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

import lombok.var;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HamaWhite
 */
public class Schema {

    public static String getBufferString(List<BaseMessage> messages) {
        return getBufferString(messages, "Human", "AI");
    }

    /**
     * Get buffer string of messages.
     */
    public static String getBufferString(List<BaseMessage> messages, String humanPrefix, String aiPrefix) {
        var stringMessages = new ArrayList<String>();
        for (var m : messages) {
            String role;
            if (m instanceof HumanMessage) {
                role = humanPrefix;
            } else if (m instanceof AIMessage) {
                role = aiPrefix;
            } else if (m instanceof SystemMessage) {
                role = "System";
            } else if (m instanceof ChatMessage) {
                role = ((ChatMessage)m).getRole();
            } else {
                throw new IllegalArgumentException("Got unsupported message type: " + m);
            }
            stringMessages.add(role + ": " + m.getContent());
        }
        return String.join("\n", stringMessages);
    }
}
