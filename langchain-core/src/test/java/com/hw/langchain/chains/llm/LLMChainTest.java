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

import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.llms.openai.OpenAI;
import com.hw.langchain.prompts.chat.ChatPromptTemplate;
import com.hw.langchain.prompts.chat.HumanMessagePromptTemplate;
import com.hw.langchain.prompts.chat.SystemMessagePromptTemplate;
import com.hw.langchain.prompts.prompt.PromptTemplate;

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
        PromptTemplate prompt = new PromptTemplate(List.of("product"),
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
        PromptTemplate prompt = new PromptTemplate(List.of("company", "product"),
                "What is a good name for {company} that makes {product}?");

        Chain chain = new LLMChain(llm, prompt);
        String actual = chain.run(Map.of("company", "ABC Startup", "product", "colorful socks"));

        String expected = "\n\nSocktastic!";
        assertEquals(expected, actual);
    }

    @Test
    void testLLMChainForNLP2SQL() {
        String template =
                """
                        You are a H2 expert. Given an input question, first create a syntactically correct H2 query to run, then look at the results of the query and return the answer to the input question.
                        Unless the user specifies in the question a specific number of examples to obtain, query for at most 5 results using the LIMIT clause as per H2. You can order the results to return the most informative data in the database.
                        Never query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in backticks (`) to denote them as delimited identifiers.
                        Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.
                        Pay attention to use CURDATE() function to get the current date, if the question involves "today".

                        Use the following format:

                        Question: Question here
                        SQLQuery: SQL Query to run
                        SQLResult: Result of the SQLQuery
                        Answer: Final answer here

                        Only use the following tables:

                        CREATE TABLE parents (
                        	id INTEGER(32),
                        	student_name CHARACTER VARYING(64),
                        	parent_name CHARACTER VARYING(64),
                        	parent_mobile CHARACTER VARYING(16)
                        )

                        /*
                        3 rows from parents table:
                        id	student_name	parent_name	parent_mobile
                        1	Alex	Barry	088121
                        2	Alice	Jessica	088122
                        3	Jack	Simon	088123
                        */


                        CREATE TABLE students (
                        	id INTEGER(32),
                        	name CHARACTER VARYING(64),
                        	score INTEGER(32) COMMENT 'math score',
                        	teacher_note CHARACTER VARYING(256)
                        ) COMMENT 'student score table'

                        /*
                        3 rows from students table:
                        id	name	score	teacher_note
                        1	Alex	100	Alex did perfectly every day in the class.
                        2	Alice	70	Alice needs a lot of improvements.
                        3	Jack	75	Event it is not the best, Jack has already improved.
                        */

                        Question: Who got zero score? Show me her parent's contact information.
                        SQLQuery:""";
        PromptTemplate prompt = new PromptTemplate(List.of(), template);

        Chain chain = new LLMChain(llm, prompt);
        String actual = chain.run(Map.of("stop", List.of("\nSQLResult:")));
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

        var chatPrompt = ChatPromptTemplate.fromMessages(List.of(systemMessagePrompt, humanMessagePrompt));

        var chain = new LLMChain(chat, chatPrompt);
        String actual = chain.run(Map.of("input_language", "English",
                "output_language", "French",
                "text", "I love programming."));

        String expected = "J'adore la programmation.";
        assertEquals(expected, actual);
    }
}