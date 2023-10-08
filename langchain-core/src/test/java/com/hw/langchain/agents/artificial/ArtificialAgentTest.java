package com.hw.langchain.agents.artificial;

import com.google.common.collect.Lists;
import com.hw.langchain.artificial.models.ArtificialModel;
import com.hw.langchain.tools.base.BaseTool;
import com.hw.langchain.tools.base.Tool;
import com.hw.langchain.utilities.restapi.RestAPIWrapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtificialAgentTest {

    private static final Logger logger = LoggerFactory.getLogger(ArtificialAgentTest.class);

    public BaseTool requestPhoneCity() {
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key","48c723e9562a7be8646bae283c8bc90d");
        kwargs.put("path", "/mobile/get");
        return new Tool("RequestPhoneCity",
                "{\"mapstruct\":\"{\\\"phone\\\":\\\"phone\\\"}\",\"required\":\"phone\"}",
                RestAPIWrapper.of(kwargs)::run);
    }

    public BaseTool requestCityWeather() {
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key","3b347817181dfd748144fdbe9f0c80f6");
        kwargs.put("path", "/simpleWeather/query");
        return new Tool("RequestCityWeather",
                "{\"mapstruct\":\"{\\\"city\\\":\\\"city,CITY\\\"}\",\"required\":\"city\"}",
                RestAPIWrapper.of(kwargs)::run);
    }

    public BaseTool requestCityLife() {
        Map<String, Object> kwargs = new HashMap<>();
        kwargs.put("key","3b347817181dfd748144fdbe9f0c80f6");
        kwargs.put("path", "/simpleWeather/life");
        return new Tool("RequestCityLife",
                "{\"mapstruct\":\"{\\\"city\\\":\\\"city,CITY\\\"}\",\"required\":\"city\"}",
                RestAPIWrapper.of(kwargs)::run);
    }

    @Test
    void testAgentWithArtificialModel() {
        List<BaseTool> baseTools = Lists.newArrayList(requestPhoneCity(), requestCityWeather(), requestCityLife());
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("phone", "13062587304");
        ArtificialAgent artificialAgent = ArtificialAgent.fromLMAndTools(ArtificialModel.builder().build(), baseTools, parameter);
        artificialAgent.run();
    }

}
