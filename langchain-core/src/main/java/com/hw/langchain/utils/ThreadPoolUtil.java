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

package com.hw.langchain.utils;

import java.util.concurrent.*;

/**
 * @author HamaWhite
 */
public class ThreadPoolUtil {

    private ThreadPoolUtil() {
    }

    public static ExecutorService createThreadPool(int maxConcurrency) {
        int corePoolSize = Math.min(maxConcurrency, Runtime.getRuntime().availableProcessors());
        long keepAliveTime = 0L;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        return new ThreadPoolExecutor(corePoolSize, maxConcurrency, keepAliveTime, unit, workQueue, threadFactory,
                handler);
    }
}
