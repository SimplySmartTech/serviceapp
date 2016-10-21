package com.simplysmart.service.config;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(ApplicationConstant.SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit().create(serviceClass);
    }

    public static Retrofit retrofit() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", ApplicationConstant.CONTENT_TYPE)
                        .header("Accept", ApplicationConstant.ACCEPT_TYPE)
                        .header("X-Api-Key", GlobalData.getInstance().getApiKey() != null ? GlobalData.getInstance().getApiKey() : "")
                        .header("Authorization", GlobalData.getInstance().getAuthToken() != null ? GlobalData.getInstance().getAuthToken() : "")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);

        OkHttpClient client = httpClient.build();
        return builder.client(client).build();
    }

}