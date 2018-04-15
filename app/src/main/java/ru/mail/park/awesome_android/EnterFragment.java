package ru.mail.park.awesome_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EnterFragment extends Fragment {
    private Button addButton;
    private Button searchButton;
    private EditText enterIngredient;
    private LinearLayout addedIngredients;

    private Ingredients ingredients = new Ingredients();
    private ArrayList ingredientsArray = new ArrayList();

    private String ingredient;

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

            String ingr = childText.getText().toString();

            ingredientsArray.remove(ingr);

            ((ViewManager) parent).removeView(childText);
            ((ViewManager) parent).removeView(childLayoutWithButton);
            ((ViewManager) parent).removeView(parent);
        }
    };

    private View.OnClickListener onSearchButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Gson gson = new Gson();

            ingredients.setIngredients(ingredientsArray);
            gson.toJson(ingredients);


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://food-node.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Service service = retrofit.create(Service.class);

            final Call<Recipe[]> call = service.getRecipe();

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Response<Recipe[]> response = call.execute();

                        Recipe[] recipesArray = response.body();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, RecipesListFragment.newInstance())
                    .commit();

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
            enterIngredient.setText(R.string.delete);

            LinearLayout layoutWithIngredientAndButton = new LinearLayout(getActivity());
            layoutWithIngredientAndButton.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout layoutWithButton = new LinearLayout(getActivity());
            layoutWithButton.setGravity(Gravity.RIGHT);

            Button remove = new Button(getActivity());
            remove.setText(R.string.remove); //убрать хардкод
            remove.setWidth(200);
            remove.setOnClickListener(onRemoveButtonClickListener);
            layoutWithButton.addView(remove);


            TextView text = new TextView(getActivity());
            text.setText(ingredient);
            text.setGravity(Gravity.LEFT);
            text.setTextSize(20); //убрать хардкод
            text.setWidth(800);
            layoutWithIngredientAndButton.addView(text);
            layoutWithIngredientAndButton.addView(layoutWithButton);

            addedIngredients.addView(layoutWithIngredientAndButton);
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.enter_fr, container, false);

        enterIngredient = v.findViewById(R.id.ingredient);
        addButton = v.findViewById(R.id.add_button);
        searchButton = v.findViewById(R.id.search_recipe);

        addedIngredients = v.findViewById(R.id.add_ingredient_layout);

        addButton.setOnClickListener(onAddButtonClickListener);
        searchButton.setOnClickListener(onSearchButtonClickListener);

        return v;
    }
}
