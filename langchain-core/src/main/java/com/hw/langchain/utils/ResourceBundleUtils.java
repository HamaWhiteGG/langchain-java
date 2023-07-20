/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hw.langchain.utils;

import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Borges
 */
@UtilityClass
public class ResourceBundleUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceBundleUtils.class);

    private static final String BUNDLE_NAME = "i18n.messages";
    private static final String USE_LANGUAGE_ENV = "USE_LANGUAGE";

    public static String getString(String key) {
        String language = System.getenv(USE_LANGUAGE_ENV);

        Locale locale = new Locale("en", "US");

        if (language != null) {
            try {
                locale = LocaleUtils.toLocale(language);
                LOG.info("Using locale from {}", language);
            } catch (Exception ex) {
                LOG.warn("Can't load locale for language {}, setting default en_US", language);
            }
        }

        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        return bundle.getString(key);
    }

}
