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

package com.hw.langchain.agents.chat.prompt;

/**
 * @author HamaWhite
 */
public class Prompt {

    public static String SYSTEM_MESSAGE_PREFIX = """
            Answer the following questions as best you can. You have access to the following tools:""";

    public static String FORMAT_INSTRUCTIONS =
            """
                    The way you use the tools is by specifying a json blob.
                    Specifically, this json should have a `action` key (with the name of the tool to use) and a `action_input` key (with the input to the tool going here).

                    The only values that should be in the "action" field are: {tool_names}

                    The $JSON_BLOB should only contain a SINGLE action, do NOT return a list of multiple actions. Here is an example of a valid $JSON_BLOB:

                    ```
                    {{{{
                      "action": $TOOL_NAME,
                      "action_input": $INPUT
                    }}}}
                    ```

                    ALWAYS use the following format:

                    Question: the input question you must answer
                    Thought: you should always think about what to do
                    Action:
                    ```
                    $JSON_BLOB
                    ```
                    Observation: the result of the action
                    ... (this Thought/Action/Observation can repeat N times)
                    Thought: I now know the final answer
                    Final Answer: the final answer to the original input question""";

    public static String SYSTEM_MESSAGE_SUFFIX = """
            Begin! Reminder to always use the exact characters `Final Answer` when responding.""";

    public static String HUMAN_MESSAGE = "{input}\n\n{agent_scratchpad}";
}
