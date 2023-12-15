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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ChatChoice
 * @author HamaWhite
 */
@Data
public class ChatChoice {

    private Integer index;

    @JsonAlias("delta")
    private ChatMessage message;

    /**
     * The reason the model stopped generating tokens. This will be stopped if the model hit a natural stop point or a
     * provided stop sequence, length if the maximum number of tokens specified in the request was reached,
     * content_filter if content was omitted due to a flag from our content filters, tool_calls if the model called a
     * tool, or function_call (deprecated) if the model called a function.
     */
    @JsonProperty("finish_reason")
    private String finishReason;
}
