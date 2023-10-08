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

package com.hw.langchain.chains.sql.database.prompt;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import com.hw.langchain.output.parsers.list.CommaSeparatedListOutputParser;
import com.hw.langchain.prompts.prompt.PromptTemplate;
import com.hw.langchain.utils.ResourceBundleUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Prompt
 * @author HamaWhite
 */
public class Prompt {

    private static String PROMPT_SUFFIX = ResourceBundleUtils.getString("prompt.suffix");

    private static String _DEFAULT_TEMPLATE = ResourceBundleUtils.getString("prompt.default.template");

    public static PromptTemplate PROMPT = new PromptTemplate(ListUtil.of("input", "table_info", "dialect", "top_k"),
            _DEFAULT_TEMPLATE + PROMPT_SUFFIX);

    private static String _DECIDER_TEMPLATE = ResourceBundleUtils.getString("prompt.decider.template");

    public static PromptTemplate DECIDER_PROMPT = new PromptTemplate(_DECIDER_TEMPLATE,
            ListUtil.of("query", "table_names"),
            new CommaSeparatedListOutputParser());

    private static String _mysql_prompt = ResourceBundleUtils.getString("prompt.database.mysql");

    public static PromptTemplate MYSQL_PROMPT =
            new PromptTemplate(ListUtil.of("input", "table_info", "top_k"), _mysql_prompt + PROMPT_SUFFIX);

    private static String _h2_prompt = ResourceBundleUtils.getString("prompt.database.h2");;

    public static PromptTemplate H2_PROMPT =
            new PromptTemplate(ListUtil.of("input", "table_info", "top_k"), _h2_prompt + PROMPT_SUFFIX);

    private static String _mariadb_prompt = ResourceBundleUtils.getString("prompt.database.mariadb");;

    public static PromptTemplate MARIADB_PROMPT =
            new PromptTemplate(ListUtil.of("input", "table_info", "top_k"), _mariadb_prompt + PROMPT_SUFFIX);

    private static String _oracle_prompt = ResourceBundleUtils.getString("prompt.database.oracle");

    public static PromptTemplate ORACLE_PROMPT =
            new PromptTemplate(ListUtil.of("input", "table_info", "top_k"), _oracle_prompt + PROMPT_SUFFIX);

    private static String _postgres_prompt = ResourceBundleUtils.getString("prompt.database.postgres");

    public static PromptTemplate POSTGRES_PROMPT =
            new PromptTemplate(ListUtil.of("input", "table_info", "top_k"), _postgres_prompt + PROMPT_SUFFIX);

    private static String _sqlite_prompt = ResourceBundleUtils.getString("prompt.database.sqlite");

    public static PromptTemplate SQLITE_PROMPT =
            new PromptTemplate(ListUtil.of("input", "table_info", "top_k"), _sqlite_prompt + PROMPT_SUFFIX);

    public static final Map<String, PromptTemplate> SQL_PROMPTS = MapBuilder.create(new HashMap<String, PromptTemplate>())
            .put("mysql", MYSQL_PROMPT)
            .put("h2", H2_PROMPT)
            .put("mariadb", MARIADB_PROMPT)
            .put("oracle", ORACLE_PROMPT)
            .put("postgresql", POSTGRES_PROMPT)
            .put("sqlite", SQLITE_PROMPT).map();
}
