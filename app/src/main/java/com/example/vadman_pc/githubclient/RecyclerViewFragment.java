package com.example.vadman_pc.githubclient;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vadman_pc.githubclient.api.Client;
import com.example.vadman_pc.githubclient.api.Service;
import com.example.vadman_pc.githubclient.model.Item;
import com.example.vadman_pc.githubclient.model.ItemResponse;
import com.example.vadman_pc.githubclient.utils.PaginationScrollListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment {

    private static final String TAG = RecyclerViewFragment.class.getSimpleName();

    private static final int PER_PAGE = 20; //constant value how much json we will receive
    private static final int PAGE_START = 1; //constant value where will be our first request
    private int TOTAL_PAGES = 10; //constant value how much page we will receive from pagination

    private boolean isLoading = false;// variable, which help pagination
    private boolean isLastPage = false;// variable, which help pagination

    private int currentPage = PAGE_START; // variable, which help pagination load new page

    private String LANGUAGE = "java"; // variable which language of programing we will search (now for protection we have hardcoded value "java")
    private String LOCATION = "krakow"; //variable which city we will search (now for protection we have hardcoded value "krakow")

    private Service apiService;  // service which connected to our URL
    private TextView diconected; // TextView which will shown if we will have problem with internet connection

    private List<Item> searchList; // main list which contains parsed JSON

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView mRecyclerView;
    private PaginationAdapter mAdapter; // adapter for recycle view
    private SwipeRefreshLayout swipeContainer;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blank2, container, false);

        diconected = (TextView) rootView.findViewById(R.id.disconnected);

        setupProgresBar();
        setupRecyclerView(rootView);
        setupSwipeContainer(rootView);

        loadJSONfromFirstPage();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.clear();
    }

    public void setupRecyclerView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.scrollToPosition(0);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PaginationAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setupProgresBar() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Github Users...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void setupSwipeContainer(View rootView) {
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }

    public void refreshItems() {
        currentPage = PAGE_START;
        mAdapter.clear();
        loadJSONfromFirstPage();
        Toast.makeText(getActivity(), "Github Users Refreshed", Toast.LENGTH_SHORT).show();
    }

    public void loadJSONfromFirstPage() {
        Log.d(TAG, "loadJSONfromFirstPage");

        try {
            Client client = new Client();
            apiService = Client.getClient().create(Service.class);
            Log.d(TAG, "createClient");
            Call<ItemResponse> call = apiService.getItems(new String[]{LANGUAGE, LOCATION}, currentPage, PER_PAGE);
            Log.d(TAG, "query");
            call.enqueue(new Callback<ItemResponse>() {
                @Override
                public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                    Log.d(TAG, "response");

                    if (response.body() != null) {
                        List<Item> items = response.body().getItems();
                        mRecyclerView.addOnScrollListener(getPaginationScrollListener(linearLayoutManager));
                        mRecyclerView.smoothScrollToPosition(0);
                        swipeContainer.setRefreshing(false);
                        searchList = items;
                        mAdapter.addAll(searchList);

                        if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
                        else isLastPage = true;
                        progressDialog.hide();

                    } else {
                        Toast.makeText(getActivity(), "Error body response loadJSONfromFirstPage", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }


                }

                @Override
                public void onFailure(Call<ItemResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getActivity(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                    diconected.setVisibility(View.VISIBLE);
                    progressDialog.hide();
                    Log.d("VadmanTagJSON", "loadJSONfromFirstPage: onFailure");


                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Log.d("VadmanTagJSON", "loadJSONfromFirstPage: BOOM");
        }
    }

    private PaginationScrollListener getPaginationScrollListener(LinearLayoutManager linearLayoutManager) {
        return new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        };
    }

    public void loadNextPage() {
        Log.d("VadmanTagJSON", "loadNextPage: " + currentPage);

        try {
            Call<ItemResponse> call = apiService.getItems(new String[]{LANGUAGE, LOCATION}, currentPage, PER_PAGE);
            call.enqueue(new Callback<ItemResponse>() {
                @Override
                public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                    mAdapter.removeLoadingFooter();
                    isLoading = false;


                    if (response.body() != null) {
                        List<Item> items = response.body().getItems();
                        searchList = items;
                        mAdapter.addAll(searchList);
                    } else {
                        Toast.makeText(getActivity(), "Error body response loadNextPage", Toast.LENGTH_SHORT).show();
                    }

                    if (currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter();
                    else isLastPage = true;
                }

                @Override
                public void onFailure(Call<ItemResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("VadmanTagJSON", "loadNextPage onFailure");

                    // TODO: 08/11/16 handle failure
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("VadmanTagJSON", "loadNextPage: BOOM");
        }
    }

    public void searchingItems(String newText) {
        newText = newText.toLowerCase();
//        List<Item> newList = new ArrayList<Item>();
        Log.d("VadmanTag 1", " searchList" + searchList);

//        for (Item item1 : searchList) {
//            String name = item1.getLogin().toLowerCase();
//
//            if (name.contains(newText)) {
//                newList.add(item1);
//            }java
//
//
//        }
//        newList = searchingRequest(newText);
//        mAdapter.setFilter(newList);

        if (newText == null) {
            newText = "vad";
        }
        String SEARCH_BY_NAME = newText;


        try {
            Call<ItemResponse> call = apiService.getItemsWithName(SEARCH_BY_NAME, new String[]{LANGUAGE, LOCATION}, currentPage, PER_PAGE);
            Log.d("VadmanTagJSON", "call ");
            call.enqueue(new Callback<ItemResponse>() {
                @Override
                public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {


                    Log.d("VadmanTagJSON", "response " + response);

                    if (response.body() != null) {
                        List<Item> items = response.body().getItems();
                        searchList = items;
                        mAdapter.setFilter(searchList);
                    } else {
                        Toast.makeText(getActivity(), "Error body response loadNextPage", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ItemResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("VadmanTagJSON", "loadNextPage: onFailure");
                    // TODO: 08/11/16 handle failure
                }
            });
        } catch (Exception e) {
            Log.d("VadmanTagJSON", "searchingItems: BOOM");
            e.printStackTrace();
        }

    }


    public void updateInfo(String location, String language) {
        LOCATION = location;
        LANGUAGE = language;

    }
    //    private void saveOnSharedPreference() {
//        String key = "Key";
//        SharedPreferences mPrefs = this.getActivity().getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
////        SharedPreferences pref2 = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        SharedPreferences.Editor editor = mPrefs.edit();
//        Gson gson = new Gson();
//        List<Item> savedSearchList = searchList;
//        Log.d("VadmanTag 1", "inside on SaveSHPref");
//
//        String jsonSearchList = gson.toJson(savedSearchList);
//        Log.d("VadmanTag 1", " jsonSearchList" + jsonSearchList);
//
////        editor = mPrefs.edit();
////        editor.remove(key).apply();
//        editor.putString(key, jsonSearchList);
//        Log.d("VadmanTag 1", " jsonSearchList");
//        editor.apply();
//    }


    //    public List<Item> loadSharedPreferences() {
    //        List<Item> savedList;
    //        SharedPreferences pref2 = PreferenceManager.getDefaultSharedPreferences(getActivity());
    //        Gson gson = new Gson();
    //        String json = pref2.getString("Key", "");
    //        if (json.isEmpty()) {
    //            savedList = new ArrayList<Item>();
    //        } else {
    //            Type type = new TypeToken<List<Item>>() {
    //            }.getType();
    //            savedList = gson.fromJson(json, type);
    //        }
    //        return savedList;
    //    }
}




