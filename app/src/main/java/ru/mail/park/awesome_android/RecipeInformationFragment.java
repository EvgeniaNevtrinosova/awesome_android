package ru.mail.park.awesome_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class RecipeInformationFragment extends Fragment {
    private Recipe recipe = new Recipe();
    private TextView recipeName;
    private TextView recipeProducts;
    private TextView recipeText;

    private void getRecipe() {
        Object clickRecipe = new Object();
        Bundle bundle = getArguments();

        try {
            clickRecipe = bundle.getSerializable("recipe");
        } catch (final Exception e) {
            Toast errorMessage = Toast.makeText(getContext(), R.string.error_message, Toast.LENGTH_SHORT);
            errorMessage.show();
        }
        recipe = (Recipe) clickRecipe;
    }

    private void writeRecipe() {
        recipeName.setText(recipe.getName());

        Map<String, String> productsMap = new HashMap<String, String>();
        productsMap = recipe.getProducts();
        StringBuilder listOfProducts = new StringBuilder();
        String prefix = "";
        for (Map.Entry entry: productsMap.entrySet()) {
            listOfProducts.append(prefix);
            prefix = ",";
            listOfProducts.append(entry.getKey()).append('-').append(entry.getValue());
        }
        recipeProducts.setText(listOfProducts.toString());

        recipeText.setText(recipe.getText());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.info_recipe_fr, container, false);

        recipeName = v.findViewById(R.id.recipe_name);
        recipeProducts = v.findViewById(R.id.recipe_products);
        recipeText = v.findViewById(R.id.recipe_text);

        getRecipe();
        writeRecipe();

        return v;
    }
}
