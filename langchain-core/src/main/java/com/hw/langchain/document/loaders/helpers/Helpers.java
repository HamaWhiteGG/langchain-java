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

package com.hw.langchain.document.loaders.helpers;

import org.python.icu.text.CharsetDetector;
import org.python.icu.text.CharsetMatch;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author HamaWhite
 */
public class Helpers {

    /**
     * Try to detect the file encoding.
     */
    public static FileEncoding detectFileEncodings(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);

        CharsetDetector detector = new CharsetDetector();
        detector.setText(data);
        CharsetMatch match = detector.detect();

        Charset charset = Charset.forName(match.getName());
        return new FileEncoding(charset, match.getConfidence(), match.getLanguage());
    }
}
