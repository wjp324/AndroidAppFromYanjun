package com.futcore.restaurant.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.futcore.restaurant.R;

public class MainPageFragment extends Fragment {

    public static final MainPageFragment newInstance(String message)
    {
        MainPageFragment f = new MainPageFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString("wahaha", message);
        f.setArguments(bdl);
        return f;
    }    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_index, container, false);
        return rootView;
    }
}
