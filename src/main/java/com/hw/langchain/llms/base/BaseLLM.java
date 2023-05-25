package com.hw.langchain.llms.base;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.schema.PromptValue;

import java.util.List;

/**
 * @description: LLM wrapper should take in a prompt and return a string.
 * @author: HamaWhite
 */
public class BaseLLM extends BaseLanguageModel {

    @Override
    public LLMResult generatePrompt(List<PromptValue> promptList, List<String> stopList) {
        return null;
    }
}
