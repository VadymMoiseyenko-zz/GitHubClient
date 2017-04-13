package com.example.vadman_pc.githubclient;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    EditText etLocation;
    EditText etLanguage;

    OnNameSetListener onNameSetListener;



    public MainFragment() {
        // Required empty public constructor
    }

    public void onSearch() {
        String location = etLocation.getText().toString();
        String language = etLanguage.getText().toString();

        onNameSetListener.setInfoForSearch(location,language);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_fragment, container, false);
        etLocation = (EditText) rootView.findViewById(R.id.location);
        etLanguage = (EditText) rootView.findViewById(R.id.language);




        return rootView;
    }

    public interface OnNameSetListener {
        public void setInfoForSearch(String location, String language);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            onNameSetListener = (OnNameSetListener) context;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
