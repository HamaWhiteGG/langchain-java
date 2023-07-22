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

package com.hw.langchain.tools.spark.sql.tool;

import com.hw.langchain.utilities.spark.sql.SparkSql;

import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * Tool for getting tables names.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public class ListSparkSqlTool extends BaseSparkSqlTool {

    private static final String NAME = "list_tables_sql_db";
    private static final String DESCRIPTION =
            "Input is an empty string, output is a comma separated list of tables in the Spark SQL.";

    public ListSparkSqlTool(SparkSql db) {
        super(db, NAME, DESCRIPTION);
    }

    /**
     * Get the schema for a specific table.
     */
    @Override
    public Object innerRun(String query, Map<String, Object> kwargs) {
        return String.join(", ", db.getUsableTableNames());
    }
}
