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

package io.github.resilience4j.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author HamaWhite
 */
public class Resilience4jRetryExample {

    private static final Logger LOG = LoggerFactory.getLogger(Resilience4jRetryExample.class);

    public static void main(String[] args) {
        int maxRetries = 6;
        String result = retryWithExponentialBackoff(maxRetries, () -> {
            double value = Math.random();
            LOG.info("Attempt: value is {}", value);
            if (value < 0.7) {
                throw new RuntimeException("Operation failed");
            }
            return "Operation succeeded";
        });
        LOG.info("Final result is {}", result);
    }

    public static <T> T retryWithExponentialBackoff(int maxRetries, Supplier<T> action) {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(maxRetries)
                .intervalFunction(
                        IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(4), 2))
                .build();
        Retry retry = Retry.of("retryWithExponential", retryConfig);

        retry.getEventPublisher().onRetry(event -> LOG.warn("Retry failed on attempt #{} with exception: {}",
                event.getNumberOfRetryAttempts(), requireNonNull(event.getLastThrowable()).getMessage()));

        return retry.executeSupplier(action);
    }
}
