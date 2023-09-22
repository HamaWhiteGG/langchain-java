package com.hw.langchain.agents.artificial;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.tools.base.BaseTool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Artificial Agent for load tools runner
 */
public class ArtificialAgent {

    private static final Logger logger = LoggerFactory.getLogger(ArtificialAgent.class);

    private BaseLanguageModel lm;
    private List<BaseTool> tools;
    private Map<String, Object> kwargs;

    public ArtificialAgent(BaseLanguageModel lm, List<BaseTool> baseTools, Map<String, Object> params){
        this.lm = lm;
        this.tools = baseTools;
        this.kwargs = params;
    }

    public static ArtificialAgent fromLMAndTools(BaseLanguageModel lm, List<BaseTool> tools, Map<String, Object> kwargs) {
        return new ArtificialAgent(lm, tools, kwargs);
    }

    /**
     * execute run engine
     * @return
     */
    public boolean run(){
        if(CollectionUtils.isEmpty(tools) || MapUtils.isEmpty(kwargs)){
            logger.error("the required params must be not null");
            return false;
        }
        for(BaseTool tool : tools){
            String input = lm.predict(tool.getDescription(), Lists.newArrayList(JSON.toJSONString(kwargs)));
            if(StringUtils.isEmpty(input)){
                logger.error("artificial model predict input info failed");
                return false;
            }
            if(logger.isDebugEnabled()){
                logger.debug("predict input info - {}", input);
            }
            Object response = tool.innerRun(input, kwargs);
            if(Objects.isNull(response)){
                return false;
            }
            if(logger.isInfoEnabled()){
                logger.info("the tool -{}-, response result is: {}", tool.name, response);
            }
            kwargs = JSON.parseObject(response.toString());
        }
        return true;
    }
}
