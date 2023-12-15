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

package com.hw.openai.function;

import com.hw.openai.OpenAiClientTest;
import com.hw.openai.entity.chat.*;
import com.hw.openai.function.entity.Weather;
import com.hw.openai.function.entity.WeatherResponse;
import com.hw.openai.utils.ChatParameterUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
class ChatFunctionTest extends OpenAiClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(ChatFunctionTest.class);

    private final String functionName = "get_current_weather";

    @BeforeEach
    void setUp() {
        // register function
        FunctionExecutor.register(functionName, this::getCurrentWeather);
    }

    WeatherResponse getCurrentWeather(Weather weather) {
        // mock function
        return WeatherResponse.builder()
                .location(weather.location())
                .unit(weather.unit())
                .temperature(new Random().nextInt(50))
                .description("sunny")
                .build();
    }

    @Test
    void testChatFunction() {
        ChatFunction chatFunction = ChatFunction.builder()
                .name(functionName)
                .description("Get the current weather in a given location")
                .parameters(ChatParameterUtils.generate(Weather.class))
                .build();

        ChatMessage message = ChatMessage.of("What is the weather like in Boston?");

        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model("gpt-4")
                .temperature(0)
                .messages(List.of(message))
                .tools(List.of(new Tool(chatFunction)))
                .toolChoice("auto")
                .build();

        ChatCompletionResp response = client.createChatCompletion(chatCompletion);
        ChatChoice chatChoice = response.getChoices().get(0);
        LOG.info("result: {}", chatChoice);
        assertThat(chatChoice).isNotNull();
        assertEquals("tool_calls", chatChoice.getFinishReason());

        FunctionCall function = chatChoice.getMessage().getToolCalls().get(0).getFunction();
        // name=get_current_weather, arguments={ "location": "Boston" }
        assertEquals(functionName, function.getName());

        String expectedArguments = """
                {
                  "location": "Boston, MA"
                }""";
        assertEquals(expectedArguments, function.getArguments());

        // execute function
        WeatherResponse weatherResponse =
                FunctionExecutor.execute(function.getName(), Weather.class, function.getArguments());
        LOG.info("result: {}", weatherResponse);
        assertThat(weatherResponse).isNotNull();
    }
}
