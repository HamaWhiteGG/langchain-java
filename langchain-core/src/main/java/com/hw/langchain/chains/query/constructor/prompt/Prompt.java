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

package com.hw.langchain.chains.query.constructor.prompt;

import com.hw.langchain.prompts.prompt.PromptTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author HamaWhite
 */
public class Prompt {

    private Prompt() {
    }

    private static final String SONG_DATA_SOURCE = """
            \
            ```json
            {
                "content": "Lyrics of a song",
                "attributes": {
                    "artist": {
                        "type": "string",
                        "description": "Name of the song artist"
                    },
                    "length": {
                        "type": "integer",
                        "description": "Length of the song in seconds"
                    },
                    "genre": {
                        "type": "string",
                        "description": "The song genre, one of \"pop\", \"rock\" or \"rap\""
                    }
                }
            }
            ```\
            """.replace(
            "{", "{{").replace(
                    "}", "}}");

    private static final String FULL_ANSWER = """
            \
            ```json
            {{
                "query": "teenager love",
                "filter": "and(or(eq(\\"artist\\", \\"Taylor Swift\\"), eq(\\"artist\\", \\"Katy Perry\\")), \
            lt(\\"length\\", 180), eq(\\"genre\\", \\"pop\\"))"
            }}
            ```\
            """;

    private static final String NO_FILTER_ANSWER = """
            \
            ```json
            {{
                "query": "",
                "filter": "NO_FILTER"
            }}
            ```\
            """;

    private static final String WITH_LIMIT_ANSWER = """
            \
            ```json
            {{
                "query": "love",
                "filter": "NO_FILTER",
                "limit": 2
            }}
            ```\
            """;

    public static final List<Map<String, Object>> DEFAULT_EXAMPLES = List.of(
            createExample(1, SONG_DATA_SOURCE,
                    "What are songs by Taylor Swift or Katy Perry about teenage romance under 3 minutes long in the dance pop genre",
                    FULL_ANSWER),
            createExample(2, SONG_DATA_SOURCE, "What are songs that were not published on Spotify", NO_FILTER_ANSWER));

    public static final List<Map<String, Object>> EXAMPLES_WITH_LIMIT = List.of(
            createExample(1, SONG_DATA_SOURCE,
                    "What are songs by Taylor Swift or Katy Perry about teenage romance under 3 minutes long in the dance pop genre",
                    FULL_ANSWER),
            createExample(2, SONG_DATA_SOURCE, "What are songs that were not published on Spotify", NO_FILTER_ANSWER),
            createExample(3, SONG_DATA_SOURCE, "What are three songs about love", WITH_LIMIT_ANSWER));

    private static Map<String, Object> createExample(int i, String dataSource, String userQuery,
            String structuredRequest) {
        return Map.of(
                "i", i,
                "data_source", dataSource,
                "user_query", userQuery,
                "structured_request", structuredRequest);
    }

    private static final String EXAMPLE_PROMPT_TEMPLATE = """
            \
            << Example {i}. >>
            Data Source:
            {data_source}

            User Query:
            {user_query}

            Structured Request:
            {structured_request}
            """;

    public static final PromptTemplate EXAMPLE_PROMPT = new PromptTemplate(
            List.of("i", "data_source", "user_query", "structured_request"), EXAMPLE_PROMPT_TEMPLATE);

    public static final String DEFAULT_SCHEMA =
            """
                    \
                    << Structured Request Schema >>
                    When responding use a markdown code snippet with a JSON object formatted in the \
                    following schema:

                    ```json
                    {{{{
                        "query": string \\ text string to compare to document contents
                        "filter": string \\ logical condition statement for filtering documents
                    }}}}
                    ```

                    The query string should contain only text that is expected to match the contents of \
                    documents. Any conditions in the filter should not be mentioned in the query as well.

                    A logical condition statement is composed of one or more comparison and logical \
                    operation statements.

                    A comparison statement takes the form: `comp(attr, val)`:
                    - `comp` ({allowed_comparators}): comparator
                    - `attr` (string):  name of attribute to apply the comparison to
                    - `val` (string): is the comparison value

                    A logical operation statement takes the form `op(statement1, statement2, ...)`:
                    - `op` ({allowed_operators}): logical operator
                    - `statement1`, `statement2`, ... (comparison statements or logical operation \
                    statements): one or more statements to apply the operation to

                    Make sure that you only use the comparators and logical operators listed above and \
                    no others.
                    Make sure that filters only refer to attributes that exist in the data source.
                    Make sure that filters only use the attributed names with its function names if there are functions applied on them.
                    Make sure that filters only use format `YYYY-MM-DD` when handling timestamp data typed values.
                    Make sure that filters take into account the descriptions of attributes and only make \
                    comparisons that are feasible given the type of data being stored.
                    Make sure that filters are only used as needed. If there are no filters that should be \
                    applied return "NO_FILTER" for the filter value.\
                    """;

    public static final String SCHEMA_WITH_LIMIT =
            """
                    \
                    << Structured Request Schema >>
                    When responding use a markdown code snippet with a JSON object formatted in the \
                    following schema:

                    ```json
                    {{{{
                        "query": string \\ text string to compare to document contents
                        "filter": string \\ logical condition statement for filtering documents
                        "limit": int \\ the number of documents to retrieve
                    }}}}
                    ```

                    The query string should contain only text that is expected to match the contents of \
                    documents. Any conditions in the filter should not be mentioned in the query as well.

                    A logical condition statement is composed of one or more comparison and logical \
                    operation statements.

                    A comparison statement takes the form: `comp(attr, val)`:
                    - `comp` ({allowed_comparators}): comparator
                    - `attr` (string):  name of attribute to apply the comparison to
                    - `val` (string): is the comparison value

                    A logical operation statement takes the form `op(statement1, statement2, ...)`:
                    - `op` ({allowed_operators}): logical operator
                    - `statement1`, `statement2`, ... (comparison statements or logical operation \
                    statements): one or more statements to apply the operation to

                    Make sure that you only use the comparators and logical operators listed above and \
                    no others.
                    Make sure that filters only refer to attributes that exist in the data source.
                    Make sure that filters only use the attributed names with its function names if there are functions applied on them.
                    Make sure that filters only use format `YYYY-MM-DD` when handling timestamp data typed values.
                    Make sure that filters take into account the descriptions of attributes and only make \
                    comparisons that are feasible given the type of data being stored.
                    Make sure that filters are only used as needed. If there are no filters that should be \
                    applied return "NO_FILTER" for the filter value.
                    Make sure the `limit` is always an int value. It is an optional parameter so leave it blank if it is does not make sense.
                    """;

    public static final String DEFAULT_PREFIX = """
            \
            Your goal is to structure the user's query to match the request schema provided below.

            {schema}\
            """;

    public static final String DEFAULT_SUFFIX = """
            \
            << Example {i}. >>
            Data Source:
            ```json
            {{{{
                "content": "{content}",
                "attributes": {attributes}
            }}}}
            ```

            User Query:
            {{query}}

            Structured Request:
            """;

}
