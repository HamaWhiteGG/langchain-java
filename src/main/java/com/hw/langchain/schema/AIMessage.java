package com.hw.langchain.schema;

/**
 * @description: Type of message that is spoken by the AI.
 * @author: HamaWhite
 */
public class AIMessage extends BaseMessage {

    private boolean example;

    @Override
    public String type() {
        return "ai";
    }
}
