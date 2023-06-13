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

package com.hw.langchain.schema;

/**
 * Exception that output parsers should raise to signify a parsing error.
 * <p>
 * This exists to differentiate parsing errors from other code or execution errors
 * that also may arise inside the output parser. OutputParserExceptions will be
 * available to catch and handle in ways to fix the parsing error, while other
 * errors will be raised.
 *
 * @author HamaWhite
 */
public class OutputParserException extends IllegalArgumentException {

    private String observation;
    private String llmOutput;
    private boolean sendToLlm;

    public OutputParserException(Object error, String observation, String llmOutput, boolean sendToLlm) {
        super(error.toString());
        if (sendToLlm) {
            if (observation == null || llmOutput == null) {
                throw new IllegalArgumentException(
                        "Arguments 'observation' & 'llm_output' are required if 'send_to_llm' is true");
            }
        }
        this.observation = observation;
        this.llmOutput = llmOutput;
        this.sendToLlm = sendToLlm;
    }

    public OutputParserException(Object error) {
        super(error.toString());
    }

    public String getObservation() {
        return observation;
    }

    public String getLlmOutput() {
        return llmOutput;
    }

    public boolean isSendToLlm() {
        return sendToLlm;
    }
}