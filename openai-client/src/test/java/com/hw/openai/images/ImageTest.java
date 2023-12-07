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

package com.hw.openai.images;

import com.hw.openai.OpenAiClientTest;
import com.hw.openai.entity.image.CreateImageReq;
import com.hw.openai.entity.image.Image;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author HamaWhite
 */
@Disabled("Test requires costly OpenAI calls, can be run manually.")
public class ImageTest extends OpenAiClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(ImageTest.class);

    @Test
    void testCreateImage() {
        CreateImageReq request = CreateImageReq.builder()
                .model("dall-e-3")
                .prompt("A cute baby sea otter")
                .n(1)
                .size("1024x1024")
                .build();

        List<Image> images = client.createImage(request).getImages();
        LOG.info("Images: {}", images);
        assertEquals(1, images.size());
        assertNotNull(images.get(0).getUrl());
    }
}
