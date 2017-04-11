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

    Service apiService;
    public final String LANGUAGE = "java";
    public final String LOCATION = "krakow";
    public static final int PAGE_START = 1;
    public boolean isLoading = false;
    public boolean isLastPage = false;
    public int TOTAL_PAGES = 10;
    public int currentPage = PAGE_START;

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
        //сюда треба підгрузити запрос до бд
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
                currentPage = 1;
                mAdapter.clear();
                loadJSONfromFirstPage();
                Toast.makeText(getActivity(), "Github Users Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("VadmanLog", "finish_with_swipe_container");
        loadJSONfromFirstPage();
        return rootView;
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
            Call<ItemResponse> call = apiService.getItems(new String[]{LANGUAGE, LOCATION}, currentPage);
            Log.d("VadmanLog", "query");
            call.enqueue(new Callback<ItemResponse>() {
                @Override
                public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                    Log.d("VadmanLog", "response");
                    List<Item> items = response.body().getItems();
                    mRecyclerView.addOnScrollListener(getPaginationScrollListener(linearLayoutManager));
                    mRecyclerView.smoothScrollToPosition(0);
                    swipeContainer.setRefreshing(false);
                    searchList = items;
                    mAdapter.addAll(items);

                    if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
                    else isLastPage = true;
                    pd.hide();


                }

                @Override
                public void onFailure(Call<ItemResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getActivity(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                    diconected.setVisibility(View.VISIBLE);
                    pd.hide();


                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        }

    public void loadNextPage(){
        Log.d("VadmanTag", "loadNextPage: " + currentPage);

        Call<ItemResponse> call = apiService.getItems(new String[]{LANGUAGE, LOCATION}, currentPage);
        call.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                mAdapter.removeLoadingFooter();
                isLoading = false;

                List<Item> items = response.body().getItems();
                mAdapter.addAll(items);

                if (currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }

}




