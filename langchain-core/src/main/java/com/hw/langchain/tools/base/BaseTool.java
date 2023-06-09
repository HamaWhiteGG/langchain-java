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

package com.hw.langchain.tools.base;

/**
 * Interface LangChain tools must implement.
 *
 * @author HamaWhite
 */
public abstract class BaseTool {

    /**
     * The unique name of the tool that clearly communicates its purpose.
     */
    protected String name;

    /**
     * Used to tell the model how/when/why to use the tool.
     * You can provide few-shot examples as a part of the description.
     */
    protected String description;

    /**
     * Whether to return the tool's output directly. Setting this to true means
     * that after the tool is called, the AgentExecutor will stop looping.
     */
    protected boolean returnDirect = false;

    public BaseTool(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
