package com.ypyproductions.cloudplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ypyproductions.cloudplayer.R;
import com.ypyproductions.soundclound.object.SongObjectLibrary;
import com.ypyproductions.soundclound.object.TrackObject;

import java.util.ArrayList;

/**
 * Created by taras.fihurnyak on 6/27/2016.
 */
public class PhoneSongAdapter extends BaseAdapter{

    private ArrayList<TrackObject> songs;
    private LayoutInflater songInf;

    public PhoneSongAdapter(Context c, ArrayList<TrackObject> theSongs){
        songs=theSongs;
        songInf= LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate
                (R.layout.phone_music_song, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView songDuration = (TextView)songLay.findViewById(R.id.song_duration);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        //get song using position
        TrackObject currSong = songs.get(position);

        //get title, duration and artist strings
        songView.setText(currSong.getTitle());

        long duration = currSong.getDuration() / 1000;
        String minute = String.valueOf((int) (duration / 60));
        String seconds = String.valueOf((int) (duration % 60));
        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        songDuration.setText(minute + ":" + seconds);

        artistView.setText(String.valueOf(currSong.getDescription()));
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
