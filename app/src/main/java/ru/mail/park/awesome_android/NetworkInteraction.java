package ru.mail.park.awesome_android;
import android.app.Activity;
import android.os.StrictMode;
import android.view.View;
import android.widget.RelativeLayout;
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

class NetworkInteraction {
    private List<Recipe> getRecipes = new ArrayList<>();

    List<Recipe> getGetRecipes() {
        return getRecipes;
    }

    private void setGetRecipes(List<Recipe> getRecipes) {
        this.getRecipes = getRecipes;
    }

    private static final Gson GSON = new GsonBuilder()
            .create();

    void DataTransmissionAndReception(final ArrayList<String> ingredients, final Activity activity, RelativeLayout loadingPanel, final View v) throws IOException {
        JsonObject json = new JsonObject();
        Call<ResponseBody> post;

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
                    post.cancel();
                    loadingPanel.setVisibility(View.GONE);
                    v.setVisibility(View.VISIBLE);
                    Toast.makeText(activity, R.string.empty_recipes_list, Toast.LENGTH_SHORT).show();
                    return;
                }

                setGetRecipes(parseRecipe(body));
            }
        } catch (IOException e) {
            post.cancel();
            loadingPanel.setVisibility(View.GONE);
            v.setVisibility(View.VISIBLE);
            Toast.makeText(activity, R.string.error_message, Toast.LENGTH_SHORT).show();
        }
    }

    private List<Recipe> parseRecipe(final String body) throws IOException {
        try {
            Type listType = new TypeToken<List<Recipe>>() {
            }.getType();
            return GSON.fromJson(body, listType);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }
}
