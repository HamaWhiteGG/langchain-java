package com.hw.langchain.prompts.base;

import com.hw.langchain.schema.BaseOutputParser;
import com.hw.langchain.schema.PromptValue;

import java.util.List;
import java.util.Map;

/**
 * @description: Base class for all prompt templates, returning a prompt.
 * @author: HamaWhite
 */
public abstract class BasePromptTemplate {

    /**
     * A list of the names of the variables the prompt template expects.
     */
    protected List<String> inputVariables;

    /**
     * How to parse the output of calling an LLM on this formatted prompt.
     */
    protected BaseOutputParser outputParser;


    public BasePromptTemplate(List<String> inputVariables) {
        this.inputVariables = inputVariables;
    }

    public BasePromptTemplate(List<String> inputVariables, BaseOutputParser outputParser) {
        this.inputVariables = inputVariables;
        this.outputParser = outputParser;
    }

    /**
     * Create Chat Messages.
     */
    public abstract PromptValue formatPrompt(Map<String, Object> kwargs);

    public abstract String format(Map<String, Object> kwargs);

}
