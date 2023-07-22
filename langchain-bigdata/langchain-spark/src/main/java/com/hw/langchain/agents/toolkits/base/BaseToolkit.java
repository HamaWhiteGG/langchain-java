package com.hw.langchain.agents.toolkits.base;

import com.hw.langchain.tools.base.BaseTool;

import java.util.List;

/**
 * Base Toolkit representing a collection of related tools.
 *
 * @author HamaWhite
 */
public interface BaseToolkit {

     /**
      * Get the tools in the toolkit.
      *
      * @return a list of tools in the toolkit.
      */
     List<BaseTool> getTools();
}
