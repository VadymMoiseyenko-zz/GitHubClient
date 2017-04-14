package com.example.vadman_pc.githubclient.controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import com.example.vadman_pc.githubclient.MainFragment;
import com.example.vadman_pc.githubclient.R;
import com.example.vadman_pc.githubclient.RecyclerViewFragment;
import com.example.vadman_pc.githubclient.ViewPagerAdapter;


public class MainActivity extends AppCompatActivity implements MainFragment.OnNameSetListener {


    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    RecyclerViewFragment recyclerViewFragment;
    MainFragment mainFragment;
    FloatingActionButton fab;
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
    int tempPosition;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }


    }


    private void initViews() {
        Log.d("VadmanLog", "begine init Views");
        mainToolbar = (Toolbar) findViewById(R.id.my_toolBar);
        setSupportActionBar(mainToolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tempPosition == 0) {
                    mainFragment.onSearch();
                }

            }
        });


        Log.d("VadmanLog", "Finish ini views");


        //here i work with fragment
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        Log.d("VadmanTag 1", "tabLayout" + tabLayout);
        Log.d("VadmanTag 1", "viewPager" + viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mainFragment = new MainFragment();
        viewPagerAdapter.addFragments(mainFragment, "Home");

        recyclerViewFragment = new RecyclerViewFragment();

        Log.d("VadmanTagHash", "recyclerViewFragment" + recyclerViewFragment.hashCode());


        viewPagerAdapter.addFragments(recyclerViewFragment, "List");



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Toast.makeText(getApplication(), "onPageScrolled" + position, LENGTH_SHORT).show();
                if (tempPosition == 1) {
                    mainFragment.onSearch();
                }
                Log.d("VadmanTagPageScrolled", "position " + position +" positionOffset " + positionOffset + " positionOffsetPixels" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                tempPosition = position;
                if (tempPosition == 1) {
//                    fab.hide();
                } else {
                    fab.show();
                }
//                Toast.makeText(getApplication(), "onPageSelected" + position, LENGTH_SHORT).show();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Toast.makeText(getApplication(), "onPageScrollStateChanged" + state, LENGTH_SHORT).show();
                if (tempPosition == 1) {
                    if (state == 1) {
                        fab.show();
                    } else {
                        fab.hide();
                    }

                }
                Log.d("VadmanTagPageScrolled", "state " + state);

            }
        });
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

    @Override
    public void setInfoForSearch(String location, String language) {
//        RecyclerViewFragment recyclerViewFragment = getFragmentManager().findFragmentById(R.id.recyclerView);
        recyclerViewFragment.updateInfo(location,language);
        recyclerViewFragment.refreshItems();
        viewPager.setCurrentItem(1,true);

    }
}
