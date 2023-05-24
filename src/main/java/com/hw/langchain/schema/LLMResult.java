package com.hw.langchain.schema;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @description: Class that contains all relevant information for an LLM Result.
 * @author: HamaWhite
 */
public class LLMResult {

    /**
     * List of the things generated. This is List<List<Generation>> because each input could have multiple generations.
     */
    private List<List<Generation>> generations;

    /**
     * For arbitrary LLM provider specific output.
     */
    private Map<String, Object> llmOutput;
}
