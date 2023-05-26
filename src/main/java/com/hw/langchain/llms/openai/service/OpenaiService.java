package com.hw.langchain.llms.openai.service;


import com.hw.langchain.llms.openai.entity.request.Completion;
import com.hw.langchain.llms.openai.entity.response.CompletionResp;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @description: OpenaiService
 * @author: HamaWhite
 */
public interface OpenaiService {


    @POST("v1/completions")
    Single<CompletionResp> completion(@Body Completion completion);

}
