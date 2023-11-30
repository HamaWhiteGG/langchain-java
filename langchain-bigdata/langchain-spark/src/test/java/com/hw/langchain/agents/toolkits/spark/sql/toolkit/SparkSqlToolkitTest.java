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

package com.hw.langchain.agents.toolkits.spark.sql.toolkit;

import com.hw.langchain.agents.agent.AgentExecutor;
import com.hw.langchain.chat.models.openai.ChatOpenAI;
import com.hw.langchain.utilities.spark.sql.SparkSql;

import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.hw.langchain.agents.toolkits.spark.sql.base.SparkSqlAgent.createSparkSqlAgent;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <a href="https://python.langchain.com/docs/modules/agents/toolkits/spark_sql">Spark SQL Agent</a>
 * <p>
 * Note:
 * Due to the error <a href="https://stackoverflow.com/questions/73465937/apache-spark-3-3-0-breaks-on-java-17-with-cannot-access-class-sun-nio-ch-direct">Apache Spark 3.3.0 breaks on Java 17 with 'cannot access class sun.nio.ch.DirectBuffer'</a>
 * <p>
 * Solution: Go to Run/Debug Configurations -> Build and Run -> Modify Options -> Allow Multiple Instances,
 * and then add the JVM option "--add-exports java.base/sun.nio.ch=ALL-UNNAMED"
 *
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class SparkSqlToolkitTest {

    private static final String SCHEMA = "langchain_example";

    private static SparkSession spark;

    private static AgentExecutor agentExecutor;

    @BeforeAll
    static void setup() {
        spark = SparkSession.builder().master("local").getOrCreate();

        // set the log level to WARN
        spark.sparkContext().setLogLevel("WARN");

        spark.sql("CREATE DATABASE IF NOT EXISTS " + SCHEMA);
        spark.sql("USE " + SCHEMA);

        String csvFilePath = "../../data/extras/modules/titanic.csv";
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(csvFilePath);

        String table = "titanic";
        df.write().saveAsTable(table);

        var sparkSql = SparkSql.builder()
                .spark(spark)
                .schema(SCHEMA)
                .build().init();

        var llm = ChatOpenAI.builder()
                .model("gpt-4")
                .temperature(0)
                .build().init();

        var toolkit = new SparkSqlToolkit(sparkSql, llm);
        agentExecutor = createSparkSqlAgent(llm, toolkit);
    }

    @Test
    void testDescribeTable() {
        var actual = agentExecutor.run("Describe the titanic table");

        var expected =
                """
                        The titanic table has the following columns: PassengerId (INT), Survived (INT), Pclass (INT), Name (STRING), Sex (STRING), Age (DOUBLE), SibSp (INT), Parch (INT), Ticket (STRING), Fare (DOUBLE), Cabin (STRING), Embarked (STRING). Here are some sample rows from the table:
                        1. PassengerId: 1, Survived: 0, Pclass: 3, Name: Braund, Mr. Owen Harris, Sex: male, Age: 22.0, SibSp: 1, Parch: 0, Ticket: A/5 21171, Fare: 7.25, Cabin: null, Embarked: S
                        2. PassengerId: 2, Survived: 1, Pclass: 1, Name: Cumings, Mrs. John Bradley (Florence Briggs Thayer), Sex: female, Age: 38.0, SibSp: 1, Parch: 0, Ticket: PC 17599, Fare: 71.2833, Cabin: C85, Embarked: C
                        3. PassengerId: 3, Survived: 1, Pclass: 3, Name: Heikkinen, Miss. Laina, Sex: female, Age: 26.0, SibSp: 0, Parch: 0, Ticket: STON/O2. 3101282, Fare: 7.925, Cabin: null, Embarked: S""";
        assertEquals(expected, actual);
    }

    @Test
    void testRunFirstQuery() {
        var actual = agentExecutor.run("whats the square root of the average age?");

        var expected = "The square root of the average age is approximately 5.45.";
        assertEquals(expected, actual);
    }

    @Test
    void testRunSecondQuery() {
        var actual = agentExecutor.run("What's the name of the oldest survived passenger?");

        var expected = "The name of the oldest survived passenger is Barkworth, Mr. Algernon Henry Wilson.";
        assertEquals(expected, actual);
    }

    @AfterAll
    static void cleanup() throws IOException, URISyntaxException {
        // delete the spark-warehouse directory
        String warehousePath = spark.conf().get("spark.sql.warehouse.dir");
        File warehouseDir = new File(new URI(warehousePath));
        if (warehouseDir.exists()) {
            FileUtils.deleteDirectory(warehouseDir);
        }
        spark.stop();
    }
}