package com.hw.langchain.tools.spark.sql.tool;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.utilities.spark.sql.SparkSql;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

import static com.hw.langchain.tools.spark.sql.prompt.Prompt.QUERY_CHECKER;

/**
 * Use an LLM to check if a query is correct.
 *
 * @author HamaWhite
 */
@EqualsAndHashCode(callSuper = true)
public class QueryCheckerTool extends BaseSparkSqlTool {

    private final BaseLanguageModel llm;

    private LLMChain llmChain;

    private static final String NAME = "query_checker_sql_db";
    private static final String DESCRIPTION = """
            Use this tool to double check if your query is correct before executing it.
            Always use this tool before executing a query with query_sql_db!
            """;

    public QueryCheckerTool(SparkSql db, BaseLanguageModel llm) {
        super(db, NAME, DESCRIPTION);
        this.llm = llm;

        initializeLlmChain();
    }

    private void initializeLlmChain() {
        llmChain = new LLMChain(llm, new PromptTemplate(List.of("query"), QUERY_CHECKER));
    }

    /**
     * Use the LLM to check the query.
     */
    @Override
    public Object innerRun(String query, Map<String, Object> kwargs) {
        return llmChain.predict(Map.of("query", query));
    }
}
