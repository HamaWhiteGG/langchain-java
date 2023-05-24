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

    public StringPromptTemplate(List<String> inputVariableList) {
        super(inputVariableList);
    }

    public StringPromptTemplate(List<String> inputVariableList, BaseOutputParser outputParser) {
        super(inputVariableList,outputParser);
    }

    @Override
    public PromptValue formatPrompt(Map<String, Object> optionMap) {
        return new StringPromptValue(format(optionMap));
    }

}
