package com.example.vadman_pc.githubclient.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vadman_pc.githubclient.PaginationAdapter;
import com.example.vadman_pc.githubclient.R;
import com.example.vadman_pc.githubclient.api.Client;
import com.example.vadman_pc.githubclient.api.Service;
import com.example.vadman_pc.githubclient.model.Item;
import com.example.vadman_pc.githubclient.model.ItemResponse;
import com.example.vadman_pc.githubclient.utils.PaginationScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private final String LANGUAGE = "java";
    private final String LOCATION = "krakow";

    List<Item> searchList;

    private RecyclerView recyclerView;
    TextView diconected;
    private Item item;
    ProgressDialog pd;
    PaginationAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    public static LinearLayoutManager linearLayoutManager;

    private static Context context;

    Service apiService;

    Toolbar mainToolbar;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 10;
    private int currentPage = PAGE_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        initViews();

        initFrgments();
        Log.d("VadmanLog", "swipecontainer");
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                adapter.clear();
                loadJSONfromFirstPage();
                Toast.makeText(MainActivity.this, "Github Users Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("VadmanLog", "finish_with_swipe_container");

        loadJSONfromFirstPage();
    }

    private void initFrgments() {
        Log.d("VadmanLog", "begine init frag");
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_page);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this) {
        };
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
        Log.d("VadmanLog", "finish init frag");

}

    private void initViews() {
        Log.d("VadmanLog", "begine init Views");
        mainToolbar = (Toolbar) findViewById(R.id.my_toolBar);
        setSupportActionBar(mainToolbar);


        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Github Users...");
        pd.setCancelable(false);
        pd.show();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new PaginationAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(0);
        Log.d("VadmanLog", "Finish ini views");




    }


    private void loadJSONfromFirstPage() {
        Log.d("VadmanLog", "loadJSONfromFirstPage");
        diconected = (TextView) findViewById(R.id.disconnected);

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
                    recyclerView.addOnScrollListener(getPaginationScrollListener(linearLayoutManager));
                    recyclerView.smoothScrollToPosition(0);
                    swipeContainer.setRefreshing(false);
                    searchList = items;
                    adapter.addAll(items);

                    if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                    pd.hide();


                }

                @Override
                public void onFailure(Call<ItemResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                    diconected.setVisibility(View.VISIBLE);
                    pd.hide();


                }
            });

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

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

    private void loadNextPage() {
        Log.d("VadmanTag", "loadNextPage: " + currentPage);

        Call<ItemResponse> call = apiService.getItems(new String[]{LANGUAGE, LOCATION}, currentPage);
        call.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                List<Item> items = response.body().getItems();
                adapter.addAll(items);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();
                ArrayList<Item> newList = new ArrayList<Item>();

                for (Item item1 : searchList) {
                    String name = item1.getLogin().toLowerCase();

                    if (name.contains(newText)) {
                        newList.add(item1);
                    }


                }
                adapter.setFilter(newList);

                return true;
            }
        });
        return true;
    }

    @Override
    protected void onPause() {
        super.onStop();
        pd.dismiss();
    }


    class PagerAdapter extends FragmentPagerAdapter {
        String tabTitles[] = new String[]{"First Tab", "Second Tab"};
        Context context;

        public PagerAdapter(android.support.v4.app.FragmentManager supportFragmentManager, MainActivity context) {
            super(supportFragmentManager);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new BlankFragment();

                case 1:
                    return new BlankFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }
}
