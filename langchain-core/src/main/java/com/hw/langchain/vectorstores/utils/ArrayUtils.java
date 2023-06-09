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

import java.lang.reflect.Array;
import java.util.List;

/**
 * @author HamaWhite
 */
public class ArrayUtils {

    private ArrayUtils() {
    }

    public static <T> List<List<T>> arrayToList(T[][] array) {
        List<List<T>> result = Lists.newArrayListWithCapacity(array.length);
        for (T[] subArray : array) {
            result.add(Lists.newArrayList(subArray));
        }
        return result;
    }

    public static <T> T[][] listToArray(List<List<T>> list) {
        Class<?> elementType = list.get(0).get(0).getClass();
        T[][] result = newArray(elementType, list.size(), list.get(0).size());
        for (int i = 0; i < list.size(); i++) {
            List<T> subList = list.get(i);
            result[i] = subList.toArray(newArray(elementType, subList.size()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[][] newArray(Class<?> elementType, int rows, int columns) {
        return (T[][]) Array.newInstance(elementType, rows, columns);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] newArray(Class<?> elementType, int length) {
        return (T[]) Array.newInstance(elementType, length);
    }
}
