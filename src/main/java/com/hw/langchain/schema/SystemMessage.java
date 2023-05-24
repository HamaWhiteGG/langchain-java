package com.hw.langchain.schema;

/**
 * @description: Type of message that is a system message.
 * @author: HamaWhite
 */
public class SystemMessage extends BaseMessage {

    @Override
    public String type() {
        return "system";
    }
}
