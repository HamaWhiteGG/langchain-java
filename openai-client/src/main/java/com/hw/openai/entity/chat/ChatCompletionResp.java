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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.openai.entity.completions.Usage;

import lombok.Data;

import java.util.List;

/**
 * CompletionResp
 * @author HamaWhite
 */
@Data
public class ChatCompletionResp {

    private String id;

    private String object;

    private Long created;

    private String model;

    /**
     * This fingerprint represents the backend configuration that the model runs with. Can be used in conjunction with
     * the seed request parameter to understand when backend changes have been made that might impact determinism.
     */
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

    private List<ChatChoice> choices;

    private Usage usage;
}
