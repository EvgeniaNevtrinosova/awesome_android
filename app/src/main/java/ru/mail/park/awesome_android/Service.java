package ru.mail.park.awesome_android;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Service {
    @GET("/getRecipe")
    Call<Recipe[]> getRecipe();
}
