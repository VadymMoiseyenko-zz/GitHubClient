package com.example.vadman_pc.githubclient;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment {

    private static final int PER_PAGE = 20;
    Service apiService;
    public String LANGUAGE = "java";
    public String LOCATION = "krakow";
    public static final int PAGE_START = 1;
    public boolean isLoading = false;
    public boolean isLastPage = false;
    public int TOTAL_PAGES = 10;
    public int currentPage = PAGE_START;

    public String tag = getTag();
    public String SEARCH_BY_NAME;

    TextView diconected;

    private static final String TAG = "RecyclerViewFragment";
    List<Item> searchList;

    protected LinearLayoutManager linearLayoutManager;


    protected RecyclerView mRecyclerView;
    protected PaginationAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;
    private ProgressDialog pd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity();


    }

    public void updateInfo(String location, String language) {
        LOCATION = location;
        LANGUAGE = language;

    }

    @Override
    public void onStart() {
        super.onStart();
        loadJSONfromFirstPage();


    }

    @Override
    public void onResume() {
        Log.d("VadmanTag 1", " jsonSearchLisugut3333333333333");
        searchList = loadSharedPreferences();
        Log.d("VadmanTag 1", " searchList" + searchList);
        super.onResume();
    }

    public List<Item> loadSharedPreferences() {
        List<Item> savedList;
        SharedPreferences pref2 = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = pref2.getString("Key", "");
        if (json.isEmpty()) {
            savedList = new ArrayList<Item>();
        } else {
            Type type = new TypeToken<List<Item>>() {
            }.getType();
            savedList = gson.fromJson(json, type);
        }
        return savedList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_blank2, container, false);
        rootView.setTag(TAG);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Fetching Github Users...");
        pd.setCancelable(false);
        pd.show();

        diconected = (TextView) rootView.findViewById(R.id.disconnected);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.scrollToPosition(0);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new PaginationAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        Log.d("VadmanLog", "swipecontainer");
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        Log.d("VadmanLog", "finish_with_swipe_container");

        return rootView;
    }

    public void refreshItems() {
        currentPage = PAGE_START;
        mAdapter.clear();
        loadJSONfromFirstPage();
        Toast.makeText(getActivity(), "Github Users Refreshed", Toast.LENGTH_SHORT).show();
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


    public void loadJSONfromFirstPage() {
        Log.d("VadmanLog", "loadJSONfromFirstPage");

        try {
            Client client = new Client();
            apiService = Client.getClient().create(Service.class);
            Log.d("VadmanLog", "createClient");
            Call<ItemResponse> call = apiService.getItems(new String[]{LANGUAGE, LOCATION}, currentPage, PER_PAGE);
            Log.d("VadmanLog", "query");
            call.enqueue(new Callback<ItemResponse>() {
                @Override
                public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                    Log.d("VadmanLog", "response");

                    if (response.body() != null) {
                        List<Item> items = response.body().getItems();
                        mRecyclerView.addOnScrollListener(getPaginationScrollListener(linearLayoutManager));
                        mRecyclerView.smoothScrollToPosition(0);
                        swipeContainer.setRefreshing(false);
                        searchList = items;
                        mAdapter.addAll(searchList);

                        if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
                        else isLastPage = true;
                        pd.hide();

                    } else {
                        Toast.makeText(getActivity(), "Error body response loadJSONfromFirstPage", Toast.LENGTH_SHORT).show();
                    }




                }

                @Override
                public void onFailure(Call<ItemResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getActivity(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                    diconected.setVisibility(View.VISIBLE);
                    pd.hide();
                    Log.d("VadmanTagJSON", "loadJSONfromFirstPage: onFailure");


                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Log.d("VadmanTagJSON", "loadJSONfromFirstPage: BOOM");
        }
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
        SEARCH_BY_NAME = newText;


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

//    private List<Item> searchingRequest(String newText) {
//
//        if (newText == null) {
//            newText = "vad";
//        }
//        SEARCH_BY_NAME = newText;
//        Call<ItemResponse> call = apiService.getItemsWithName(SEARCH_BY_NAME, new String[]{LANGUAGE, LOCATION}, currentPage, PER_PAGE);
//        Log.d("VadmanTagJSON", "call ");
//        call.enqueue(new Callback<ItemResponse>() {
//            @Override
//            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
//
//
//                Log.d("VadmanTagJSON", "response " + response);
//                List<Item> items = response.body().getItems();
//                searchList = items;
//            }
//
//            @Override
//            public void onFailure(Call<ItemResponse> call, Throwable t) {
//                t.printStackTrace();
//                // TODO: 08/11/16 handle failure
//            }
//        });
//        return searchList;
//    }

    @Override
    public void onPause() {
        super.onPause();
        pd.dismiss();
        Log.d("VadmanTag 1", "inside on Pause");

        saveOnSharedPreference();


    }

    private void saveOnSharedPreference() {
        String key = "Key";
        SharedPreferences mPrefs = this.getActivity().getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
//        SharedPreferences pref2 = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        List<Item> savedSearchList = searchList;
        Log.d("VadmanTag 1", "inside on SaveSHPref");

        String jsonSearchList = gson.toJson(savedSearchList);
        Log.d("VadmanTag 1", " jsonSearchList" + jsonSearchList);

//        editor = mPrefs.edit();
//        editor.remove(key).apply();
        editor.putString(key, jsonSearchList);
        Log.d("VadmanTag 1", " jsonSearchList");
        editor.apply();
    }


}




