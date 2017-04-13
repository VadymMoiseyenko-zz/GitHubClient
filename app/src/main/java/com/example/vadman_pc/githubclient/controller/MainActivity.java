package com.example.vadman_pc.githubclient.controller;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.vadman_pc.githubclient.MainFragment;
import com.example.vadman_pc.githubclient.R;
import com.example.vadman_pc.githubclient.RecyclerViewFragment;
import com.example.vadman_pc.githubclient.ViewPagerAdapter;


public class MainActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    RecyclerViewFragment recyclerViewFragment;
    boolean isFirst;

//    List<Item> searchList;

//    private RecyclerView recyclerView;
//    TextView diconected;
//    private Item item;
//    ProgressDialog pd;
//    PaginationAdapter adapter;
//    private SwipeRefreshLayout swipeContainer;
//    public static LinearLayoutManager linearLayoutManager;

//    private static Context context;


    Toolbar mainToolbar;
    private String textForSearching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        context = getApplicationContext();
        Log.d("VadmanTag 1", "activity_main OnCreate");

        if (savedInstanceState != null) {
            isFirst = false;
        }


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
        Log.d("VadmanTag 1", "tabLayout" + tabLayout);
        Log.d("VadmanTag 1", "viewPager" + viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new MainFragment(), "Home");

        recyclerViewFragment = new RecyclerViewFragment();

        Log.d("VadmanTagHash", "recyclerViewFragment" + recyclerViewFragment.hashCode());


        viewPagerAdapter.addFragments(recyclerViewFragment, "List");


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
                textForSearching = newText;
                recyclerViewFragment.searchingItems(textForSearching);

                return true;
            }
        });
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("key", textForSearching);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textForSearching = savedInstanceState.getString("key");

    }
}
