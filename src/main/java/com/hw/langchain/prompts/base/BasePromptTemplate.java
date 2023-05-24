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
    protected List<String> inputVariableList;

    /**
     * How to parse the output of calling an LLM on this formatted prompt.
     */
    protected BaseOutputParser outputParser;


    public BasePromptTemplate(List<String> inputVariableList) {
        this.inputVariableList = inputVariableList;
    }

    public BasePromptTemplate(List<String> inputVariableList, BaseOutputParser outputParser) {
        this.inputVariableList = inputVariableList;
        this.outputParser = outputParser;
    }

    /**
     * Create Chat Messages.
     */
    public abstract PromptValue formatPrompt(Map<String, Object> optionMap);

    public abstract String format(Map<String, Object> optionMap);

}
