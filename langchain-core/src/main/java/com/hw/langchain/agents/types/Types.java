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

package com.hw.langchain.agents.types;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.agents.agent.BaseSingleActionAgent;
import com.hw.langchain.agents.agent.types.AgentType;
import com.hw.langchain.agents.chat.base.ChatAgent;
import com.hw.langchain.agents.mrkl.base.ZeroShotAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HamaWhite
 */
public class Types {

    public static final Map<AgentType, Class<? extends BaseSingleActionAgent>> AGENT_TO_CLASS =
            MapBuilder.create(new HashMap<AgentType, Class<? extends BaseSingleActionAgent>>())
                    .put(AgentType.ZERO_SHOT_REACT_DESCRIPTION, ZeroShotAgent.class)
                    .put(AgentType.CHAT_ZERO_SHOT_REACT_DESCRIPTION, ChatAgent.class).map();
}
