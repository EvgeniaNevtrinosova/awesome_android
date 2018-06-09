package ru.mail.park.awesome_android;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkInteraction {
    List<Recipe> getRecipes = new ArrayList<>();

    public List<Recipe> getGetRecipes() {
        return getRecipes;
    }

    public void setGetRecipes(List<Recipe> getRecipes) {
        this.getRecipes = getRecipes;
    }

    private static final Gson GSON = new GsonBuilder()
            .create();

    public void DataTransmissionAndReception(final ArrayList<String> ingredients, final Handler handler, Call<ResponseBody> post, final Activity activity) throws IOException {
        JsonObject json = new JsonObject();

        json.addProperty(activity.getResources().getString(R.string.products_property), String.valueOf(ingredients));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(activity.getResources().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Service service = retrofit.create(Service.class);

        post = service.setIngredients(json);

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ResponseBody responseBody = post.execute().body();

            if (responseBody != null) {
                String body = responseBody.string();

                if (body != null && body.equals(activity.getResources().getString(R.string.empty_response_body))) {
                    Toast.makeText(activity, R.string.empty_recipes_list, Toast.LENGTH_SHORT).show();
                    return;
                }

                setGetRecipes(parseRecipe(body));
            }
        } catch (IOException e) {
            Toast.makeText(activity, R.string.error_message, Toast.LENGTH_SHORT).show();
        }
    }

    public List<Recipe> parseRecipe(final String body) throws IOException {
        try {
            Type listType = new TypeToken<List<Recipe>>() {
            }.getType();
            return GSON.fromJson(body, listType);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }


}
