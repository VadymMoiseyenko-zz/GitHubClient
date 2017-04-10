package com.example.vadman_pc.githubclient.controller;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.example.vadman_pc.githubclient.R;

/**
 * Created by Vadman_PC on 10.04.2017.
 */
public class BlankFragment extends Fragment {

    public BlankFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.linear);

        return rootView;
    }
}
