package com.ypyproductions.cloudplayer;

import com.ypyproductions.cloudplayer.fragments.MainFragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;

/**
 * 
 * 
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.ypyproductions.com
 * @Project:AndroidCloundMusicPlayer
 * @Date:Dec 14, 2014
 * 
 */
public class MainActivity extends AppCompatActivity  {

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        Fragment mainFragment = new MainFragment();
        
        mainFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();
         android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction()
        			.add(R.id.fragment_container, mainFragment);
        transaction.commit();
        
    }
}
