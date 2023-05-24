package com.hw.langchain.schema;

/**
 * @description: Type of message with arbitrary speaker.
 * @author: HamaWhite
 */
public class ChatMessage extends BaseMessage {

    private String str;

    @Override
    public String type() {
        return "chat";
    }
}
