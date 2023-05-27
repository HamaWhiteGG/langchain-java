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

import com.hw.langchain.llms.openai.entity.request.Completion;
import com.hw.langchain.util.ProxyUtils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @description: OpenaiClientTest
 * @author: HamaWhite
 */
class OpenaiClientTest {

    @Test
    void testCompletion() {
        OpenaiClient openai = OpenaiClient.builder()
                .apiKey("xx-xxxxxx")
                .proxy(ProxyUtils.http("127.0.0.1", 1087))
                .build()
                .init();

        Completion completion = Completion.builder()
                .model("text-davinci-003")
                .prompt("Say this is a test")
                .maxTokens(700)
                .temperature(0)
                .build();

        assertThat(openai.completion(completion)).isEqualTo("This is indeed a test.");
    }
}
