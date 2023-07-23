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

package com.hw.langchain.tools.flink.sql.tool;

import com.hw.langchain.tools.base.BaseTool;
import com.hw.langchain.utilities.flink.sql.FlinkSql;

import lombok.EqualsAndHashCode;

/**
 * Base tool for interacting with Flink SQL.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public abstract class BaseFlinkSqlTool extends BaseTool {

    protected final FlinkSql db;

    protected BaseFlinkSqlTool(FlinkSql db, String name, String description) {
        super(name, description);
        this.db = db;
    }
}
