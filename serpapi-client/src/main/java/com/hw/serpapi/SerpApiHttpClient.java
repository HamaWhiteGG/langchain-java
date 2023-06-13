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

package com.hw.serpapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * HTTPS search for Serp API
 *
 * @author HamaWhite
 */
public class SerpApiHttpClient {

    private int httpConnectionTimeout;

    private int httpReadTimeout;

    /**
     * current API version
     */
    public static String VERSION = "2.0.3";

    /**
     * backend service
     */
    public static String BACKEND = "https://serpapi.com";

    /**
     * initialize gson
     */
    private static Gson gson = new Gson();

    /**
     * current backend HTTP path
     */
    public String path;

    /***
     * Constructor
     * @param path HTTP url path
     */
    public SerpApiHttpClient(String path) {
        this.path = path;
    }

    /***
     * Build URL
     *
     * @param path url end point
     * @param parameter search parameter map like: { "q": "coffee", "location": "Austin, TX"}
     * @return httpUrlConnection
     * @throws SerpApiSearchException wraps error message
     */
    protected HttpURLConnection buildConnection(String path, Map<String, String> parameter)
            throws SerpApiSearchException {
        HttpURLConnection con;
        try {
            allowHTTPS();
            String query = ParameterStringBuilder.getParamsString(parameter);
            URL url = new URL(BACKEND + path + "?" + query);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            throw new SerpApiSearchException(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new SerpApiSearchException(e);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            throw new SerpApiSearchException(e);
        }

        String outputFormat = parameter.get("output");
        if (outputFormat == null) {
            if (path.startsWith("/search?")) {
                throw new SerpApiSearchException("output format must be defined: " + path);
            }
        } else if (outputFormat.startsWith("json")) {
            con.setRequestProperty("Content-Type", "application/json");
        }

        con.setConnectTimeout(getHttpConnectionTimeout());
        con.setReadTimeout(getHttpReadTimeout());

        con.setDoOutput(true);
        return con;
    }

    private void allowHTTPS() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts;
        trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

        }};

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        /*
         * end of the fix
         */
    }

    /***
     * Get results
     *
     * @param parameter user search parameters
     * @return http response body
     * @throws SerpApiSearchException wraps error message
     */
    public String getResults(Map<String, String> parameter) throws SerpApiSearchException {
        HttpURLConnection con = buildConnection(this.path, parameter);

        // Get HTTP status
        int statusCode = -1;
        // Hold response stream
        InputStream is = null;
        // Read buffer
        BufferedReader in = null;
        try {
            statusCode = con.getResponseCode();

            if (statusCode == 200) {
                is = con.getInputStream();
            } else {
                is = con.getErrorStream();
            }

            Reader reader = new InputStreamReader(is);
            in = new BufferedReader(reader);
        } catch (IOException e) {
            throw new SerpApiSearchException(e);
        }

        String inputLine;
        StringBuffer content = new StringBuffer();
        try {
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new SerpApiSearchException(e);
        }

        // Disconnect
        con.disconnect();

        if (statusCode != 200) {
            triggerSerpApiClientException(content.toString());
        }
        return content.toString();
    }

    /**
     * trigger a exception on error
     * @param content raw JSON response from serpapi.com
     * @throws SerpApiSearchException wraps error message
     */
    protected void triggerSerpApiClientException(String content) throws SerpApiSearchException {
        String errorMessage;
        try {
            JsonObject element = gson.fromJson(content, JsonObject.class);
            errorMessage = element.get("error").getAsString();
        } catch (Exception e) {
            throw new AssertionError("invalid response format: " + content);
        }
        throw new SerpApiSearchException(errorMessage);
    }

    /**
     * @return current HTTP connection timeout
     */
    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    /**
     * @param httpConnectionTimeout set HTTP connection timeout
     */
    public void setHttpConnectionTimeout(int httpConnectionTimeout) {
        this.httpConnectionTimeout = httpConnectionTimeout;
    }

    /**
     * @return current HTTP read timeout
     */
    public int getHttpReadTimeout() {
        return httpReadTimeout;
    }

    /**
     * @param httpReadTimeout set HTTP read timeout
     */
    public void setHttpReadTimeout(int httpReadTimeout) {
        this.httpReadTimeout = httpReadTimeout;
    }

}
