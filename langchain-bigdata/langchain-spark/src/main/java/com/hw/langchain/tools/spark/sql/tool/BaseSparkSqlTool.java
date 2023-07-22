package com.hw.langchain.tools.spark.sql.tool;

import com.hw.langchain.tools.base.BaseTool;
import com.hw.langchain.utilities.spark.sql.SparkSql;
import lombok.EqualsAndHashCode;

/**
 * Base tool for interacting with Spark SQL.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public abstract class BaseSparkSqlTool extends BaseTool {

    protected final SparkSql db;

    protected BaseSparkSqlTool(SparkSql db, String name, String description) {
        super(name, description);
        this.db = db;
    }
}
