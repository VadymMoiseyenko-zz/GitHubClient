package com.example.vadman_pc.githubclient;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Vadman_PC on 11.04.2017.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();

    public void addFragments(Fragment fragment, String titles) {
        this.fragments.add(fragment);
        this.tabTitles.add(titles);

    }



    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return tabTitles.get(position);
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = fragments.get(position);
//
//        if (fragment != null) {
//            return fragment;
//        } else {
//
//            return  super.instantiateItem(container, position);
//
//        }
//    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object aux = super.instantiateItem(container, position);
        Fragment fragmentTemp = fragments.get(position);

        //Update the references to the Fragments we have on the view pager
        if(position==0){
            fragmentTemp = (MainFragment)aux;
            fragments.set(position, fragmentTemp);
        }
        else{
            fragmentTemp = (RecyclerViewFragment) aux;
            fragments.set(position, fragmentTemp);
        }


        return aux;
    }


}
