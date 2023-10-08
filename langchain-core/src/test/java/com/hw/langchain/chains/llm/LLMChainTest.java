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

package com.hw.langchain.chains.llm;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.prompts.chat.ChatPromptTemplate;
import com.hw.langchain.prompts.chat.HumanMessagePromptTemplate;
import com.hw.langchain.prompts.chat.SystemMessagePromptTemplate;
import com.hw.langchain.prompts.prompt.PromptTemplate;

import lombok.var;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * LLMChainTest
 *
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class LLMChainTest {

    private static BaseLanguageModel llm;

    private static BaseLanguageModel chat;

    @BeforeAll
    public static void setup() throws SQLException {
        llm = OpenAI.builder()
                .temperature(0)
                .build()
                .init();

        chat = ChatOpenAI.builder()
                .temperature(0)
                .build()
                .init();
    }

    @Test
    void testLLMChainWithOneInputVariables() {
        PromptTemplate prompt = new PromptTemplate(ListUtil.of("product"),
                "What is a good name for a company that makes {product}?");

        Chain chain = new LLMChain(llm, prompt);
        String actual = chain.run("colorful socks");

        String expected = "\n\nSocktastic!";
        assertEquals(expected, actual);
    }

    @Test
    void testAsyncRun() {
        PromptTemplate prompt = new PromptTemplate(List.of("product"),
                "What is a good name for a company that makes {product}?");

        Chain chain = new LLMChain(llm, prompt);
        Flux<String> actual = chain.asyncRun("colorful socks");

        List<String> resultList = actual.collectList().block();
        assertThat(resultList).isNotNull();
        assertThat(String.join("", resultList)).isEqualTo("\n\nSocktastic!");
    }

    @Test
    void testLLMChainWithMultipleInputVariables() {
        PromptTemplate prompt = new PromptTemplate(ListUtil.of("company", "product"),
                "What is a good name for {company} that makes {product}?");

        Chain chain = new LLMChain(llm, prompt);
        String actual = chain.run(MapBuilder.create()
                .put("company", "ABC Startup")
                .put("product", "colorful socks").map());

        String expected = "\n\nSocktastic!";
        assertEquals(expected, actual);
    }

    @Test
    void testLLMChainForNLP2SQL() {
        String template = "              " +
                "                        You are a H2 expert. Given an input question, first create a syntactically correct H2 query to run, then look at the results of the query and return the answer to the input question.\n" +
                "                        Unless the user specifies in the question a specific number of examples to obtain, query for at most 5 results using the LIMIT clause as per H2. You can order the results to return the most informative data in the database.\n" +
                "                        Never query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in backticks (`) to denote them as delimited identifiers.\n" +
                "                        Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.\n" +
                "                        Pay attention to use CURDATE() function to get the current date, if the question involves \"today\".\n" +
                "\n" +
                "                        Use the following format:\n" +
                "\n" +
                "                        Question: Question here\n" +
                "                        SQLQuery: SQL Query to run\n" +
                "                        SQLResult: Result of the SQLQuery\n" +
                "                        Answer: Final answer here\n" +
                "\n" +
                "                        Only use the following tables:\n" +
                "\n" +
                "                        CREATE TABLE parents (\n" +
                "                        \tid INTEGER(32),\n" +
                "                        \tstudent_name CHARACTER VARYING(64),\n" +
                "                        \tparent_name CHARACTER VARYING(64),\n" +
                "                        \tparent_mobile CHARACTER VARYING(16)\n" +
                "                        )\n" +
                "\n" +
                "                        /*\n" +
                "                        3 rows from parents table:\n" +
                "                        id\tstudent_name\tparent_name\tparent_mobile\n" +
                "                        1\tAlex\tBarry\t088121\n" +
                "                        2\tAlice\tJessica\t088122\n" +
                "                        3\tJack\tSimon\t088123\n" +
                "                        */\n" +
                "\n" +
                "\n" +
                "                        CREATE TABLE students (\n" +
                "                        \tid INTEGER(32),\n" +
                "                        \tname CHARACTER VARYING(64),\n" +
                "                        \tscore INTEGER(32) COMMENT 'math score',\n" +
                "                        \tteacher_note CHARACTER VARYING(256)\n" +
                "                        ) COMMENT 'student score table'\n" +
                "\n" +
                "                        /*\n" +
                "                        3 rows from students table:\n" +
                "                        id\tname\tscore\tteacher_note\n" +
                "                        1\tAlex\t100\tAlex did perfectly every day in the class.\n" +
                "                        2\tAlice\t70\tAlice needs a lot of improvements.\n" +
                "                        3\tJack\t75\tEvent it is not the best, Jack has already improved.\n" +
                "                        */\n" +
                "\n" +
                "                        Question: Who got zero score? Show me her parent's contact information.\n" +
                "                        SQLQuery:";
        PromptTemplate prompt = new PromptTemplate(ListUtil.of(), template);

        Chain chain = new LLMChain(llm, prompt);
        String actual = chain.run(MapUtil.of("stop", ListUtil.of("\nSQLResult:")));
        String expected =
                " SELECT `parent_name`, `parent_mobile` FROM `parents` WHERE `student_name` IN (SELECT `name` FROM `students` WHERE `score` = 0) LIMIT 5;";
        assertEquals(expected, actual);
    }

    @Test
    void testLLMChainWithChatModels() {
        var template = "You are a helpful assistant that translates {input_language} to {output_language}.";
        var systemMessagePrompt = SystemMessagePromptTemplate.fromTemplate(template);

        var humanTemplate = "{text}";
        var humanMessagePrompt = HumanMessagePromptTemplate.fromTemplate(humanTemplate);

        var chatPrompt = ChatPromptTemplate.fromMessages(ListUtil.of(systemMessagePrompt, humanMessagePrompt));

        var chain = new LLMChain(chat, chatPrompt);
        String actual = chain.run(MapBuilder.create()
                .put("input_language", "English")
                .put("output_language", "French")
                .put("text", "I love programming.").map());

        String expected = "J'adore la programmation.";
        assertEquals(expected, actual);
    }
}