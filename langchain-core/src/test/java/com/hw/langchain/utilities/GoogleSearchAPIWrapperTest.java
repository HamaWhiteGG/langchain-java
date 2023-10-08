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

package com.hw.langchain.utilities;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Set the environment variable GOOGLE_API_KEY and GOOGLE_CSE_ID.
 *
 * @author HamaWhite
 */
@Disabled("Test requires Google Search Key , can be run manually.")
class GoogleSearchAPIWrapperTest {

    @Test
    void testGoogleSearch() {
        String query = "2022 USA national auto sales by brand";

        GoogleSearchAPIWrapper searchWrapper = GoogleSearchAPIWrapper.builder()
                .connectTimeout(10 * 1000)
                .build();

        List<Map<String, String>> results = searchWrapper.results(query, 10);
        assertThat(results).isNotEmpty().hasSize(10);

        assertThat(results.get(0)).isEqualTo(Map.of(
                "title", "Full-Year 2022 National Auto Sales By Brand",
                "link", "https://www.carpro.com/blog/full-year-2022-national-auto-sales-by-brand",
                "snippet",
                "Jan 12, 2023 ... Full-Year 2022 National Auto Sales By Brand ; 1. Toyota, 1,849,751 ; 2. Ford, 1,767,439 ; 3. Chevrolet, 1,502,389 ; 4. Honda, 881,201 ..."));
    }
}