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

import io.github.resilience4j.retry.IntervalFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

import java.time.Duration;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author HamaWhite
 */
public class Resilience4jRetryUtils {

    private Resilience4jRetryUtils() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(Resilience4jRetryUtils.class);

    public static <T> T retryWithExponentialBackoff(int maxRetries, Supplier<T> action) {
        return retryWithExponentialBackoff(maxRetries, action, Duration.ofSeconds(4), 2, Duration.ofSeconds(16));
    }

    public static <T> T retryWithExponentialBackoff(int maxRetries, Supplier<T> action, Duration initialInterval,
            double multiplier, Duration maxInterval) {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(maxRetries)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(initialInterval, multiplier))
                .build();
        Retry retry = Retry.of("retryWithExponentialBackoff", retryConfig);

        retry.getEventPublisher().onRetry(event -> LOG.warn("Retry failed on attempt #{} with exception: {}",
                event.getNumberOfRetryAttempts(), requireNonNull(event.getLastThrowable()).getMessage()));

        return retry.executeSupplier(action);
    }
}
