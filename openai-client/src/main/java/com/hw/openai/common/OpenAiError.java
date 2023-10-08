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

package com.hw.openai.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the error body when an OpenAI request fails
 *
 * @author HamaWhite
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiError {

    private OpenAiErrorDetails error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenAiErrorDetails {

        /**
         * Human-readable error message
         */
        String message;

        /**
         * OpenAI error type, for example "invalid_request_error"
         * <a href="https://platform.openai.com/docs/guides/error-codes/api-errors">Error codes</a>
         */
        String type;

        String param;

        /**
         * OpenAI error code, for example "invalid_api_key"
         */
        String code;
    }
}
