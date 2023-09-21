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

package com.hw.langchain.examples;

import com.hw.langchain.examples.runner.RunnableExample;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.hw.langchain.examples.utils.PrintUtils.println;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Scan the example classes annotated with `RunnableExample` annotation,
 * execute them all at once, and collect the results.
 *
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI and Pinecone calls, can be run manually.")
class ExampleRunnerTest {

    private static final String PACKAGE_NAME = "com.hw";

    @Test
    void testExecuteExamples() {
        Set<Class<?>> exampleClasses = scanExampleClasses();
        int totalTests = exampleClasses.size();
        int successfulTests = 0;
        List<String> failedClasses = new ArrayList<>();

        println("Running test suite...");
        for (Class<?> exampleClass : exampleClasses) {
            try {
                Method mainMethod = exampleClass.getDeclaredMethod("main", String[].class);
                mainMethod.invoke(null, (Object) null);
                successfulTests++;
            } catch (Exception e) {
                failedClasses.add(exampleClass.getName());
                e.printStackTrace();
            }
        }

        println("\n\nTest suite summary:");
        println("Total tests: " + totalTests);
        println("Successful tests: " + successfulTests);
        println("Failed tests: " + failedClasses.size());

        if (!failedClasses.isEmpty()) {
            println("Failed classes:");
            for (String className : failedClasses) {
                println(className);
            }
        }
        assertEquals(0, failedClasses.size(), "There should be no failed tests.");
    }

    private Set<Class<?>> scanExampleClasses() {
        Reflections reflections = new Reflections(PACKAGE_NAME);
        return reflections.getTypesAnnotatedWith(RunnableExample.class);
    }
}
