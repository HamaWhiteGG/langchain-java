package com.hw.langchain.artificial.models;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.schema.BaseMessage;
import com.hw.langchain.schema.LLMResult;
import com.hw.langchain.schema.PromptValue;
import lombok.experimental.SuperBuilder;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Artificial Language Model for Rules Mapstruct
 */
@SuperBuilder
public class ArtificialModel implements BaseLanguageModel {

    private static final Logger logger = LoggerFactory.getLogger(ArtificialModel.class);

    @Override
    public LLMResult generatePrompt(List<PromptValue> prompts, List<String> stop) {
        return null;
    }

    @Override
    public String predict(String text) {
        return null;
    }

    /**
     * text as description template:
     * {
     *     required:"phone,city,province"
     *     mapstruct:{
     *         phone:"telephone,PHONE,phone",
     *         city:"CITY,city,district",
     *         province:"province,PROVINCE"
     *     }
     * }
     * frontInput as the response json transfer to next input param:
     * {
     *     phone:13313585378,
     *     city:北京,
     *     province:北京
     * }
     * @param template
     * @param frontInput
     * @return
     */
    @Override
    public String predict(String template, List<String> frontInput) {
        if(StringUtils.isEmpty(template) || CollUtil.isEmpty(frontInput)){
            return null;
        }
        List<String> requiredList = Lists.newArrayList();
        JSONObject mapstructObject = new JSONObject();
        if(!adaptAndValidateTemplate(template, mapstructObject, requiredList)){
            return null;
        }
        String frontInputInfo = frontInput.iterator().next();
        Map<String, Object> frontInputObject = adaptAndValidateFrontInfo(frontInputInfo, requiredList);
        if(MapUtil.isEmpty(frontInputObject)){
            return null;
        }
        Map<String, Object> predictInput = new HashMap<>();
        for(String requiredKey : requiredList){
            String mapstructInfo = (String) mapstructObject.get(requiredKey);
            if(StringUtils.isEmpty(mapstructInfo)){
                predictInput.put(requiredKey, frontInputObject.get(requiredKey));
                continue;
            }
            List<String> mapstructList = Arrays.asList(mapstructInfo.split(","));
            Object requiredValue = adaptRequiredValue(mapstructList, frontInputObject);
            if(Objects.isNull(requiredValue)){
                logger.warn("requiredValue adapt failed,requiredKey-{},frontInput-{}", requiredKey, frontInputObject);
            }
            predictInput.put(requiredKey, requiredValue);
        }
        return JSON.toJSONString(predictInput);
    }

    /**
     *
     * @param mapstructList
     * @param frontInputObject
     * @return
     */
    private Object adaptRequiredValue(List<String> mapstructList, Map<String, Object> frontInputObject) {
        for (String mappedKey : mapstructList) {
            Object mappedValue = frontInputObject.get(mappedKey);
            if (Objects.isNull(mappedValue)) {
                continue;
            }
            return mappedValue;
        }

        return null;
    }

    /**
     * adapt and validate frontInfo
     * @param frontInputInfo
     * @param requiredList
     * @return
     */
    private Map<String, Object> adaptAndValidateFrontInfo(String frontInputInfo, List<String> requiredList) {
        try {
            Map<String, Object> frontInputObject = JSON.parseObject(frontInputInfo);
            if(MapUtil.isEmpty(frontInputObject)){
                logger.error("parse front input object empty");
                return null;
            }
            List<String> frontKeyList = new ArrayList<>(frontInputObject.keySet());
            if(!frontKeyList.containsAll(requiredList)){
                logger.error("the frontInput key list can not contains all required key list");
                return null;
            }
            return frontInputObject;
        }catch (Exception exception){
            logger.error("parse frontInput object failed,exception-", exception);
            return null;
        }
    }

    /**
     * adapt and validate template for requiredKeyList and mapstructObject
     * @param template
     * @param mapstructObject
     * @param requiredList
     * @return
     */
    private boolean adaptAndValidateTemplate(String template, JSONObject mapstructObject, List<String> requiredList){
        try {
            JSONObject requiredObject = JSON.parseObject(template);
            if(MapUtil.isEmpty(requiredObject)){
                return false;
            }
            String requiredInfo = (String) requiredObject.get("required");
            if(StringUtils.isEmpty(requiredInfo)){
                return false;
            }
            requiredList.addAll(Arrays.asList(requiredInfo.split(",")));
            mapstructObject.putAll(JSON.parseObject(requiredObject.get("mapstruct").toString()));
        }catch (Exception exception){
            logger.error("parse required object failed,exception-", exception);
            return false;
        }
        return true;
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> messages) {
        return null;
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> messages, List<String> stop) {
        return null;
    }
}
