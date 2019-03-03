package com.munhj.meloplayer;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.io.IOException;

import Adapter.ViewAdapter;
import Fragments.ListFragment;
import Fragments.musicFragment;
import Model.ListItem;
import MusicHandler.MyMusicHandler;

public class MainActivity extends AppCompatActivity {

    public static final int STORAGE_PERMISSION = 1;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    public static MyMusicHandler musicHandler;
    public static ListItem item;
    public static ListItem savedItem;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        musicHandler = new MyMusicHandler();


        bottomNav.setSelectedItemId(R.id.nav_music_list);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch(menuItem.getItemId()) {
                        case R.id.nav_music_list:
                            ListFragment listFragment = new ListFragment();
                            listFragment.setBottomNavigationView(bottomNav);
                            selectedFragment = listFragment;
                            break;
                        case R.id.nav_music_player:
                            musicFragment musicFragment = new musicFragment();
                            musicFragment.setBottomNavigationView(bottomNav);
                            selectedFragment = musicFragment;
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };


    public void startMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        stopService(serviceIntent);
    }


}
