package com.hw.langchain.artificial.models;

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.schema.PromptValue;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Artificial Language Model for Rules Mapstruct
 */
@SuperBuilder
public class ArtificialLanguageModel implements BaseLanguageModel {

    @Override
    public LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop) {
        return null;
    }

    @Override
    public String predict(String text) {
        return null;
    }

    /**
     * text as description template:
     * {
     *     required:{"phone","city", "province"},
     *     mapstruct:{
     *         phone:{"telephone", "PHONE","phone"},
     *         city:{"CITY","city","district"},
     *         province:{"province", "PROVINCE"}
     *     }
     * }
     * frontInput as the response json transfer to next input param
     * @param template
     * @param frontInput
     * @return
     */
    @Override
    public String predict(String template, List<String> frontInput) {
        return null;
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> messages) {
        return null;
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> messages, List<String> stop) {
        return null;
    }
}
