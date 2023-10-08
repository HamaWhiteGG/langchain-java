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

package com.hw.openai.entity.chat;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Message
 *
 * @author HamaWhite
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message implements Serializable {

    /**
     * The role of the author of this message. One of system, user, or assistant.
     */
    @NotNull
    private Role role;

    /**
     * The contents of the message.
     * content should always exist in the call, even if it is null
     */
    @JsonInclude()
    private String content;

    /**
     * The name of the author of this message. May contain a-z, A-Z, 0-9, and underscores,
     * with a maximum length of 64 characters.
     */
    private String name;

    public Message(Role role, String content) {
        this.role = role;
        this.content = content;
    }

    public static Message of(String role, String content) {
        return new Message(Role.fromValue(role), content);
    }

    public static Message of(String content) {
        return new Message(Role.USER, content);
    }

    public static Message ofSystem(String content) {
        return new Message(Role.SYSTEM, content);
    }

    public static Message ofAssistant(String content) {
        return new Message(Role.ASSISTANT, content);
    }

    public static Message ofFunction(String content, String name) {
        return new Message(Role.FUNCTION, content, name);
    }
}
