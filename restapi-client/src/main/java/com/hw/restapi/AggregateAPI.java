/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hw.restapi;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * AggregateAPI By Free PlateForm
 *
 * @author Artisan
 */
public class AggregateAPI extends RestApiRequest {

    /**
     * api url
     */
    private String baseApiUrl;

    @Override
    protected Map<String, Object> getRequestProperty() {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("Content-Type", "application/x-www-form-urlencoded");
        return requestMap;
    }

    @Override
    protected String getApiKeyName() {
        return "key";
    }

    @Override
    protected String getRestBaseUrl() {
        if (StringUtils.isNotEmpty(baseApiUrl)) {
            return baseApiUrl;
        }
        return "http://apis.juhe.cn";
    }

    /**
     * Constructor
     *
     * @param parameter  search parameter
     * @param restApiKey secret API key
     */
    public AggregateAPI(Map<String, Object> parameter, String restApiKey) {
        super(parameter, restApiKey);
    }

    public AggregateAPI(Map<String, Object> parameter, String baseApiUrl, String restApiKey) {
        super(parameter, restApiKey);
        this.baseApiUrl = baseApiUrl;
    }

}
