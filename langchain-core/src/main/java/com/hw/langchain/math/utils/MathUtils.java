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

package com.hw.langchain.math.utils;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.List;

import static com.hw.langchain.vectorstores.utils.ArrayUtils.listToArray;
import static org.nd4j.linalg.ops.transforms.Transforms.allCosineSimilarities;

/**
 * Math utils.
 *
 * @author HamaWhite
 */
public class MathUtils {

    private MathUtils() {
    }

    /**
     * Row-wise cosine similarity between two equal-width matrices.
     */
    public static INDArray cosineSimilarity(List<List<Float>> X, INDArray yArray) {
        return cosineSimilarity(Nd4j.createFromArray(listToArray(X)), yArray);
    }

    /**
     * Row-wise cosine similarity between two equal-width matrices.
     */
    public static INDArray cosineSimilarity(INDArray xArray, List<List<Float>> Y) {
        return cosineSimilarity(xArray, Nd4j.createFromArray(listToArray(Y)));
    }

    /**
     * Row-wise cosine similarity between two equal-width matrices.
     */
    public static INDArray cosineSimilarity(INDArray xArray, INDArray yArray) {
        if (xArray.isEmpty() || yArray.isEmpty()) {
            return Nd4j.create(new float[0][0]);
        }
        if (xArray.shape()[1] != yArray.shape()[1]) {
            throw new IllegalArgumentException(
                    String.format("Number of columns in X and Y must be the same. X has shape %s and Y has shape %s.",
                            Arrays.toString(xArray.shape()), Arrays.toString(yArray.shape())));
        }
        return allCosineSimilarities(xArray, yArray, xArray.rank() - 1);
    }
}
