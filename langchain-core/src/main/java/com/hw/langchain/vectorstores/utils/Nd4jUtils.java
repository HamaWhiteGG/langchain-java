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

import cn.hutool.core.collection.ListUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

import static com.hw.langchain.vectorstores.utils.ArrayUtils.listToArray;

/**
 * @author HamaWhite
 */
public class Nd4jUtils {

    private Nd4jUtils() {
    }

    public static INDArray createFromList(List<Float> list) {
        Float[][] array = listToArray(ListUtil.of(list));
        return Nd4j.createFromArray(array);
    }
}
