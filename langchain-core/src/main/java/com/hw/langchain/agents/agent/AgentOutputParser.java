package com.hw.langchain.agents.agent;

import com.hw.langchain.schema.BaseOutputParser;

/**
 * @author HamaWhite
 */
public abstract class AgentOutputParser extends BaseOutputParser {

    @Override
    public Object parse(String text) {
        return null;
    }

    @Override
    public String getFormatInstructions() {
        return null;
    }
}
