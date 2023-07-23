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

import com.hw.langchain.utilities.flink.sql.FlinkSql;

import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.Set;

/**
 * Tool for getting metadata about a Flink SQL.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public class InfoFlinkSqlTool extends BaseFlinkSqlTool {

    private static final String NAME = "schema_sql_db";
    private static final String DESCRIPTION =
            """
                    Input to this tool is a comma-separated list of tables, output is the schema and sample rows for those tables.
                    Be sure that the tables actually exist by calling list_tables_sql_db first!

                    Example Input: "table1, table2, table3"
                    """;

    public InfoFlinkSqlTool(FlinkSql db) {
        super(db, NAME, DESCRIPTION);
    }

    /**
     * Get the schema for tables in a comma-separated list.
     */
    @Override
    public Object innerRun(String query, Map<String, Object> kwargs) {
        return db.getTableInfo(Set.of(query.split(", ")));
    }
}
