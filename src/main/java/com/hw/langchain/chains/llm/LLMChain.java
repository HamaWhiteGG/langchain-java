package com.hw.langchain.chains.llm;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.prompts.base.BasePromptTemplate;

/**
 * @description: Chain to run queries against LLMs
 * @author: HamaWhite
 */
public class LLMChain implements Chain{

    private BaseLanguageModel llm;

    /**
     * Prompt object to use.
     */
    private BasePromptTemplate prompt;

    private String outputKey = "text";

    public LLMChain(BaseLanguageModel llm, BasePromptTemplate prompt) {
        this.llm = llm;
        this.prompt = prompt;
    }

    public LLMChain(BaseLanguageModel llm, BasePromptTemplate prompt, String outputKey) {
        this.llm = llm;
        this.prompt = prompt;
        this.outputKey = outputKey;
    }
}
