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

package com.hw.langchain.output.parsers;

/**
 * @author HamaWhite
 */
public class FormatInstructions {

    public static String STRUCTURED_FORMAT_INSTRUCTIONS =
            """
                    The output should be a markdown code snippet formatted in the following schema, including the leading and trailing "```json" and "```":

                    ```json
                    {{
                    {format}
                    }}
                    ```""";

    public static String PYDANTIC_FORMAT_INSTRUCTIONS =
            """
                    The output should be formatted as a JSON instance that conforms to the JSON schema below.

                    As an example, for the schema {{"properties": {{"foo": {{"title": "Foo", "description": "a list of strings", "type": "array", "items": {{"type": "string"}}}}}}, "required": ["foo"]}}}}
                    the object {{"foo": ["bar", "baz"]}} is a well-formatted instance of the schema. The object {{"properties": {{"foo": ["bar", "baz"]}}}} is not well-formatted.

                    Here is the output schema:
                    ```
                    {schema}
                    ```""";
}
