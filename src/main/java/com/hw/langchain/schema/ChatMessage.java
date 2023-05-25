package com.hw.langchain.schema;

/**
 * @description: Type of message with arbitrary speaker.
 * @author: HamaWhite
 */
public class ChatMessage extends BaseMessage {

    private String str;

    public ChatMessage(String content) {
        super(content);
    }

    @Override
    public String type() {
        return "chat";
    }
}
