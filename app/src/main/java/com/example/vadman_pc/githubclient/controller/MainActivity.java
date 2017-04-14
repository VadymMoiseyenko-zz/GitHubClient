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

    FloatingActionButton fab;
    boolean isFirst;
    Toolbar mainToolbar;
    private String textForSearching;
    int tempPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }


    private void initViews() {
        mainToolbar = (Toolbar) findViewById(R.id.my_toolBar);
        setSupportActionBar(mainToolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //it hide soft   Keyboard when  app start

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new MainFragment(), "Home");



        viewPagerAdapter.addFragments(new RecyclerViewFragment(), "List");



                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Toast.makeText(getApplication(), "onPageScrolled" + position, LENGTH_SHORT).show();
                if (tempPosition == 1) {
                    ((MainFragment)viewPagerAdapter.getItem(0)).onSearch();
                }
                Log.d("VadmanTagPageScrolled", "position " + position +" positionOffset " + positionOffset + " positionOffsetPixels" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                tempPosition = position;
                if (tempPosition == 1) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tempPosition == 0) {
                    ((MainFragment)viewPagerAdapter.getItem(0)).onSearch();
                }
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
                textForSearching = newText;
                ((RecyclerViewFragment)viewPagerAdapter.getItem(1)).searchingItems(textForSearching);

                return true;
            }
        });
        return true;
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        viewPager.setCurrentItem(1,true);
    }



    @Override
    public void setInfoForSearch(String location, String language) {
        ((RecyclerViewFragment)viewPagerAdapter.getItem(1)).updateInfo(location,language);
        ((RecyclerViewFragment)viewPagerAdapter.getItem(1)).refreshItems();
        viewPager.setCurrentItem(1,true);
    }
}
