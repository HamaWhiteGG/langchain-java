package com.hw.langchain.agents.artificial;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.tools.base.BaseTool;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Artificial Agent for load tools runner
 */
public class ArtificialAgent {
    private BaseLanguageModel lm;
    private List<BaseTool> tools;
    private Map<String, Object> kwargs;

    public ArtificialAgent(BaseLanguageModel lm, List<BaseTool> baseTools, Map<String, Object> params){
        this.lm = lm;
        this.tools = baseTools;
        this.kwargs = params;
    }

    public ArtificialAgent fromLMAndTools(
            BaseLanguageModel lm,
            List<BaseTool> tools,
            Map<String, Object> kwargs) {
        return new ArtificialAgent(lm, tools, kwargs);
    }

    /**
     * execute run engine
     * @return
     */
    public boolean run(){
        if(CollectionUtils.isEmpty(tools)){
            return false;
        }
        for(BaseTool tool : tools){
            String input = lm.predict(tool.getDescription(), Lists.newArrayList(JSON.toJSONString(kwargs)));
            Object response = tool.innerRun(input, kwargs);
            if(Objects.isNull(response)){
                return false;
            }
            kwargs = JSON.parseObject(response.toString());
        }
        return true;
    }
}
