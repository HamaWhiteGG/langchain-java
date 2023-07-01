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

package com.hw.langchain.vectorstores.utils;

import com.google.common.collect.Lists;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.util.ArrayList;
import java.util.List;

import static com.hw.langchain.math.utils.MathUtils.cosineSimilarity;
import static com.hw.langchain.vectorstores.utils.Nd4jUtils.createFromList;
import static java.lang.Float.NEGATIVE_INFINITY;

/**
 * Utility functions for working with vectors and vectorStores.
 *
 * @author HamaWhite
 */
public class Utils {

    private Utils() {
    }

    /**
     * Calculate maximal marginal relevance.
     */
    public static List<Integer> maximalMarginalRelevance(INDArray queryEmbedding, List<List<Float>> embeddingList,
            int k, float lambdaMult) {
        if (Math.min(k, embeddingList.size()) <= 0) {
            return new ArrayList<>();
        }
        if (queryEmbedding.rank() == 1) {
            queryEmbedding = Nd4j.expandDims(queryEmbedding, 0);
        }
        INDArray similarityToQuery = cosineSimilarity(queryEmbedding, embeddingList).getRow(0);
        int mostSimilar = Nd4j.argMax(similarityToQuery).getInt(0);
        List<Integer> idxs = Lists.newArrayList(mostSimilar);
        INDArray selected = createFromList(embeddingList.get(mostSimilar));

        while (idxs.size() < Math.min(k, embeddingList.size())) {
            float bestScore = NEGATIVE_INFINITY;
            int idxToAdd = -1;
            INDArray similarityToSelected = cosineSimilarity(embeddingList, selected);
            for (int i = 0; i < similarityToQuery.columns(); i++) {
                if (idxs.contains(i)) {
                    continue;
                }
                float redundantScore = Transforms.max(similarityToSelected.getRow(i), 0).getFloat(0);
                float equationScore = lambdaMult * similarityToQuery.getFloat(i) - (1 - lambdaMult) * redundantScore;
                if (equationScore > bestScore) {
                    bestScore = equationScore;
                    idxToAdd = i;
                }
            }
            idxs.add(idxToAdd);
            selected = Nd4j.vstack(selected, createFromList(embeddingList.get(idxToAdd)));
        }
        return idxs;
    }
}
