package com.hw.langchain.llms.openai.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: Choice
 * @author: HamaWhite
 */
@Data
public class Choice {

    private String text;

    private Integer index;

    private Integer logprobs;

    @JsonProperty("finish_reason")
    private String finishReason;
}
