package com.hw.langchain.llms.openai;


import com.hw.langchain.llms.openai.entity.request.Completion;
import com.hw.langchain.llms.openai.service.OpenaiService;
import lombok.Builder;
import lombok.Data;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

/**
 * @description: OpenaiClient
 * @author: HamaWhite
 */
@Data
@Builder
public class OpenaiClient {

    private static final String BASE_URL = "https://api.openai.com/";

    private String apiKey;

    private OpenaiService service;

    public OpenaiClient init() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey);
                Request newRequest = requestBuilder.build();
                return chain.proceed(newRequest);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build();

        this.service = retrofit.create(OpenaiService.class);
        return this;
    }


    public String completion(Completion completion) {
        return service.completion(completion).blockingGet().getChoices().get(0).getText();
    }

    public static void main(String[] args) {
        OpenaiClient openai= OpenaiClient.builder()
                .apiKey("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")
                .build()
                .init();

        Completion completion=Completion.builder()
                .model("text-davinci-003")
                .prompt("Say this is a test")
                .maxTokens(700)
                .temperature(0)
                .build();

        System.out.println(openai.completion(completion));

    }
}
