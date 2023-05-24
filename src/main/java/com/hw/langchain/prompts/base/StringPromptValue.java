package com.hw.langchain.prompts.base;

import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.HumanMessage;
import com.hw.langchain.schema.PromptValue;

import java.util.Collections;
import java.util.List;

/**
 * @description: StringPromptValue
 * @author: HamaWhite
 */
public class StringPromptValue implements PromptValue {

    private String text;

    public StringPromptValue(String text) {
        this.text = text;
    }

    @Override
    public List<BaseMessage> toMessageList() {
        return Collections.singletonList(new HumanMessage(text));
    }

    /**
     * Return prompt as string.
     */
    @Override
    public String toString() {
        return text;
    }
}
