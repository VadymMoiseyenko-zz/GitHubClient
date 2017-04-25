package com.example.vadman_pc.githubclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainFragment extends Fragment {

    public interface OnNameSetListener {
        void setInfoForSearch(String location, String language);
    }

    private static final String TAG = MainFragment.class.getSimpleName();
    private EditText etLocation;
    private EditText etLanguage;
    private OnNameSetListener onNameSetListener;

    public MainFragment() {
    }

    public void onSearch() {
        if ((!TextUtils.isEmpty(etLocation.getText().toString()) && !TextUtils.isEmpty(etLanguage.getText().toString()))) {
            // TODO: 14.04.2017 if app relaunched, we have null pointer exceptions in et

            String location = etLocation.getText().toString();
            String language = etLanguage.getText().toString();

            if (onNameSetListener != null) {
                onNameSetListener.setInfoForSearch(location, language);
            }
        }
    }

    // Inflate our fragment and show it in the page
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_fragment, container, false);

        etLocation = (EditText) rootView.findViewById(R.id.location);
        etLanguage = (EditText) rootView.findViewById(R.id.language);

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
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onNameSetListener = (OnNameSetListener) context;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
