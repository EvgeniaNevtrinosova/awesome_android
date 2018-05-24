package ru.mail.park.awesome_android;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnterFragment extends Fragment {
    private Button addButton;
    private Button searchButton;
    private RelativeLayout loadingPanel;
    private AutoCompleteTextView enterIngredient;
    private ArrayList<String> ingredients;
    private Call<ResponseBody> post;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    private Handler handler = new MyHandler(this);
    private static final Gson GSON = new GsonBuilder()
            .create();

    public static EnterFragment newInstance() {
        Bundle args = new Bundle();
        EnterFragment fragment = new EnterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    static class MyHandler extends Handler {
        WeakReference<EnterFragment> frag;

        MyHandler(EnterFragment f) {
            frag = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            frag.get().loadingPanel.setVisibility(View.GONE);
            frag.get().searchButton.setVisibility(View.VISIBLE);
        }
    }

    private TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().contains(getResources().getString(R.string.break_symbol))) {
                s = s.toString().replace(getResources().getString(R.string.break_symbol), getResources().getString(R.string.empty_string));
                enterIngredient.setText(s);
                addButton.performClick();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private View.OnClickListener onSearchButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (ingredients.size() == getResources().getInteger(R.integer.empty_size)) {
                Toast.makeText(getActivity(), R.string.empty_error_message, Toast.LENGTH_SHORT).show();
                return;
            }
            v.setVisibility(View.GONE);
            loadingPanel = getActivity().findViewById(R.id.loadingPanel);
            loadingPanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    post.cancel();
                    loadingPanel.setVisibility(View.GONE);
                    v.setVisibility(View.VISIBLE);
                }
            });

            if (loadingPanel != null) {
                loadingPanel.setVisibility(View.VISIBLE);
            }
            JsonObject json = new JsonObject();
            json.addProperty(getResources().getString(R.string.products_property), String.valueOf(ingredients));


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Service service = retrofit.create(Service.class);

            post = service.setIngredients(json);

            post.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            handler.sendEmptyMessage(getResources().getInteger(R.integer.empty_size));

                            ResponseBody responseBody = response.body();

                            if (responseBody != null) {
                               String body = responseBody.string();

                                if (body != null && body.equals(getResources().getString(R.string.empty_response_body))) {
                                    Toast.makeText(getActivity(), R.string.empty_recipes_list, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                final List<Recipe> getRecipes = parseRecipe(body);

                                ingredients.clear();

                                Fragment recipesListFragment = new RecipesListFragment();

                                Bundle bundle = new Bundle();
                                bundle.putInt("size", getRecipes.size());
                                for (int i = 0; i < getRecipes.size(); i++) {
                                    bundle.putSerializable("recipe " + i, getRecipes.get(i));
                                }

                                recipesListFragment.setArguments(bundle);
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragmentContainer, recipesListFragment, getResources().getString(R.string.recipe_tag))
                                        .addToBackStack(null)
                                        .commit();
                            }
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), R.string.error_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    if(t.getMessage().equals(getResources().getString(R.string.network_throwable))) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    }

                    if (t.getMessage().equals(getResources().getString(R.string.server_throwable))) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    };

    private View.OnClickListener onAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ingredient = enterIngredient.getText().toString();
            if (ingredient.length() == getResources().getInteger(R.integer.empty_size)) {
                return;
            }
            if (!Arrays.asList(getResources().getStringArray(R.array.autoCompleteArray)).contains(ingredient.toLowerCase())) {
                Toast t = Toast.makeText(getActivity(), R.string.not_found_ingredients, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER_VERTICAL, getResources().getInteger(R.integer.toast_xOffset), getResources().getInteger(R.integer.toast_xOffset));
                t.show();
                return;
            }

            if (ingredients.contains(ingredient.toLowerCase())) {
                adapter.notifyItemChanged(ingredients.indexOf(ingredient.toLowerCase()));
                return;
            }

            enterIngredient.setText(R.string.empty_string);
            ingredients.add(ingredient);
            adapter.notifyItemChanged(getResources().getInteger(R.integer.adapter_position));
        }
    };

    public List<Recipe> parseRecipe(final String body) throws IOException {
        try {
            Type listType = new TypeToken<List<Recipe>>() {
            }.getType();
            return GSON.fromJson(body, listType);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View v = inflater.inflate(R.layout.enter_fr, container, getResources().getBoolean(R.bool.attach_to_root));
        ingredients = new ArrayList<>();
        adapter = new RecyclerAdapter(ingredients);
        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (container != null) {
                recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), getResources().getInteger(R.integer.landscape_span)));
            }
        } else {
            if (container != null) {
                recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), getResources().getInteger(R.integer.portrait_span)));
            }
        }
        adapter.notifyDataSetChanged();

        enterIngredient = v.findViewById(R.id.ingredient);

        Resources res = getResources();
        String[] autoCompleteArray = res.getStringArray(R.array.autoCompleteArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, autoCompleteArray);
        enterIngredient.setAdapter(adapter);
        enterIngredient.addTextChangedListener(onTextChangedListener);

        addButton = v.findViewById(R.id.add_button);
        searchButton = v.findViewById(R.id.search_recipe);

        addButton.setOnClickListener(onAddButtonClickListener);
        searchButton.setOnClickListener(onSearchButtonClickListener);
        return v;
    }
}
