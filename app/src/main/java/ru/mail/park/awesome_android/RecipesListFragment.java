package ru.mail.park.awesome_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipesListFragment extends Fragment {
    private LinearLayout recipeLayout;

    private void writeListOfRecipes(ArrayList<Recipe> recipes) {
        for (int i = 0; i < recipes.size(); i++) {
            LinearLayout recipeTitle = new LinearLayout(getActivity());
            recipeTitle.setOrientation(LinearLayout.VERTICAL);

            TextView name = new TextView(getActivity());
            name.setText(recipes.get(i).getName());
            name.setGravity(Gravity.LEFT);
            name.setTextSize(20); //убрать хардкод
            name.setWidth(800);
            recipeTitle.addView(name);

            TextView products = new TextView(getActivity());
            Map<String, String> productsArray = new HashMap<String, String>();
            productsArray = recipes.get(i).getProducts();
            StringBuilder listOfProducts = new StringBuilder();

            for (Map.Entry entry: productsArray.entrySet()) {
                listOfProducts.append(entry.getKey()).append('-').append(entry.getValue()).append(',');

            }

            products.setText(listOfProducts.toString());
            products.setGravity(Gravity.LEFT);
            products.setTextSize(20); //убрать хардкод
            products.setWidth(1000);
            recipeTitle.addView(products);

            recipeLayout.addView(recipeTitle);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.get_list_fr, container, false);
        ArrayList getRecipes = new ArrayList();
        recipeLayout = v.findViewById(R.id.list_of_recipes);

        Bundle bundle = getArguments();
        for (int i = 0; i < bundle.getInt("size"); i++) {
            try {
                getRecipes.add(bundle.getSerializable("recipe " + i));
            } catch (final Exception e) {
                //
            }
        }

        ArrayList<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < getRecipes.size(); i++) {
            Recipe recipe = (Recipe) getRecipes.get(i);
            recipes.add(recipe);
        }

        writeListOfRecipes(recipes);
        return v;
    }
}
