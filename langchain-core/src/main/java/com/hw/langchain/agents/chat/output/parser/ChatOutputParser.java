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

package com.hw.langchain.agents.chat.output.parser;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hw.langchain.agents.agent.AgentOutputParser;
import com.hw.langchain.schema.AgentAction;
import com.hw.langchain.schema.AgentFinish;
import com.hw.langchain.schema.AgentResult;
import com.hw.langchain.schema.OutputParserException;

import java.lang.reflect.Type;
import java.util.Map;

import static com.hw.langchain.agents.chat.prompt.Prompt.FORMAT_INSTRUCTIONS;

/**
 * @author HamaWhite
 */
public class ChatOutputParser extends AgentOutputParser {

    private static final String FINAL_ANSWER_ACTION = "Final Answer:";

    @Override
    public AgentResult parse(String text) {
        boolean includesAnswer = text.contains(FINAL_ANSWER_ACTION);
        try {
            String action = text.split("```")[1];
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> response = new Gson().fromJson(StrUtil.strip(action, " "), mapType);

            boolean includesAction = response.containsKey("action") && response.containsKey("action_input");
            if (includesAnswer && includesAction) {
                throw new OutputParserException(
                        "Parsing LLM output produced a final answer and a parse-able action: " + text);
            }
            return new AgentAction(response.get("action").toString(), response.get("action_input"), text);
        } catch (Exception e) {
            if (!includesAnswer) {
                throw new OutputParserException("Could not parse LLM output: " + text);
            }
            String[] splitText = text.split(FINAL_ANSWER_ACTION);
            String output = StrUtil.strip(splitText[splitText.length - 1], " ");
            return new AgentFinish(MapUtil.ofEntries(MapUtil.entry("output", output)), text);
        }
    }

    @Override
    public String getFormatInstructions() {
        return FORMAT_INSTRUCTIONS;
    }
}
