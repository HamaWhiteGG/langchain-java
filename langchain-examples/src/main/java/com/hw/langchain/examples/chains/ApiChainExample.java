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

package com.hw.langchain.examples.chains;

import com.hw.langchain.chains.api.base.ApiChain;
import com.hw.langchain.examples.runner.RunnableExample;
import com.hw.langchain.llms.openai.OpenAI;

import static com.hw.langchain.chains.api.meteo.OpenMeteoDocs.OPEN_METEO_DOCS;
import static com.hw.langchain.examples.utils.PrintUtils.println;

/**
 * <a href="https://python.langchain.com/docs/modules/chains/popular/api">API chains</a>
 *
 * @author HamaWhite
 */
@RunnableExample
public class ApiChainExample {

    public static void main(String[] args) {
        var llm = OpenAI.builder().temperature(0).build().init();

        var chain = ApiChain.fromLlmAndApiDocs(llm, OPEN_METEO_DOCS);
        var result = chain.run("What is the weather like right now in Hangzhou, China in degrees Fahrenheit?");

        // The current temperature in Hangzhou, China is 79.8°F with a windSpeed of 7.4 km/h and a wind direction of
        // 133°.
        println(result);
    }
}
