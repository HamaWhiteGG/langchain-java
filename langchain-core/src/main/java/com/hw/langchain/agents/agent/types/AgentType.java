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

package com.hw.langchain.agents.agent.types;

import java.util.Optional;

/**
 * @author HamaWhite
 */

public enum AgentType {

    ZERO_SHOT_REACT_DESCRIPTION("zero-shot-react-description"),
    REACT_DOCSTORE("react-docstore"),
    SELF_ASK_WITH_SEARCH("self-ask-with-search"),
    CONVERSATIONAL_REACT_DESCRIPTION("conversational-react-description"),
    CHAT_ZERO_SHOT_REACT_DESCRIPTION("chat-zero-shot-react-description"),
    CHAT_CONVERSATIONAL_REACT_DESCRIPTION("chat-conversational-react-description"),
    STRUCTURED_CHAT_ZERO_SHOT_REACT_DESCRIPTION("structured-chat-zero-shot-react-description");

    private final String value;

    private AgentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<AgentType> fromValue(String value) {
        for (AgentType agentType : AgentType.values()) {
            if (agentType.getValue().equals(value)) {
                return Optional.of(agentType);
            }
        }
        return Optional.empty();
    }
}
