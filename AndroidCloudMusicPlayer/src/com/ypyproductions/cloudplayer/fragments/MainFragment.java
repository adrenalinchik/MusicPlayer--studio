package com.ypyproductions.cloudplayer.fragments;

import com.ypyproductions.cloudplayer.R;
import com.ypyproductions.cloudplayer.adapter.TabViewPagerAdapter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment{
	  	private Toolbar toolbar;
	    private TabLayout tabLayout;
	    private ViewPager viewPager;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_main, container, false);
     

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(2);
        }
 
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);

        
        return rootView;
   }
 
    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new HomeTabFragment(), "HOME");
        adapter.addFragment(new LibraryTabFragment(), "LIBRARY");
        adapter.addFragment(new PlaylistTabFragment(), "PLAYLIST");
        adapter.addFragment(new TophitTabFragment(), "TOP HIT");
        adapter.addFragment(new PlaylistTabFragment(), "GENRES");
        viewPager.setAdapter(adapter);
    }
        
		
}

