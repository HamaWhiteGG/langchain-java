package com.hw.langchain.schema;

/**
 * @description: Type of message that is spoken by the human.
 * @author: HamaWhite
 */
public class HumanMessage extends BaseMessage {

    private boolean example;

    public HumanMessage(String content) {
        super(content);
    }


    @Override
    public String type() {
        return "human";
    }
}
