package com.hw.langchain.prompts.base;

import com.hw.langchain.schema.BaseOutputParser;
import com.hw.langchain.schema.PromptValue;

import java.util.List;
import java.util.Map;

/**
 * @description: String prompt should expose the format method, returning a prompt.
 * @author: HamaWhite
 */
public abstract class StringPromptTemplate extends BasePromptTemplate{

    public StringPromptTemplate(List<String> inputVariables) {
        super(inputVariables);
    }

    public StringPromptTemplate(List<String> inputVariables, BaseOutputParser outputParser) {
        super(inputVariables,outputParser);
    }

    @Override
    public PromptValue formatPrompt(Map<String, Object> kwargs) {
        return new StringPromptValue(format(kwargs));
    }

}
