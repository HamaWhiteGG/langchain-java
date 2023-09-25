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

package com.hw.openai;

import cn.hutool.core.collection.ListUtil;
import com.hw.openai.entity.completions.Completion;

import java.util.List;

/**
 * @author HamaWhite
 */
public class QuickStart {

    public static void main(String[] args) {
        OpenAiClient client = OpenAiClient.builder()
                .build()
                .init();

        Completion completion = Completion.builder()
                .model("text-davinci-003")
                .prompt(ListUtil.of("Say this is a test"))
                .maxTokens(700)
                .temperature(0)
                .build();

        System.out.println(client.completion(completion));

        client.close();
    }
}
