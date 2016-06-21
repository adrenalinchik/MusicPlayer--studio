package com.ypyproductions;

import com.ypyproductions.soundclound.object.TrackObject;

import java.util.ArrayList;

/**
 * Created by taras.fihurnyak on 6/16/2016.
 */
public class SingletonTrackObjectArray {

    private static SingletonTrackObjectArray sTrack;
    public static ArrayList<TrackObject> trackObjectsList;

    private SingletonTrackObjectArray(){
    }

    public static SingletonTrackObjectArray get(ArrayList<TrackObject> trackList){
        trackObjectsList = trackList;
        if (sTrack==null) {
            sTrack = new SingletonTrackObjectArray();
        }
        return sTrack;
    }

}
