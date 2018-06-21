package ru.mail.park.awesome_android;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Service {
    @POST("/getRecipe")
    Call<ResponseBody> setIngredients(@Body RequestBody ingredientsArray);
}