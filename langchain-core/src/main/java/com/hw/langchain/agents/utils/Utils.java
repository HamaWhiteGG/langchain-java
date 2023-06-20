package com.hw.langchain.agents.utils;

import com.hw.langchain.tools.base.BaseTool;

import java.util.List;

/**
 * @author HamaWhite
 */
public class Utils {

    public static void validateToolsSingleInput(String className, List<BaseTool> tools) {
        for (BaseTool tool : tools) {
            if (!tool.isSingleInput()) {
                throw new IllegalArgumentException(className + " does not support multi-input tool " + tool.getName() + ".");
            }
        }
    }
}
