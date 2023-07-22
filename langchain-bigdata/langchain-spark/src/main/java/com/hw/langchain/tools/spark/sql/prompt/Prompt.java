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

package com.hw.langchain.tools.spark.sql.prompt;

/**
 * @author HamaWhite
 */
public class Prompt {

    private Prompt() {
        // private constructor to hide the implicit public one
        throw new IllegalStateException("Utility class");
    }

    public static final String QUERY_CHECKER =
            """
                    {query}
                    Double check the Spark SQL query above for common mistakes, including:
                    - Using NOT IN with NULL values
                    - Using UNION when UNION ALL should have been used
                    - Using BETWEEN for exclusive ranges
                    - Data type mismatch in predicates
                    - Properly quoting identifiers
                    - Using the correct number of arguments for functions
                    - Casting to the correct data type
                    - Using the proper columns for joins

                    If there are any of the above mistakes, rewrite the query. If there are no mistakes, just reproduce the original query.""";

}
