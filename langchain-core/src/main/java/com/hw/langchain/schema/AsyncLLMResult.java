package com.hw.langchain.schema;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lingjue@ubuntu
 * @since 8/1/23 7:01 PM
 */
@Data
public class AsyncLLMResult {

    /**
     * List of the things generated. This is List<List<Generation>> because each input could have multiple generations.
     */
    private List<? extends Generation> generations;

    /**
     * For arbitrary LLM provider specific output.
     */
    private Map<String, Object> llmOutput;

    public AsyncLLMResult(List<? extends Generation> generations, Map<String, Object> llmOutput) {
        this.generations = generations;
        this.llmOutput = llmOutput;
    }

}
