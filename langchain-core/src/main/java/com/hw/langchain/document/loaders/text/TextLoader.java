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

package com.hw.langchain.document.loaders.text;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.map.MapUtil;
import com.hw.langchain.document.loaders.base.BaseLoader;
import com.hw.langchain.document.loaders.helpers.FileEncoding;
import com.hw.langchain.exception.LangChainException;
import com.hw.langchain.schema.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.hw.langchain.document.loaders.helpers.Helpers.detectFileEncodings;

/**
 * Load text files.
 *
 * @author HamaWhite
 */
public class TextLoader extends BaseLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TextLoader.class);

    private final String filePath;

    private final Charset encoding;

    private final boolean autodetectEncoding;

    public TextLoader(String filePath) {
        this(filePath, Charset.defaultCharset(), false);
    }

    /**
     * Load text files.
     *
     * @param filePath           Path to the file to load.
     * @param encoding           File encoding to use. If `null`, the file will be loaded with the default system encoding.
     * @param autodetectEncoding Whether to try to autodetect the file encoding if the specified encoding fails.
     */
    public TextLoader(String filePath, Charset encoding, boolean autodetectEncoding) {
        this.filePath = filePath;
        this.encoding = encoding;
        this.autodetectEncoding = autodetectEncoding;
    }

    /**
     * Load from file path.
     */
    @Override
    public List<Document> load() {
        String text;
        try {
            text = FileUtil.readString(FileSystems.getDefault().getPath(filePath).toFile(), encoding);
        } catch (Exception e) {
            throw new LangChainException(errorMessage(filePath), e);
        }
        Map<String, Object> metadata = MapUtil.of("source", filePath);
        return ListUtil.of(new Document(text, metadata));
    }

    private String loadWithDetectedEncoding(String filePath) {
        try {
            FileEncoding detected = detectFileEncodings(filePath);
            LOG.debug("Trying encoding: {}", detected.getEncoding());
            return FileUtil.readString(FileSystems.getDefault().getPath(filePath).toFile(), detected.getEncoding());
        } catch (IOException e) {
            throw new LangChainException(errorMessage(filePath), e);
        }
    }
}
