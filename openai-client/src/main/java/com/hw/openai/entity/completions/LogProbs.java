package com.hw.openai.entity.completions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
@Data
public class LogProbs {

    List<String> tokens;

    @JsonProperty("token_logprobs")
    List<Double> tokenLogprobs;

    @JsonProperty("top_logprobs")
    List<Map<String, Double>> topLogprobs;

    @JsonProperty("text_offset")
    List<Integer> textOffset;
}
