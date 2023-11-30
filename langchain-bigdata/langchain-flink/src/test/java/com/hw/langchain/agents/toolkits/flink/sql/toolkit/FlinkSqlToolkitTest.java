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

package com.hw.langchain.agents.toolkits.flink.sql.toolkit;

import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.utilities.flink.sql.FlinkSql;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.util.FlinkRuntimeException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.hw.langchain.agents.toolkits.flink.sql.base.FlinkSqlAgent.createFlinkSqlAgent;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class FlinkSqlToolkitTest {

    private static TableEnvironmentImpl tableEnv;

    @BeforeAll
    static void setup() {
        initTableEnvironment();
        createTableOfTitanic();
    }

    private static void initTableEnvironment() {
        Configuration configuration = new Configuration();
        try (StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(configuration)) {
            env.setParallelism(1);
            EnvironmentSettings settings = EnvironmentSettings.newInstance()
                    .inBatchMode()
                    .build();
            tableEnv = (TableEnvironmentImpl) StreamTableEnvironment.create(env, settings);
        } catch (Exception e) {
            throw new FlinkRuntimeException("Init local flink execution environment error", e);
        }
    }

    /**
     * titanic_flink.csv only retains 10 records.
     */
    private static void createTableOfTitanic() {
        tableEnv.executeSql("DROP TABLE IF EXISTS titanic ");

        tableEnv.executeSql("""
                CREATE TABLE IF NOT EXISTS titanic (
                    PassengerId     INT,
                    Survived        INT,
                    Pclass          INT,
                    Name            STRING,
                    Sex             STRING,
                    Age             DOUBLE,
                    SibSp           INT,
                    Parch           INT,
                    Ticket          STRING,
                    Fare            DOUBLE,
                    Cabin           STRING,
                    Embarked        STRING
                ) WITH (
                    'connector' = 'filesystem',
                    'path' = '../../data/extras/modules/titanic_flink.csv',
                    'format' = 'csv'
                )""");
    }

    @Test
    void testRunQuery() {
        var flinkSql = FlinkSql.builder()
                .tableEnv(tableEnv)
                .build()
                .init();

        var llm = ChatOpenAI.builder()
                .model("gpt-4")
                .temperature(0)
                .build().init();

        var toolkit = new FlinkSqlToolkit(flinkSql, llm);
        var agentExecutor = createFlinkSqlAgent(llm, toolkit);

        // SELECT SQRT(AVG(Age)) FROM titanic
        var actual = agentExecutor.run("whats the square root of the average age?");
        // sometimes it's 'The square root of the average age is approximately 5.07.'
        assertEquals("The square root of the average age is approximately 5.07.", actual);

        // TODO: It should be DESC here, not ASC.
        // SELECT Name FROM titanic WHERE Survived = 1 ORDER BY Age ASC LIMIT 1
        // actual = agentExecutor.run("What's the name of the oldest survived passenger?");
        // assertEquals("Sandstrom, Miss. Marguerite Rut", actual);
    }
}