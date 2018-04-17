package ru.mail.park.awesome_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class RecipesListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.get_list_fr, container, false);
        ArrayList getRecipes = new ArrayList();

        Bundle bundle = getArguments();
        for (int i = 0; i < bundle.getInt("size"); i++) {
            try {
                getRecipes.add(bundle.getSerializable("recipe " + i));
            } catch (final Exception e) {
                //
            }
        }

        return v;
    }
}
