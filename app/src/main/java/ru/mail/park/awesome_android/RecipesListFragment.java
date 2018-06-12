package ru.mail.park.awesome_android;

import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;


public class RecipesListFragment extends Fragment {
    private LinearLayout recipeLayout;
    private ArrayList<Recipe> recipes = new ArrayList<>();

    private void setOnClick(final LinearLayout layout, final Recipe recipe){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(getResources().getString(R.string.recipe), recipe);
                Fragment recipeInformationFragment = new RecipeInformationFragment();
                recipeInformationFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction
                        .replace(R.id.fragmentContainer, recipeInformationFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private View.OnClickListener onMainMenuButtonClickListener = new View.OnClickListener() {
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

    private void writeListOfRecipes(ArrayList<Recipe> recipes) {
        for (Recipe recipe: recipes) {
            LinearLayout recipeTitle = new LinearLayout(getActivity());
            LinearLayout.LayoutParams params;

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
            } else {
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
            }

            int margin_left_and_right = getResources().getInteger(R.integer.recipe_margin_left_and_right);
            int margin_top_and_bottom = getResources().getInteger(R.integer.recipe_margin_top_and_bottom);

            params.setMargins(margin_left_and_right, margin_top_and_bottom, margin_left_and_right, margin_top_and_bottom);

            recipeTitle.setLayoutParams(params);
            recipeTitle.setOrientation(LinearLayout.VERTICAL);
            recipeTitle.setClickable(true);

            GradientDrawable border = new GradientDrawable();
            border.setColor(getResources().getColor(R.color.white));
            border.setStroke(getResources().getInteger(R.integer.recipe_border), getResources().getColor(R.color.colorPrimary));
            border.setCornerRadius(getResources().getInteger(R.integer.border_radius));
            recipeTitle.setBackground(border);

            int padding = getResources().getInteger(R.integer.recipe_title_padding);
            recipeTitle.setPadding(padding, padding, padding, padding);

            TextView name = new TextView(getActivity());
            int list_padding = getResources().getInteger(R.integer.list_padding);
            name.setText(recipe.getName());
            name.setGravity(Gravity.START);
            name.setTextSize(getResources().getInteger(R.integer.recipe_name_text_size));

            name.setWidth(getResources().getInteger(R.integer.recipe_name_width));
            name.setPadding(list_padding, list_padding, list_padding, list_padding);
            name.setTextColor(getResources().getColor(R.color.primaryText));
            recipeTitle.addView(name);

            TextView products = new TextView(getActivity());
            ArrayList<String> productsList = recipe.getProducts();
            StringBuilder listOfProducts = new StringBuilder();

            String prefix = "";
            for (String str: productsList) {
                listOfProducts.append(prefix);
                prefix = getResources().getString(R.string.prefix);
                listOfProducts.append(str);
            }

            products.setText(listOfProducts.toString());
            products.setGravity(Gravity.START);
            products.setWidth(getResources().getInteger(R.integer.products_list_width));
            products.setPadding(list_padding, list_padding, list_padding, list_padding);
            products.setTextColor(getResources().getColor(R.color.secondaryText));
            recipeTitle.addView(products);

            setOnClick(recipeTitle, recipe);

            recipeLayout.addView(recipeTitle);
        }
    }

    private void formingRecipesList() {
        ArrayList<Serializable> recipesFromServer = new ArrayList<>();
        Bundle bundle = getArguments();

        for (int i = 0; i < bundle.getInt("size"); i++) {
            try {
                recipesFromServer.add(bundle.getSerializable("recipe " + i));
            } catch (final Exception e) {
                Toast errorMessage = Toast.makeText(getContext(), R.string.error_message, Toast.LENGTH_SHORT);
                errorMessage.show();
            }
        }

        for (int i = 0; i < recipesFromServer.size(); i++) {
            Recipe recipe = (Recipe) recipesFromServer.get(i);
            recipes.add(recipe);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.get_list_fr, container, getResources().getBoolean(R.bool.attach_to_root));

        recipeLayout = v.findViewById(R.id.list_of_recipes);
        Button mainMenuButton = v.findViewById(R.id.main_menu_button_from_recipe_list);
        mainMenuButton.setOnClickListener(onMainMenuButtonClickListener);

        if (recipes.isEmpty()) {
            formingRecipesList();
        }

        writeListOfRecipes(recipes);

        return v;
    }
}
