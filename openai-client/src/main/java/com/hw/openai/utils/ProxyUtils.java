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

package com.hw.openai.utils;

import lombok.experimental.UtilityClass;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;

/**
 * @description: A utility class for creating HTTP proxies.
 * @author: HamaWhite
 */
@UtilityClass
public class ProxyUtils {

    /**
     * Creates an HTTP proxy object based on the provided address.
     *
     * @param address the address of the proxy server in the format 'http://host:port'
     * @return the Proxy object representing the HTTP proxy server
     */
    public Proxy http(String address) {
        // Parse the proxy server address
        URI uri = URI.create(address);
        String hostname = uri.getHost();
        int port = uri.getPort();

        // Create the Proxy object
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
    }
}
