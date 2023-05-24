package com.hw.langchain.chains.sql.database.base;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.sql.database.SQLDatabase;

import java.util.Map;

import static com.hw.langchain.chains.sql.database.prompt.Prompt.DECIDER_PROMPT;
import static com.hw.langchain.chains.sql.database.prompt.Prompt.PROMPT;

/**
 * Chain for querying SQL database that is a sequential chain.
 * <pre>
 * The chain is as follows:
 * 1. Based on the query, determine which tables to use.
 * 2. Based on those tables, call the normal SQL database chain.
 *
 * This is useful in cases where the number of tables in the database is large.
 * </pre>
 *
 * @author: HamaWhite
 */
public class SQLDatabaseSequentialChain implements Chain {

    private SQLDatabaseChain sqlChain;

    private LLMChain deciderChain;

    private String inputKey = "query";

    private String outputKey = "result";

    private boolean returnIntermediateSteps = false;

    /**
     * Load the necessary chains.
     */
    public static SQLDatabaseSequentialChain fromLLM(BaseLanguageModel llm,
                                                     SQLDatabase database,
                                                     BasePromptTemplate queryPrompt,
                                                     BasePromptTemplate deciderPrompt,
                                                     Map<String, Object> optionMap) {
        SQLDatabaseChain sqlChain = SQLDatabaseChain.fromLLM(llm, database, queryPrompt, optionMap);
        LLMChain deciderChain = new LLMChain(llm, deciderPrompt, "table_names");

        return null;
    }

    public static SQLDatabaseSequentialChain fromLLM(BaseLanguageModel llm, SQLDatabase database, Map<String, Object> optionMap) {
        return fromLLM(llm, database, PROMPT, DECIDER_PROMPT, optionMap);
    }
}
