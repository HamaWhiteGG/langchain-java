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


import com.hw.langchain.utils.ResourceBundleUtils;

/**
 * @author HamaWhite
 */
public class Prompt {

    public static String SYSTEM_MESSAGE_PREFIX = ResourceBundleUtils.getString("prompt.chat.system.prefix");

    public static String FORMAT_INSTRUCTIONS = ResourceBundleUtils.getString("prompt.chat.format.instructions");

    public static String SYSTEM_MESSAGE_SUFFIX = ResourceBundleUtils.getString("prompt.chat.system.suffix");

    public static String HUMAN_MESSAGE = "{input}\n\n{agent_scratchpad}";
}
