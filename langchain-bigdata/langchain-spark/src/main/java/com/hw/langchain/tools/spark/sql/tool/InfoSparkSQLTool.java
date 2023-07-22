package com.hw.langchain.tools.spark.sql.tool;

import com.hw.langchain.utilities.spark.sql.SparkSql;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.Set;

/**
 * Tool for getting metadata about a Spark SQL.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public class InfoSparkSQLTool extends BaseSparkSqlTool {

    private static final String NAME = "schema_sql_db";
    private static final String DESCRIPTION = """
            Input to this tool is a comma-separated list of tables, output is the schema and sample rows for those tables.
            Be sure that the tables actually exist by calling list_tables_sql_db first!

            Example Input: "table1, table2, table3"
            """;

    public InfoSparkSQLTool(SparkSql db) {
        super(db, NAME, DESCRIPTION);
    }

    /**
     * Get the schema for tables in a comma-separated list.
     */
    @Override
    public Object innerRun(String query, Map<String, Object> kwargs) {
        return db.getTableInfoNoThrow(Set.of(query.split(", ")));
    }
}
