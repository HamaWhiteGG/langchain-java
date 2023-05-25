package com.hw.langchain.base.language;

import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.schema.PromptValue;

import java.util.List;

/**
 * @description: BaseLanguageModel
 * @author: HamaWhite
 */
public abstract class BaseLanguageModel {

    /**
     * Take in a list of prompt values and return an LLMResult.
     */
    public abstract LLMResult generatePrompt(List<PromptValue> promptList, List<String> stopList);
}
