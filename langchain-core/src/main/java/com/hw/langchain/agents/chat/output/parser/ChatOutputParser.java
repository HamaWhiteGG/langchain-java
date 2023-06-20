package com.hw.langchain.agents.chat.output.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hw.langchain.agents.agent.AgentOutputParser;
import com.hw.langchain.schema.AgentAction;
import com.hw.langchain.schema.AgentFinish;
import com.hw.langchain.schema.AgentResult;
import com.hw.langchain.schema.OutputParserException;

import java.lang.reflect.Type;
import java.util.Map;

import static com.hw.langchain.agents.chat.prompt.Prompt.FORMAT_INSTRUCTIONS;

/**
 * @author HamaWhite
 */
public class ChatOutputParser extends AgentOutputParser {

    private static final String FINAL_ANSWER_ACTION = "Final Answer:";


    @Override
    public AgentResult parse(String text) {
        boolean includesAnswer = text.contains(FINAL_ANSWER_ACTION);
        try {
            String action = text.split("```")[1];
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> response = new Gson().fromJson(action.strip(), mapType);

            boolean includesAction = response.containsKey("action") && response.containsKey("action_input");
            if (includesAnswer && includesAction) {
                throw new OutputParserException("Parsing LLM output produced a final answer and a parse-able action: " + text);
            }
            return new AgentAction(response.get("action").toString(), response.get("action_input"), text);

        } catch (Exception e) {
            if (!includesAnswer) {
                throw new OutputParserException("Could not parse LLM output: " + text);
            }
            String[] splitText = text.split(FINAL_ANSWER_ACTION);
            String output = splitText[splitText.length - 1].strip();
            return new AgentFinish(Map.ofEntries(Map.entry("output",output)), text);
        }
    }

    @Override
    public String getFormatInstructions() {
        return FORMAT_INSTRUCTIONS;
    }
}
