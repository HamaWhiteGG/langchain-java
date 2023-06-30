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

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

import static com.hw.langchain.math.utils.MathUtils.cosineSimilarity;

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
        // INDArray XArray =Nd4j.create(queryEmbedding);
        // List<INDArray> indArrayList= embeddingList.stream().map(e->Nd4j.create(e)).toList();
        // INDArray YArray=Nd4j.create(indArrayList);
        //
        //
        // INDArray XNorm = XArray.norm2(1);
        // INDArray YNorm = YArray.norm2(1);
        //
        // INDArray similarity = XArray.mmul(YArray.transpose()).div(XNorm.reshape(XRows, 1).mmul(YNorm.reshape(1,
        // YRows)));
        //
        // // Handle NaN and Inf values
        // similarity.maskedReplace(Double.NaN, 0.0);
        // similarity.maskedReplace(Double.POSITIVE_INFINITY, 0.0);
        // similarity.maskedReplace(Double.NEGATIVE_INFINITY, 0.0);
        //
        // return similarity;
        return null;
    }
}
