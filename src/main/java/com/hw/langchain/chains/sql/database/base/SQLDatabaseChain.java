package com.hw.langchain.chains.sql.database.base;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.llm.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;
import com.hw.langchain.sql.database.SQLDatabase;

import java.util.Map;

import static com.hw.langchain.chains.sql.database.prompt.Prompt.PROMPT;
import static com.hw.langchain.chains.sql.database.prompt.Prompt.SQL_PROMPTS;

/**
 * @description: Chain for interacting with SQL Database.
 * @author: HamaWhite
 */
public class SQLDatabaseChain implements Chain {

    private LLMChain llmChain;

    /**
     * SQL Database to connect to.
     */
    private SQLDatabase database;

    /**
     * Number of results to return from the query
     */
    private int topK;

    private String inputKey = "query";

    private String outputKey = "result";

    /**
     * Whether or not to return the intermediate steps along with the final answer.
     */
    private boolean returnIntermediateSteps = false;

    /**
     * Whether or not to return the result of querying the SQL table directly.
     */
    private boolean returnDirect = false;

    /**
     * Whether or not the query checker tool should be used to attempt to fix the initial SQL from the LLM.
     */
    private boolean useQueryChecker = false;

    /**
     * The prompt template that should be used by the query checker
     */
    private BasePromptTemplate queryCheckerPrompt;

    public SQLDatabaseChain(LLMChain llmChain, SQLDatabase database) {
        this.llmChain = llmChain;
        this.database = database;
    }

    public static SQLDatabaseChain fromLLM(BaseLanguageModel llm, SQLDatabase database, BasePromptTemplate prompt, Map<String, Object> kwargs) {
        if (prompt == null) {
            prompt = SQL_PROMPTS.getOrDefault(database.getDialect(), PROMPT);
        }
        LLMChain llmChain = new LLMChain(llm, prompt);
        return new SQLDatabaseChain(llmChain, database, kwargs);
    }
}
