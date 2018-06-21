package ru.mail.park.awesome_android;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;


public class Api {

    private static final Api INSTANCE = new Api();

    private static final Gson GSON = new GsonBuilder()
            .create();

    JsonObject json = new JsonObject();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final Service service;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Api() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://195.133.1.169:8082/")
                .build();
        service = retrofit.create(Service.class);
    }

    public static Api getInstance() {
        return INSTANCE;
    }

    public ListenerHandler<OnRecipesGetListener> getRecipes(final ArrayList<String> ingredients, final OnRecipesGetListener listener) {
        final ListenerHandler<OnRecipesGetListener> handler = new ListenerHandler<>(listener);
        json.addProperty("products", String.valueOf(ingredients));

        final RequestBody body = RequestBody.create(JSON, json.toString());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service.setIngredients(body).execute();
                    if (response.code() != 200) {
                        throw new IOException("HTTP code " + response.code());
                    }
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseRecipe(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    private void invokeSuccess(final ListenerHandler<OnRecipesGetListener> handler, final List<Recipe> recipes) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnRecipesGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onRecipesSuccess(recipes);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeError(final ListenerHandler<OnRecipesGetListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnRecipesGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onRecipesError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
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

    public interface OnRecipesGetListener {
        void onRecipesSuccess(final List<Recipe> recipes);

        void onRecipesError(final Exception error);
    }
}
