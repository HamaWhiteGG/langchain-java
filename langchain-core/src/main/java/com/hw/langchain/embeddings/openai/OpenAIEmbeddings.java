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

package com.hw.langchain.embeddings.openai;

import cn.hutool.core.collection.ListUtil;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.hw.langchain.embeddings.base.Embeddings;
import com.hw.langchain.exception.LangChainException;
import com.hw.openai.OpenAiClient;
import com.hw.openai.common.OpenaiApiType;
import com.hw.openai.entity.embeddings.Embedding;
import com.hw.openai.entity.embeddings.EmbeddingResp;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.hw.langchain.utils.Utils.getOrEnvOrDefault;

/**
 * Wrapper around OpenAI embedding models.
 * <p>
 * To use, you should have the environment variable OPENAI_API_KEY set with your API key or pass it as a
 * named parameter to the constructor.
 *
 * @author HamaWhite
 */
@Builder
@AllArgsConstructor
public class OpenAIEmbeddings implements Embeddings {

    private OpenAiClient client;

    @Builder.Default
    private String model = "text-embedding-ada-002";

    private String openaiApiBase;

    /**
     * To support explicit proxy for OpenAI.
     */
    private String openaiProxy;

    @Builder.Default
    private int embeddingCtxLength = 8191;

    private String openaiApiKey;

    @Builder.Default
    private OpenaiApiType openaiApiType = OpenaiApiType.OPENAI;

    private String openaiApiVersion;

    protected String openaiOrganization;

    /**
     * Maximum number of texts to embed in each batch
     */
    @Builder.Default
    private int chunkSize = 1000;

    /**
     * Maximum number of retries to make when generating.
     */
    @Builder.Default
    private int maxRetries = 6;

    /**
     * Timeout for requests to OpenAI completion API. Default is 16 seconds.
     */
    @Builder.Default
    protected long requestTimeout = 16;

    /**
     * Validate parameters and init client
     */
    public OpenAIEmbeddings init() {
        openaiApiKey = getOrEnvOrDefault(openaiApiKey, "OPENAI_API_KEY");
        openaiApiBase = getOrEnvOrDefault(openaiApiBase, "OPENAI_API_BASE", "");
        openaiProxy = getOrEnvOrDefault(openaiProxy, "OPENAI_PROXY", "");
        openaiOrganization = getOrEnvOrDefault(openaiOrganization, "OPENAI_ORGANIZATION", "");
        openaiApiVersion = getOrEnvOrDefault(openaiApiVersion, "OPENAI_API_VERSION", "");

        this.client = OpenAiClient.builder()
                .openaiApiBase(openaiApiBase)
                .openaiApiKey(openaiApiKey)
                .openaiApiVersion(openaiApiVersion)
                .openaiApiType(openaiApiType)
                .openaiOrganization(openaiOrganization)
                .openaiProxy(openaiProxy)
                .requestTimeout(requestTimeout)
                .build()
                .init();
        return this;
    }

    /**
     * <a href="https://github.com/openai/openai-cookbook/blob/main/examples/Embedding_long_inputs.ipynb">Embedding texts that are longer than the model's maximum context length</a>
     */
    private List<List<Float>> getLenSafeEmbeddings(List<String> texts) {
        List<List<Float>> embeddings = new ArrayList<>(texts.size());

        List<List<Integer>> tokens = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        Encoding encoding = Encodings.newDefaultEncodingRegistry()
                .getEncodingForModel(model)
                .orElseThrow(() -> new LangChainException("Encoding not found."));

        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            if (model.endsWith("001")) {
                // See https://github.com/openai/openai-python/issues/418#issuecomment-1525939500
                // replace newlines, which can negatively affect performance.
                text = text.replace("\n", " ");
            }
            List<Integer> token = encoding.encode(text);
            for (int j = 0; j < token.size(); j += embeddingCtxLength) {
                tokens.add(token.subList(j, Math.min(j + embeddingCtxLength, token.size())));
                indices.add(i);
            }
        }

        List<List<Float>> batchedEmbeddings = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i += chunkSize) {
            List<?> input = tokens.subList(i, Math.min(i + chunkSize, tokens.size()));
            EmbeddingResp response = embedWithRetry(input);
            response.getData().forEach(result -> batchedEmbeddings.add(result.getEmbedding()));
        }

        List<? extends List<List<Float>>> results = IntStream.range(0, texts.size())
                .mapToObj(i -> new ArrayList<List<Float>>())
                .collect(Collectors.toList());
        List<? extends List<Integer>> numTokensInBatch = IntStream.range(0, texts.size())
                .mapToObj(i -> new ArrayList<Integer>())
                .collect(Collectors.toList());
        for (int i = 0; i < indices.size(); i++) {
            int index = indices.get(i);
            results.get(index).add(batchedEmbeddings.get(i));
            numTokensInBatch.get(index).add(tokens.get(i).size());
        }

        for (int i = 0; i < texts.size(); i++) {
            INDArray average;
            try (INDArray resultArray =
                    Nd4j.create(results.get(i).stream().map(Floats::toArray).toArray(float[][]::new))) {
                INDArray weightsArray = Nd4j.create(Doubles.toArray(numTokensInBatch.get(i)));
                average = resultArray.mulRowVector(weightsArray).sum(0).div(weightsArray.sum(0));
            }
            INDArray normalizedAverage = average.div(average.norm2Number());
            embeddings.add(Floats.asList(normalizedAverage.toFloatVector()));
        }
        return embeddings;
    }

    /**
     * Call out to OpenAI's embedding endpoint.
     */
    public List<Float> embeddingFunc(String text) {
        if (text.length() > embeddingCtxLength) {
            return getLenSafeEmbeddings(ListUtil.of(text)).get(0);
        } else {
            if (model.endsWith("001")) {
                // See: https://github.com/openai/openai-python/issues/418#issuecomment-1525939500
                // replace newlines, which can negatively affect performance.
                text = text.replace("\n", " ");
            }
            return embedWithRetry(ListUtil.of(text)).getData().get(0).getEmbedding();
        }
    }

    /**
     * Call out to OpenAI's embedding endpoint for embedding search docs.
     *
     * @param texts The list of texts to embed.
     * @return List of embeddings, one for each text.
     */
    @Override
    public List<List<Float>> embedDocuments(List<String> texts) {
        // NOTE: to keep things simple, we assume the list may contain texts longer
        // than the maximum context and use length-safe embedding function.
        return this.getLenSafeEmbeddings(texts);
    }

    /**
     * Call out to OpenAI's embedding endpoint for embedding query text.
     *
     * @param text The text to embed.
     * @return Embedding for the text.
     */
    @Override
    public List<Float> embedQuery(String text) {
        return embeddingFunc(text);
    }

    public EmbeddingResp embedWithRetry(List<?> input) {
        Embedding embedding = Embedding.builder()
                .model(model)
                .input(input)
                .build();
        return client.createEmbedding(embedding);
    }
}
