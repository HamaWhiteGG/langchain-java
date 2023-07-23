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

package com.hw.langchain.agents.toolkits.flink.sql.toolkit;

import com.hw.langchain.agents.toolkits.base.BaseToolkit;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.tools.base.BaseTool;
import com.hw.langchain.tools.flink.sql.tool.InfoFlinkSqlTool;
import com.hw.langchain.tools.flink.sql.tool.ListFlinkSqlTool;
import com.hw.langchain.tools.flink.sql.tool.QueryCheckerTool;
import com.hw.langchain.tools.flink.sql.tool.QueryFlinkSqlTool;
import com.hw.langchain.utilities.flink.sql.FlinkSql;

import java.util.List;

/**
 * Toolkit for interacting with Flink SQL.
 *
 * @author HamaWhite
 */
public class FlinkSqlToolkit implements BaseToolkit {

    private final FlinkSql db;

    private final BaseLanguageModel llm;

    public FlinkSqlToolkit(FlinkSql db, BaseLanguageModel llm) {
        this.db = db;
        this.llm = llm;
    }

    @Override
    public List<BaseTool> getTools() {
        return List.of(
                new QueryFlinkSqlTool(db),
                new InfoFlinkSqlTool(db),
                new ListFlinkSqlTool(db),
                new QueryCheckerTool(db, llm));
    }
}
