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
    private static final String DESCRIPTION = "Input is an empty string, output is a comma separated list of tables in the Spark SQL.";

    public ListSparkSqlTool(SparkSql db) {
        super(db, NAME, DESCRIPTION);
    }

    /**
     * Get the schema for a specific table.
     */
    @Override
    public Object innerRun(String query, Map<String, Object> kwargs) {
        return String.join(", ",db.getUsableTableNames());
    }
}
