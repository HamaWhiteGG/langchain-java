package com.hw.langchain.utilities.restapi;

import com.google.gson.JsonObject;
import com.hw.langchain.utils.Utils;
import com.hw.restapi.AggregateAPI;
import com.hw.restapi.RestApiRequest;

import java.util.Map;

/**
 * RestApi tool wrapper
 */
public class RestAPIWrapper {

    private RestApiRequest restAPIPlatform;

    private String path;

    private String restApiKey;

    public static RestAPIWrapper of(Map<String, Object> kwargs) {
        return new RestAPIWrapper(kwargs);
    }

    public RestAPIWrapper(Map<String, Object> kwargs) {
        this.restApiKey = Utils.getFromDict(kwargs, "key");
        this.path = Utils.getFromDict(kwargs,"path");
    }

    /**
     * Run through the rest api
     */
    public String run(Map<String, Object> params) {
        this.restAPIPlatform = new AggregateAPI(params, this.restApiKey);
        JsonObject result = restAPIPlatform.getResultByPath(this.path);
        return result.toString();
    }
}
