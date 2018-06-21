package ru.mail.park.awesome_android;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnterFragment extends Fragment {
    private ImageButton addButton;
    private Button searchButton;
    private RelativeLayout loadingPanel;
    private AutoCompleteTextView enterIngredient;
    private ArrayList<String> ingredients;
    private ValueAnimator animator;

    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    private Handler handler = new MyHandler(this);

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

    private ListenerHandler<Api.OnRecipesGetListener> recipeHandler;

    private Api.OnRecipesGetListener recipeListener = new Api.OnRecipesGetListener() {
        @Override
        public void onRecipesSuccess(List<Recipe> recipes) {

            if (recipes != null && recipes.size() != 0) {
                ingredients.clear();
                Fragment recipesListFragment = new RecipesListFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("size", recipes.size());

                for (int i = 0; i < recipes.size(); i++) {
                    bundle.putSerializable("recipe " + i, recipes.get(i));
                }

                recipesListFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, recipesListFragment, getResources().getString(R.string.recipe_tag))
                        .addToBackStack(null)
                        .commit();
            } else {
                searchButton.setVisibility(View.VISIBLE);
                loadingPanel.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.empty_recipes_list, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRecipesError(Exception error) {
            searchButton.setVisibility(View.VISIBLE);
            loadingPanel.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.error_message, Toast.LENGTH_SHORT).show();
        }
    };

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("ingredients", ingredients);
    }


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
                    loadingPanel.setVisibility(View.GONE);
                    v.setVisibility(View.VISIBLE);
                }
            });

            if (loadingPanel != null) {
                loadingPanel.setVisibility(View.VISIBLE);
            }

            if (recipeHandler != null) {
                recipeHandler.unregister();
            }


            recipeHandler = Api.getInstance().getRecipes(ingredients, recipeListener);
        }
    };

    private View.OnClickListener onAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ingredient = enterIngredient.getText().toString();
            hideKeyboard(enterIngredient);

            reset();

            final float endValue = 180f;
            animator = ValueAnimator.ofFloat(0, endValue);
            animator.setDuration(500L);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    addButton.setRotation(value);
                }
            });
            animator.start();

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

    private static void hideKeyboard(final View input) {
        final InputMethodManager inputMethodManager = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    private void reset() {
        if (animator != null) {
            animator.cancel();
        }

        addButton.setRotation(0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.enter_fr, container, getResources().getBoolean(R.bool.attach_to_root));

        if (savedInstanceState != null && savedInstanceState.containsKey("ingredients")) {
            ingredients = savedInstanceState.getStringArrayList("ingredients");
        } else {
            ingredients = new ArrayList<>();
        }


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