package com.example.vadman_pc.githubclient.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.vadman_pc.githubclient.*;
import com.example.vadman_pc.githubclient.model.Item;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {



    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    List<Item> searchList;

//    private RecyclerView recyclerView;
//    TextView diconected;
    private Item item;
    ProgressDialog pd;
    PaginationAdapter adapter;
//    private SwipeRefreshLayout swipeContainer;
    public static LinearLayoutManager linearLayoutManager;

    private static Context context;


    Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        context = getApplicationContext();

        initViews();



    }


    private void initViews() {
        Log.d("VadmanLog", "begine init Views");
        mainToolbar = (Toolbar) findViewById(R.id.my_toolBar);
        setSupportActionBar(mainToolbar);



        Log.d("VadmanLog", "Finish ini views");

        //here i work with fragment
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new MainFragment(),"Home");
        viewPagerAdapter.addFragments(new RecyclerViewFragment(),"List");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);






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



}
