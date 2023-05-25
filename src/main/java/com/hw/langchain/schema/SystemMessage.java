package com.hw.langchain.schema;

/**
 * @description: Type of message that is a system message.
 * @author: HamaWhite
 */
public class SystemMessage extends BaseMessage {

    public SystemMessage(String content) {
        super(content);
    }

    @Override
    public String type() {
        return "system";
    }
}
