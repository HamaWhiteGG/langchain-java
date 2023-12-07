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

package com.hw.openai.entity.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @author HamaWhite
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateImageReq {

    /**
     * A text description of the desired image(s).
     * The maximum length is 1000 characters for dall-e-2 and 4000 characters for dall-e-3.
     */
    @NotBlank
    private String prompt;

    /**
     * The model to use for image generation.
     */
    @Builder.Default
    private String model = "dall-e-2";

    /**
     * The number of images to generate. Must be between 1 and 10. For dall-e-3, only n=1 is supported.
     */
    @Builder.Default
    private Integer n = 1;

    /**
     * The quality of the image that will be generated. hd creates images with finer details and greater consistency
     * across the image. This param is only supported for dall-e-3.
     */
    @Builder.Default
    private String quality = "standard";

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json.
     */
    @JsonProperty("response_format")
    @Builder.Default
    private ImageRespFormat responseFormat = ImageRespFormat.URL;

    /**
     * The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024 for dall-e-2.
     * Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models.
     */
    @Builder.Default
    private String size = "1024x1024";

    /**
     * The style of the generated images. Must be one of vivid or natural.
     * Vivid causes the model to lean towards generating hyper-real and dramatic images.
     * Natural causes the model to produce more natural, less hyper-real looking images.
     * This param is only supported for dall-e-3.
     */
    @Builder.Default
    @JsonProperty("style")
    private ImageStyle imageStyle = ImageStyle.NATURAL;

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    private String user;
}
