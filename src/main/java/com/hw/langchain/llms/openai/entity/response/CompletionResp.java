package com.hw.langchain.llms.openai.entity.response;

import lombok.Data;

import java.util.List;

/**
 * @description: CompletionResp
 * @author: HamaWhite
 */
@Data
public class CompletionResp {

    private String id;

    private String object;

    private Long created;

    private String model;

    private List<Choice> choices;

    private Usage usage;
}
