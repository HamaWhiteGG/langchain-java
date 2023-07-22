package com.hw.langchain.agents.toolkits.spark.sql.toolkit;

import com.hw.langchain.agents.toolkits.base.BaseToolkit;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.tools.base.BaseTool;
import com.hw.langchain.tools.spark.sql.tool.InfoSparkSQLTool;
import com.hw.langchain.tools.spark.sql.tool.ListSparkSqlTool;
import com.hw.langchain.tools.spark.sql.tool.QueryCheckerTool;
import com.hw.langchain.tools.spark.sql.tool.QuerySparkSqlTool;
import com.hw.langchain.utilities.spark.sql.SparkSql;

import java.util.List;

/**
 * Toolkit for interacting with Spark SQL.
 *
 * @author HamaWhite
 */
public class SparkSqlToolkit implements BaseToolkit {

    private final SparkSql db;

    private final BaseLanguageModel llm;

    public SparkSqlToolkit(SparkSql db, BaseLanguageModel llm) {
        this.db = db;
        this.llm = llm;
    }

    @Override
    public List<BaseTool> getTools() {
        return List.of(
                new QuerySparkSqlTool(db),
                new InfoSparkSQLTool(db),
                new ListSparkSqlTool(db),
                new QueryCheckerTool(db, llm)
        );
    }
}
