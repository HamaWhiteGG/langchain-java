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

package com.hw.openai.exception;

import com.hw.openai.common.OpenAiError;

/**
 * @author HamaWhite
 */
public class OpenAiException extends RuntimeException {

    /**
     * HTTP status code
     */
    public final int statusCode;

    /**
     * OpenAI error code, for example "invalid_api_key"
     */
    public final String code;

    public final String param;

    /**
     * OpenAI error type, for example "invalid_request_error"
     * <a href="https://platform.openai.com/docs/guides/error-codes/api-errors">Error codes</a>
     */
    public final String type;

    public OpenAiException(OpenAiError openAiError, Exception parent, int statusCode) {
        super(openAiError.getError().getMessage(), parent);
        this.statusCode = statusCode;
        this.code = openAiError.getError().getCode();
        this.param = openAiError.getError().getParam();
        this.type = openAiError.getError().getType();
    }
}
