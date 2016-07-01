package com.ypyproductions.cloudplayer.fragments;

import com.ypyproductions.cloudplayer.R;
import com.ypyproductions.cloudplayer.adapter.PhoneSongAdapter;
import com.ypyproductions.soundclound.object.TrackObject;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LibraryTabFragment  extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<TrackObject> songList;
    private ListView songView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songList = new ArrayList<TrackObject>();
        Collections.sort(songList, new Comparator<TrackObject>() {
            public int compare(TrackObject a, TrackObject b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.library_tab_fragment, container, false);

        songView = (ListView)view.findViewById(R.id.phoneMusicList);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        emptyList = (TextView)view.findViewById(R.id.empty_song_list);
        //songView.setEmptyView(emptyList);
        PhoneSongAdapter songAdt = new PhoneSongAdapter(getContext().getApplicationContext(), songList);
        songView.setAdapter(songAdt);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        fetchMusicSongs();
                                    }
                                }
        );

    }

    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisDuration = musicCursor.getLong(durationColumn);
                songList.add(new TrackObject(thisId, thisDuration, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    private void fetchMusicSongs() {
        swipeRefreshLayout.setRefreshing(true);
        getSongList();
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        songList.clear();
        fetchMusicSongs();
    }
}
