package com.hw.langchain.llms.openai.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @description: Usage
 * @author: HamaWhite
 */
public class Usage {

    @JsonProperty("prompt_tokens")
    private Long promptTokens;

    @JsonProperty("completion_tokens")
    private Long completionTokens;

    @JsonProperty("total_tokens")
    private Long totalTokens;
}
