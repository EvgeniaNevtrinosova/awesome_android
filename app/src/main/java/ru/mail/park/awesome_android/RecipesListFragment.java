package ru.mail.park.awesome_android;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class RecipesListFragment extends Fragment {
    private LinearLayout recipeLayout;
    private ArrayList<Recipe> recipes = new ArrayList<>();

    private void setOnClick(final LinearLayout layout, final Recipe recipe){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("recipe", recipe);
                Fragment recipeInformationFragment = new RecipeInformationFragment();
                recipeInformationFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                //transaction.remove(fragmentManager.findFragmentById(R.id.fragmentContainer));

                transaction
                        .replace(R.id.fragmentContainer, recipeInformationFragment)
                        .addToBackStack("Tag name")
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

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 30, 20, 30);

            recipeTitle.setLayoutParams(params);
            recipeTitle.setOrientation(LinearLayout.VERTICAL);
            recipeTitle.setClickable(true);

            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(10, 0xFF000000);
            recipeTitle.setBackground(border);

            recipeTitle.setPadding(16, 16, 16, 16);

            TextView name = new TextView(getActivity());
            name.setText(recipe.getName());
            name.setGravity(Gravity.START);
            name.setTextSize(20);
            name.setWidth(800);
            recipeTitle.addView(name);

            TextView products = new TextView(getActivity());
            ArrayList productsList;
            productsList = recipe.getProducts();
            StringBuilder listOfProducts = new StringBuilder();

            String prefix = "";
            for (Object str: productsList) {
                listOfProducts.append(prefix);
                prefix = ",";
                listOfProducts.append(str);
            }

            products.setText(listOfProducts.toString());
            products.setGravity(Gravity.START);
            products.setTextSize(20);
            products.setWidth(1000);
            recipeTitle.addView(products);

            setOnClick(recipeTitle, recipe);

            recipeLayout.addView(recipeTitle);
        }
    }

    private void formingRecipesList() {
        ArrayList recipesFromServer = new ArrayList();
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
        View v = inflater.inflate(R.layout.get_list_fr, container, false);

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
