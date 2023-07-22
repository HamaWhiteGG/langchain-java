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

package com.hw.langchain.utilities.spark.sql;

import com.google.common.collect.Sets;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.StructField;

import lombok.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author HamaWhite
 */
@Builder
public class SparkSql {

    private SparkSession spark;

    private String catalog;

    private String schema;

    @Builder.Default
    private Set<String> ignoreTables = Set.of();

    @Builder.Default
    private Set<String> includeTables = Set.of();

    private Set<String> allTables;

    private Set<String> usableTables;

    @Builder.Default
    private int sampleRowsInTableInfo = 3;

    public SparkSql init() {
        if (spark == null) {
            spark = SparkSession.builder().getOrCreate();
        }
        if (catalog != null) {
            spark.catalog().setCurrentCatalog(catalog);
        }
        if (schema != null) {
            spark.catalog().setCurrentDatabase(schema);
        }
        allTables = getAllTableNames();

        if (!includeTables.isEmpty()) {
            Set<String> missingTables = Sets.difference(includeTables, allTables);
            if (!missingTables.isEmpty()) {
                throw new IllegalArgumentException("includeTables " + missingTables + " not found");
            }
        }
        if (!ignoreTables.isEmpty()) {
            Set<String> missingTables = Sets.difference(ignoreTables, allTables);
            if (!missingTables.isEmpty()) {
                throw new IllegalArgumentException("ignoreTables " + missingTables + " not found in database");
            }
        }
        usableTables = getUsableTableNames();
        return this;
    }

    /**
     * Creating a remote Spark Session via Spark connect.
     *
     * @param databaseUri The database URI, e.g., "sc://localhost:15002".
     * @return A SparkSql instance.
     */
    public static SparkSql fromUri(String databaseUri) {
        SparkSession spark = SparkSession.builder().master(databaseUri).getOrCreate();
        return SparkSql.builder().spark(spark).build().init();
    }

    /**
     * Get names of tables available.
     */
    public Set<String> getUsableTableNames() {
        if (CollectionUtils.isNotEmpty(includeTables)) {
            return includeTables;
        }
        return Sets.difference(allTables, ignoreTables);
    }

    private Set<String> getAllTableNames() {
        List<Row> rows = spark.sql("SHOW TABLES").select("tableName").collectAsList();
        return rows.stream()
                .map(row -> row.getString(row.fieldIndex("tableName")))
                .collect(Collectors.toSet());
    }

    private String getCreateTableStmt(String table) {
        Row row = spark.sql(String.format("SHOW CREATE TABLE %s", table)).collectAsList().get(0);
        String statement = row.getString(row.fieldIndex("createtab_stmt"));

        // Ignore the data source provider and options to reduce the number of tokens.
        int usingClauseIndex = statement.indexOf("USING");
        return statement.substring(0, usingClauseIndex) + ";";
    }

    public String getTableInfo(Set<String> tableNames) {
        Set<String> allTableNames = getUsableTableNames();
        if (tableNames != null) {
            Set<String> missingTables = Sets.difference(tableNames, allTableNames);
            if (!missingTables.isEmpty()) {
                throw new IllegalArgumentException("tableNames " + missingTables + " not found in database");
            }
            allTableNames = tableNames;
        }

        List<String> tables = new ArrayList<>();
        for (String tableName : allTableNames) {
            String tableInfo = getCreateTableStmt(tableName);
            if (sampleRowsInTableInfo > 0) {
                String sampleRows = getSampleSparkRows(tableName);
                tableInfo += "\n\n/*\n" + sampleRows + "\n*/";
            }
            tables.add(tableInfo);
        }
        return String.join("\n\n", tables);
    }

    public String getSampleSparkRows(String table) {
        String query = String.format("SELECT * FROM %s LIMIT %d", table, sampleRowsInTableInfo);
        Dataset<Row> df = spark.sql(query);
        String columnsStr = String.join("\t", Arrays.stream(df.schema().fields()).map(StructField::name).toList());

        String sampleRowsStr;
        try {
            List<List<String>> sampleRows = getDataFrameResults(df);
            sampleRowsStr = String.join("\n", sampleRows.stream()
                    .map(row -> String.join("\t", row))
                    .toList());
        } catch (Exception e) {
            sampleRowsStr = "";
        }
        return String.format("%d rows from %s table:%n%s%n%s", sampleRowsInTableInfo, table, columnsStr, sampleRowsStr);
    }

    private List<String> convertRowToList(Row row) {
        return Arrays.stream(((GenericRowWithSchema) row)
                .values())
                .map(String::valueOf)
                .toList();
    }

    private List<List<String>> getDataFrameResults(Dataset<Row> df) {
        return df.collectAsList().stream()
                .map(this::convertRowToList)
                .toList();
    }

    public String run(String command, FetchType fetchType) {
        Dataset<Row> df = spark.sql(command);
        if (FetchType.ONE.equals(fetchType)) {
            df = df.limit(1);
        }
        return getDataFrameResults(df).toString();
    }

    /**
     * Get information about specified tables.
     * <p>
     * Follows best practices as specified in: <a href="https://arxiv.org/abs/2204.00498"> Rajkumar et al, 2022</a>
     * <p>
     * If `sample_rows_in_table_info`, the specified number of sample rows will be appended to each table description.
     * This can increase performance as demonstrated in the paper.
     */
    public String getTableInfoNoThrow(Set<String> tableNames) {
        try {
            return getTableInfo(tableNames);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Execute a SQL command and return a string representing the results.
     * <p>
     * If the statement returns rows, a string of the results is returned.
     * If the statement returns no rows, an empty string is returned.
     * If the statement throws an error, the error message is returned.
     */
    public String runNoThrow(String command, FetchType fetch) {
        try {
            return run(command, fetch);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String runNoThrow(String command) {
        return runNoThrow(command, FetchType.ALL);
    }
}
