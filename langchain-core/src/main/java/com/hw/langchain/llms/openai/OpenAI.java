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
 * @description: OpenAI
 * @author: HamaWhite
 */
@SuperBuilder
public class OpenAI extends BaseOpenAI {

    /**
     * Validate that api key exists in environment.
     */
    public OpenAI init() {
        checkArgument(!(streaming && n > 1), "Cannot stream results when n > 1.");
        checkArgument(!(streaming && bestOf > 1), "Cannot stream results when bestOf > 1.");

        openaiApiKey = Utils.getOrEnvOrDefault(openaiApiKey, "OPENAI_API_KEY");
        openaiApiBase = Utils.getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE", "");
        openaiOrganization = Utils.getOrEnvOrDefault(openaiOrganization, "OPENAI_ORGANIZATION", "");
        openaiProxy = Utils.getOrEnvOrDefault(openaiProxy, "OPENAI_PROXY");

        this.client = OpenAiClient.builder()
                .openaiApiBase(openaiApiBase)
                .openaiApiKey(openaiApiKey)
                .openaiOrganization(openaiOrganization)
                .openaiProxy(openaiProxy)
                .build()
                .init();
        return this;
    }

}
