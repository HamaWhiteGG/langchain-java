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

package com.hw.langchain.utilities.flink.sql;

import com.google.common.collect.Sets;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.exceptions.DatabaseNotExistException;
import org.apache.flink.types.Row;

import lombok.Builder;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author HamaWhite
 */
@Builder
public class FlinkSql {

    private static final String SHOW_CREATE_TABLE_SQL = "SHOW CREATE TABLE %s.`%s`.%s";

    private TableEnvironmentImpl tableEnv;

    private String catalogName;

    private String databaseName;

    @Builder.Default
    private Set<String> ignoreTables = Set.of();

    @Builder.Default
    private Set<String> includeTables = Set.of();

    private Set<String> allTables;

    private Set<String> usableTables;

    @Builder.Default
    private int sampleRowsInTableInfo = 3;

    public FlinkSql init() {
        catalogName = catalogName != null ? catalogName : tableEnv.getCurrentCatalog();
        tableEnv.useCatalog(catalogName);

        databaseName = databaseName != null ? databaseName : tableEnv.getCurrentDatabase();
        tableEnv.useDatabase(databaseName);

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
     * Get names of tables available.
     */
    public Set<String> getUsableTableNames() {
        if (CollectionUtils.isNotEmpty(includeTables)) {
            return includeTables;
        }
        return Sets.difference(allTables, ignoreTables);
    }

    @SneakyThrows(DatabaseNotExistException.class)
    private Set<String> getAllTableNames() {
        return Set.copyOf(getCatalog().listTables(databaseName));
    }

    private String getTableDdl(String table) {
        String showCreateSql = String.format(SHOW_CREATE_TABLE_SQL, catalogName, databaseName, table);
        TableResult tableResult = tableEnv.executeSql(showCreateSql);
        return tableResult.collect().next().getFieldAs(0);
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
            String tableInfo = getTableDdl(tableName);
            if (sampleRowsInTableInfo > 0) {
                String sampleRows = getSampleFlinkRows(tableName);
                tableInfo += "\n\n/*\n" + sampleRows + "\n*/";
            }
            tables.add(tableInfo);
        }
        return String.join("\n\n", tables);
    }

    public String getSampleFlinkRows(String table) {
        String query = String.format("SELECT * FROM %s LIMIT %d", table, sampleRowsInTableInfo);
        TableResult tableResult = tableEnv.executeSql(query);

        String columnsStr = String.join("\t", tableResult.getResolvedSchema().getColumnNames());
        String sampleRowsStr;
        try {
            List<List<String>> sampleRows = fetchRows(tableResult.collect());
            sampleRowsStr = String.join("\n", sampleRows.stream()
                    .map(row -> String.join("\t", row))
                    .toList());
        } catch (Exception e) {
            sampleRowsStr = "";
        }
        return String.format("%d rows from %s table:%n%s%n%s", sampleRowsInTableInfo, table, columnsStr, sampleRowsStr);
    }

    public String run(String command) {
        TableResult tableResult = tableEnv.executeSql(command);
        return fetchRows(tableResult.collect()).toString();
    }

    /**
     * Converts a Flink Row to a List of Strings.
     *
     * @param row The Flink Row to be converted.
     * @return A List of Strings containing the field values of the Row.
     */
    private List<String> convertRowToList(Row row) {
        List<String> results = new ArrayList<>();
        // Get the number of fields in the Row
        int arity = row.getArity();

        // Iterate over the fields in the Row and convert each field value to a String, adding it to the List
        for (int i = 0; i < arity; i++) {
            Object fieldValue = row.getField(i);
            results.add(String.valueOf(fieldValue));
        }
        return results;
    }

    private List<List<String>> fetchRows(Iterator<Row> iterator) {
        List<List<String>> rowList = new ArrayList<>();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            rowList.add(convertRowToList(row));
        }
        return rowList;
    }

    private Catalog getCatalog() {
        return tableEnv.getCatalog(catalogName)
                .orElseThrow(() -> new ValidationException(String.format("Catalog %s does not exist", catalogName)));
    }
}
