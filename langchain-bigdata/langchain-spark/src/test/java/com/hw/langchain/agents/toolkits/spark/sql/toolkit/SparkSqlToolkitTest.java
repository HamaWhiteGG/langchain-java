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

    @BeforeAll
    static void setup() {
        spark = SparkSession.builder().master("local").getOrCreate();

        // set the log level to WARN
        spark.sparkContext().setLogLevel("WARN");

        spark.sql("CREATE DATABASE IF NOT EXISTS " + SCHEMA);
        spark.sql("USE " + SCHEMA);

        String csvFilePath = "../../docs/extras/modules/titanic.csv";
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(csvFilePath);

        String table = "titanic";
        df.write().saveAsTable(table);
    }

    @Test
    void testRunQuery() {
        var sparkSql = SparkSql.builder()
                .spark(spark)
                .schema(SCHEMA)
                .build().init();

        var llm = ChatOpenAI.builder()
                .model("gpt-4")
                .temperature(0)
                .build().init();

        var toolkit = new SparkSqlToolkit(sparkSql, llm);
        var agentExecutor = createSparkSqlAgent(llm, toolkit);

        // SELECT SQRT(AVG(Age)) FROM titanic
        var actual = agentExecutor.run("whats the square root of the average age?");

        var expected="The square root of the average age is approximately 5.45.";
        assertEquals(expected, actual);

        // SELECT Name FROM titanic WHERE Survived = 1 ORDER BY Age DESC LIMIT 1
//        actual = agentExecutor.run("What's the name of the oldest survived passenger?");
//        assertEquals("Barkworth, Mr. Algernon Henry Wilson", actual);
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