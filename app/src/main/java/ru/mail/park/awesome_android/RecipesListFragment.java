package ru.mail.park.awesome_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RecipesListFragment extends Fragment {

//    public static final Arra

    public static RecipesListFragment newInstance() {

        Bundle args = new Bundle();

        RecipesListFragment fragment = new RecipesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.get_list_fr, container, false);

        return v;
    }
}
