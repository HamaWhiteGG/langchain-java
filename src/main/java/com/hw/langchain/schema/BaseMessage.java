package com.hw.langchain.schema;

import java.util.Map;

/**
 * @description: Message object.
 * @author: HamaWhite
 */
public abstract class BaseMessage {

    protected String content;

    protected Map<String, Object> additionalKwargs;

    public BaseMessage(String content) {
        this.content = content;
    }

    /**
     * Type of the message, used for serialization.
     */
    public abstract String type();
}
