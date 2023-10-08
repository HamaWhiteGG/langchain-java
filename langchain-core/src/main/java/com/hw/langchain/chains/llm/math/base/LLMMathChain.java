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

package com.hw.langchain.chains.llm.math.base;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.hw.langchain.base.language.BaseLanguageModel;
import com.hw.langchain.chains.base.Chain;
import com.hw.langchain.chains.llm.LLMChain;
import com.hw.langchain.prompts.base.BasePromptTemplate;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hw.langchain.chains.llm.math.prompt.Prompt.PROMPT;

/**
 * Chain that interprets a prompt and executes python code to do math.
 *
 * @author HamaWhite
 */
public class LLMMathChain extends Chain {

    private static final Logger LOG = LoggerFactory.getLogger(LLMMathChain.class);

    private static final Pattern TEXT_PATTERN = Pattern.compile("^```text(.*?)```", Pattern.DOTALL);

    private LLMChain llmChain;

    private final String inputKey = "question";

    private final String outputKey = "answer";

    public LLMMathChain() {
        super();
    }

    public LLMMathChain(LLMChain llmChain) {
        super();
        this.llmChain = llmChain;
    }

    public static LLMMathChain fromLLM(BaseLanguageModel llm) {
        return fromLLM(llm, PROMPT);
    }

    public static LLMMathChain fromLLM(BaseLanguageModel llm, BasePromptTemplate prompt) {
        LLMChain llmChain = new LLMChain(llm, prompt);
        return new LLMMathChain(llmChain);
    }

    @Override
    public String chainType() {
        return "llm_math_chain";
    }

    /**
     * Expect input key.
     */
    @Override
    public List<String> inputKeys() {
        return ListUtil.of(inputKey);
    }

    /**
     * Expect output key.
     */
    @Override
    public List<String> outputKeys() {
        return ListUtil.of(outputKey);
    }

    public String evaluateExpression(String expression) {
        LOG.debug("expression: {}", expression);
        PyObject result;
        try (PythonInterpreter interpreter = new PythonInterpreter()) {
            // Define local variables
            Map<String,Object> localDict = MapBuilder.create(new HashMap<String, Object>())
                    .put("pi", Math.PI)
                    .put( "e", Math.E).map();
            // Set local variables in the interpreter
            localDict.forEach(interpreter::set);
            // Evaluate the expression using jython
            result = interpreter.eval(StrUtil.strip(expression, " "));
        }
        // Convert the result to a string
        String output = result.toString();
        // Remove any leading and trailing brackets from the output
        return output.replaceAll("^\\[|\\]$", "");
    }

    public Map<String, String> processLLMResult(String llmOutput) {
        llmOutput = StrUtil.strip(llmOutput, " ");
        Matcher textMatcher = TEXT_PATTERN.matcher(llmOutput);
        String answer;
        if (textMatcher.find()) {
            String expression = textMatcher.group(1);
            String output = evaluateExpression(expression);
            answer = "Answer: " + output;
        } else if (llmOutput.startsWith("Answer:")) {
            answer = llmOutput;
        } else if (llmOutput.contains("Answer:")) {
            answer = "Answer: " + llmOutput.split("Answer:")[1];
        } else {
            throw new IllegalArgumentException("unknown format from LLM: " + llmOutput);
        }
        return MapUtil.of(this.outputKey, answer);
    }

    @Override
    public Map<String, String> innerCall(Map<String, Object> inputs) {
        Map<String, Object> kwargs = MapBuilder.create(new HashMap<String, Object>())
                .put("question", inputs.get(inputKey))
                .put("stop", ListUtil.of("```output")).map();
        String llmOutput = llmChain.predict(kwargs);
        return processLLMResult(llmOutput);
    }
}
