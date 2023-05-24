package com.hw.langchain.prompts.prompt;

import com.hw.langchain.prompts.base.StringPromptTemplate;
import com.hw.langchain.schema.BaseOutputParser;

import java.util.List;
import java.util.Map;

/**
 * @description: Schema to represent a prompt for an LLM.
 * @author: HamaWhite
 */
public class PromptTemplate extends StringPromptTemplate {

    /**
     * The prompt template.
     */
    private String template;

    /**
     * Whether or not to try validating the template.
     */
    private boolean validateTemplate;


    public PromptTemplate(List<String> inputVariableList, String template) {
        super(inputVariableList);
        this.template = template;
    }

    public PromptTemplate(List<String> inputVariableList, String template, BaseOutputParser outputParser) {
        super(inputVariableList, outputParser);
        this.template = template;
    }

    @Override
    public String format(Map<String, Object> optionMap) {
    }
}
