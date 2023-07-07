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

package com.hw.langchain.text.splitter;

import lombok.Data;

/**
 * @author HamaWhite
 */
@Data
public class HeaderType {

    private int level;

    private String name;

    private String data;

    /**
     * Create a new HeaderType.
     *
     * @param level Header level
     * @param name  Header name
     * @param data  Header data
     */
    public HeaderType(int level, String name, String data) {
        this.level = level;
        this.name = name;
        this.data = data;
    }
}
