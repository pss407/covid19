package com.example.covid.retrofit;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ServiceAPI {
    @GET("openapi/service/rest/Covid19/getCovid19InfStateJson")
    Call<DTO> response(
            @QueryMap HashMap<String, String> query
            );
}
