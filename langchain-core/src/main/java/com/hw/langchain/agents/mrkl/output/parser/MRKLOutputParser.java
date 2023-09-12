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

package com.hw.langchain.agents.mrkl.output.parser;

import cn.hutool.core.util.StrUtil;
import com.hw.langchain.agents.agent.AgentOutputParser;
import com.hw.langchain.schema.AgentAction;
import com.hw.langchain.schema.AgentFinish;
import com.hw.langchain.schema.AgentResult;
import com.hw.langchain.schema.OutputParserException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hw.langchain.agents.mrkl.prompt.Prompt.FORMAT_INSTRUCTIONS;

/**
 * @author HamaWhite
 */
public class MRKLOutputParser extends AgentOutputParser {

    private static final String FINAL_ANSWER_ACTION = "Final Answer:";

    @Override
    public AgentResult parse(String text) {
        if (text.contains(FINAL_ANSWER_ACTION)) {
            String[] splitText = text.split(FINAL_ANSWER_ACTION);
            String output = StrUtil.strip(splitText[splitText.length - 1], " ");
            Map<String, String> returnValues = new HashMap<>();
            returnValues.put("output", output);
            return new AgentFinish(returnValues, text);
        }

        // \s matches against tab/newline/whitespace
        String regex = "Action\\s*\\d*\\s*:[\\s]*(.*?)[\\s]*Action\\s*\\d*\\s*Input\\s*\\d*\\s*:[\\s]*(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher match = pattern.matcher(text);

        if (!match.find()) {
            if (!Pattern.matches("Action\\s*\\d*\\s*:[\\s]*(.*?)", text)) {
                throw new OutputParserException(
                        "Could not parse LLM output: `" + text + "`",
                        "Invalid Format: Missing 'Action:' after 'Thought:'",
                        text,
                        true);
            } else if (!Pattern.matches("[\\s]*Action\\s*\\d*\\s*Input\\s*\\d*\\s*:[\\s]*(.*)", text)) {
                throw new OutputParserException(
                        "Could not parse LLM output: `" + text + "`",
                        "Invalid Format: Missing 'Action Input:' after 'Action:'",
                        text,
                        true);
            } else {
                throw new OutputParserException("Could not parse LLM output: `" + text + "`");
            }
        }

        String action = StrUtil.strip(match.group(1), " ");
        String actionInput = match.group(2);

        String toolInput = StrUtil.strip(actionInput, " ");
        // ensure if it's a well-formed SQL query we don't remove any trailing " chars
        if (!toolInput.startsWith("SELECT ")) {
            toolInput = toolInput.replaceAll("^\"|\"$", "");
        }
        return new AgentAction(action, toolInput, text);
    }

    @Override
    public String getFormatInstructions() {
        return FORMAT_INSTRUCTIONS;
    }
}
