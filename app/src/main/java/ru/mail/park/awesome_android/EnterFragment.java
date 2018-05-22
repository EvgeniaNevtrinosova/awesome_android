package ru.mail.park.awesome_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
    private AutoCompleteTextView enterIngredient;
    private LinearLayout addedIngredients;
    private ArrayList ingredientsArray = new ArrayList();
    private String ingredient;
    private static final Gson GSON = new GsonBuilder()
            .create();

    private String[] autoCompleteArray = {"Огурец", "Томат", "Картошка", "Моркошка", "Говно", "Палки", "Хрен"};

    public static EnterFragment newInstance() {
        Bundle args = new Bundle();
        EnterFragment fragment = new EnterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View.OnClickListener onRemoveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout parent = (LinearLayout) ((LinearLayout) v.getParent()).getParent();
            LinearLayout childLayoutWithButton = (LinearLayout) v.getParent();
            TextView childText = (TextView) parent.getChildAt(0);

            String removeIngredient = childText.getText().toString();
            ingredientsArray.remove(removeIngredient);

            ((ViewManager) parent).removeView(childText);
            ((ViewManager) parent).removeView(childLayoutWithButton);
            ((ViewManager) parent).removeView(parent);
        }
    };

    private View.OnClickListener onSearchButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonObject json = new JsonObject();


            json.addProperty("products", String.valueOf(ingredientsArray));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://195.133.1.169:8082/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Service service = retrofit.create(Service.class);
            final Call<ResponseBody> post = service.setIngredients(json);
            post.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String body = response.body().string();
                            final List<Recipe> getRecipes = parseRecipe(body);
                            ingredientsArray.clear();

                            Fragment recipesListFragment = new RecipesListFragment();

                            Bundle bundle = new Bundle();
                            bundle.putInt("size", getRecipes.size());
                            for (int i = 0; i < getRecipes.size(); i++) {
                                bundle.putSerializable("recipe " + i, getRecipes.get(i));
                            }

                            recipesListFragment.setArguments(bundle);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainer, recipesListFragment)
                                    .addToBackStack(null)
                                    .commit();

                        } catch (IOException e) {

                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });


        }
    };

    private View.OnClickListener onAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ingredient = enterIngredient.getText().toString();
            ingredientsArray.add(ingredient);

            if (ingredient.length() == 0) {
                return;
            }
            enterIngredient.setText(R.string.empty_string);

            LinearLayout layoutWithIngredientAndButton = new LinearLayout(getActivity());
            layoutWithIngredientAndButton.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout layoutWithButton = new LinearLayout(getActivity());
            layoutWithButton.setGravity(Gravity.RIGHT);

            Button remove = new Button(getActivity());
            remove.setText(R.string.remove);
            remove.setWidth(200);
            remove.setOnClickListener(onRemoveButtonClickListener);
            layoutWithButton.addView(remove);

            TextView text = new TextView(getActivity());
            text.setText(ingredient);
            text.setGravity(Gravity.LEFT);
            text.setTextSize(20);
            text.setWidth(800);
            layoutWithIngredientAndButton.addView(text);
            layoutWithIngredientAndButton.addView(layoutWithButton);

            addedIngredients.addView(layoutWithIngredientAndButton);
        }
    };

    public List<Recipe> parseRecipe(final String body) throws IOException {
        try {
            Type listType = new TypeToken<List<Recipe>>(){}.getType();
            return (List<Recipe>)GSON.fromJson(body, listType);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.enter_fr, container, false);

        enterIngredient = v.findViewById(R.id.ingredient);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, autoCompleteArray);
        enterIngredient.setAdapter(adapter);

        addButton = v.findViewById(R.id.add_button);
        searchButton = v.findViewById(R.id.search_recipe);

        addedIngredients = v.findViewById(R.id.add_ingredient_layout);

        addButton.setOnClickListener(onAddButtonClickListener);
        searchButton.setOnClickListener(onSearchButtonClickListener);

        return v;
    }
}
