package com.example.vadman_pc.githubclient;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class MainFragment extends Fragment {

    EditText etLocation; // View where we enter our location
    EditText etLanguage; // View where we enter our language
    OnNameSetListener onNameSetListener; // Interface for communication between activities



    public MainFragment() {
        // Required empty public constructor
    }


    // This method collect information from our EditText Views, and send it by interface to Main activity

    public void onSearch() {

        if ((etLocation.getText().toString() != null) && (etLanguage.getText().toString()!= null)) {

            // TODO: 14.04.2017 if app relaunched, we have null pointer exceptions in et

           String location = etLocation.getText().toString();
           String language = etLanguage.getText().toString();
           onNameSetListener.setInfoForSearch(location, language);
        }
    }

    // Inflate our fragment and show it in the page
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_main_fragment, container, false);

        etLocation = (EditText) rootView.findViewById(R.id.location);
        etLanguage = (EditText) rootView.findViewById(R.id.language);

//        etLanguage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    hideKeyboard(v);
//                }
//            }
//        });

        etLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        return rootView;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    //it helps send information to another fragment
    public interface OnNameSetListener {
        public void setInfoForSearch(String location, String language);
    }

    //it helps send information to another fragment
    // TODO: 13.04.2017 find out how to switch deprecated onAttach
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

    @Override
    public void onStop() {
        super.onStop();
    }
}
