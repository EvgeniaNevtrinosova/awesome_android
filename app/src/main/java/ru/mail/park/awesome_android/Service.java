package ru.mail.park.awesome_android;

import com.google.gson.JsonObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Service {
    @POST("/getRecipe")
    Call<ResponseBody> setIngredients(@Body JsonObject ingredientsArray);
}
