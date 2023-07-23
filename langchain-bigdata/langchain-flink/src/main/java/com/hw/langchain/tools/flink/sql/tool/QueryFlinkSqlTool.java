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

import java.util.Map;

/**
 * Tool for querying a Flink SQL.
 *
 * @author HamaWhite
 */
public class QueryFlinkSqlTool extends BaseFlinkSqlTool {

    private static final String NAME = "query_sql_db";
    private static final String DESCRIPTION = """
            Input to this tool is a detailed and correct SQL query, output is a result from the Flink SQL.
            If the query is not correct, an error message will be returned.
            If an error is returned, rewrite the query, check the query, and try again.
            """;

    public QueryFlinkSqlTool(FlinkSql db) {
        super(db, NAME, DESCRIPTION);
    }

    /**
     * Execute the query, return the results or an error message.
     */
    @Override
    public Object innerRun(String query, Map<String, Object> kwargs) {
        return db.run(query);
    }
}
