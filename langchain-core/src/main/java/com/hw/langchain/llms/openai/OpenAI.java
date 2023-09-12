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

package com.hw.langchain.llms.openai;

import com.hw.langchain.utils.Utils;
import com.hw.openai.OpenAiClient;

import lombok.experimental.SuperBuilder;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * OpenAI
 * @author HamaWhite
 */
@SuperBuilder
public class OpenAI extends BaseOpenAI {

    /**
     * Validate that api key exists in environment.
     */
    public OpenAI init() {
        checkArgument(!(stream && n > 1), "Cannot stream results when n > 1.");
        checkArgument(!(stream && bestOf > 1), "Cannot stream results when bestOf > 1.");

        openaiApiKey = Utils.getOrEnvOrDefault(openaiApiKey, "OPENAI_API_KEY","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");
        openaiApiBase = Utils.getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE", "https://api.openai-proxy.com/v1/");
        openaiOrganization = Utils.getOrEnvOrDefault(openaiOrganization, "OPENAI_ORGANIZATION", "");
        openaiProxy = Utils.getOrEnvOrDefault(openaiProxy, "OPENAI_PROXY", "");
        openaiApiType = Utils.getOrEnvOrDefault(openaiApiType, "OPENAI_API_TYPE", "");
        openaiApiVersion = Utils.getOrEnvOrDefault(openaiApiVersion, "OPENAI_API_VERSION", "");

        this.client = OpenAiClient.builder()
                .openaiApiBase(openaiApiBase)
                .openaiApiKey(openaiApiKey)
                .openaiApiVersion(openaiApiVersion)
                .openaiApiType(openaiApiType)
                .openaiOrganization(openaiOrganization)
                .openaiProxy(openaiProxy)
                .proxyUsername(proxyUsername)
                .proxyPassword(proxyPassword)
                .requestTimeout(requestTimeout)
                .interceptorList(interceptorList)
                .build()
                .init();
        return this;
    }

}
