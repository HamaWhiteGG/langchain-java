package com.hw.langchain.schema;

import java.util.Dictionary;
import java.util.Optional;

/**
 * @description: Output of a single generation.
 * @author: HamaWhite
 */
public class Generation {

    /**
     * Generated text output.
     */
    private String text;

    /**
     * Raw generation info response from the provider.
     * May include things like reason for finishing (e.g. in OpenAI)
     */
    private Optional<Dictionary<String, Object>> generationInfo;
}
