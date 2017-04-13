package com.example.vadman_pc.githubclient;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    EditText etLocation;
    EditText etLanguage;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_fragment, container, false);
        etLocation = (EditText) rootView.findViewById(R.id.location);
        etLanguage = (EditText) rootView.findViewById(R.id.language);

        String location = etLocation.getText().toString();
        String language = etLanguage.getText().toString();

        return rootView;
    }

}
