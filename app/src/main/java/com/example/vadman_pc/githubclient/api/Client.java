package com.example.vadman_pc.githubclient.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vadman_PC on 04.04.2017.
 */
public class Client {
    public static final String BASE_URL = "https://api.github.com";
    public static Retrofit retrofit = null;

    public static  Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
