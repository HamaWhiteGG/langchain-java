package com.hw.langchain.schema;

/**
 * @description: Type of message that is spoken by the AI.
 * @author: HamaWhite
 */
public class AIMessage extends BaseMessage {

    private boolean example;

    public AIMessage(String content) {
        super(content);
    }

    @Override
    public String type() {
        return "ai";
    }
}
