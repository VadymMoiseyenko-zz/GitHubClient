package com.example.vadman_pc.githubclient.api;

import com.example.vadman_pc.githubclient.model.ItemResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Vadman_PC on 04.04.2017.
 */
public interface Service {
    @GET("/search/users")
    Call<ItemResponse> getItems(@Query("q") String[] params,
                                @Query("page") int pageIndex,
                                @Query("per_page") int perPage
    );

    @GET("/search/users")
    Call<ItemResponse> getItemsWithName(@Query("q") String name,
                                        @Query("a") String[] params,
                                        @Query("page") int pageIndex,
                                        @Query("per_page") int perPage
    );
}

//https://api.github.com/search/users?q=vadym&a=language;java+location:krakow&page=2&per_page=20
