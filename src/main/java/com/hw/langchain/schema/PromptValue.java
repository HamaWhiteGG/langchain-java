package com.hw.langchain.schema;

import java.util.List;

/**
 * @description: PromptValue
 * @author: HamaWhite
 */
public interface PromptValue {

    /**
     * Returns the prompt as a list of messages.
     */
    List<BaseMessage> toMessageList();
}
