package com.hw.langchain.tools.spark.sql.tool;

import com.hw.langchain.utilities.spark.sql.SparkSql;

import java.util.Map;

/**
 * Tool for querying a Spark SQL.
 *
 * @author HamaWhite
 */
public class QuerySparkSqlTool extends BaseSparkSqlTool {

    private static final String NAME = "query_sql_db";
    private static final String DESCRIPTION = """
            Input to this tool is a detailed and correct SQL query, output is a result from the Spark SQL.
            If the query is not correct, an error message will be returned.
            If an error is returned, rewrite the query, check the query, and try again.
            """;

    public QuerySparkSqlTool(SparkSql db) {
        super(db, NAME, DESCRIPTION);
    }

    /**
     * Execute the query, return the results or an error message.
     */
    @Override
    public Object innerRun(String query, Map<String, Object> kwargs) {
        return db.runNoThrow(query);
    }
}
