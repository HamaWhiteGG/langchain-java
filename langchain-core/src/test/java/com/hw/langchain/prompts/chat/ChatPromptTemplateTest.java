package com.hw.langchain.prompts.chat;

import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.HumanMessage;
import com.hw.langchain.schema.SystemMessage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author HamaWhite
 */
class ChatPromptTemplateTest {

    @Test
    void testFormatMessages() {
        var template = "You are a helpful assistant that translates {input_language} to {output_language}.";
        var systemMessagePrompt = SystemMessagePromptTemplate.fromTemplate(template);

        var humanTemplate = "{text}";
        var humanMessagePrompt = HumanMessagePromptTemplate.fromTemplate(humanTemplate);

        var chatPrompt = ChatPromptTemplate.fromMessages(List.of(systemMessagePrompt, humanMessagePrompt));
        List<BaseMessage> actual = chatPrompt.formatMessages(Map.of("input_language", "English",
                "output_language", "French",
                "text", "I love programming."));

        List<BaseMessage> expected = List.of(
                new SystemMessage("You are a helpful assistant that translates English to French."),
                new HumanMessage("I love programming."));
        assertEquals(expected, actual);
    }
}