package ru.mail.park.awesome_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class RecipeInformationFragment extends Fragment {
    private Recipe recipe = new Recipe();
    private TextView recipeName;
    private TextView recipeProducts;
    private TextView recipeText;

    private void getRecipe() {
        Object clickRecipe = new Object();
        Bundle bundle = getArguments();

        try {
            clickRecipe = bundle.getSerializable(getResources().getString(R.string.recipe));
        } catch (final Exception e) {
            Toast errorMessage = Toast.makeText(getContext(), R.string.error_message, Toast.LENGTH_SHORT);
            errorMessage.show();
        }
        recipe = (Recipe) clickRecipe;
    }

    private void writeRecipe() {
        recipeName.setText(recipe.getName());
        ArrayList<String> productsList = recipe.getProducts();
        StringBuilder listOfProducts = new StringBuilder();

        String prefix = "";
        for (String str: productsList) {
            listOfProducts.append(prefix);
            prefix = getResources().getString(R.string.prefix);
            listOfProducts.append(str);
        }
        recipeProducts.setText(listOfProducts.toString());
        recipeText.setText(recipe.getText());
    }

    private View.OnClickListener onMainClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            EnterFragment enterFragment = new EnterFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, enterFragment)
                    .addToBackStack(null)
                    .commit();
        }
    };

    private View.OnClickListener onRecipeListClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            Fragment listFragment = fragmentManager.findFragmentByTag(getResources().getString(R.string.recipe_tag));

            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, listFragment)
                    .addToBackStack(null)
                    .commit();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.info_recipe_fr, container, getResources().getBoolean(R.bool.attach_to_root));

        recipeName = v.findViewById(R.id.recipe_name);
        recipeProducts = v.findViewById(R.id.recipe_products);
        recipeText = v.findViewById(R.id.recipe_text);
        Button recipeListButton = v.findViewById(R.id.recipe_button);
        Button mainMenuButton = v.findViewById(R.id.main_button);

        recipeListButton.setOnClickListener(onRecipeListClickListener);
        mainMenuButton.setOnClickListener(onMainClickListener);

        getRecipe();
        writeRecipe();

        return v;
    }
}
