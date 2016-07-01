/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.ypyproductions.cloudplayer.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ypyproductions.SingletonTrackObjectArray;
import com.ypyproductions.abtractclass.fragment.IDBFragmentConstants;
import com.ypyproductions.cloudplayer.MainActivity;
import com.ypyproductions.cloudplayer.R;
import com.ypyproductions.cloudplayer.constants.ICloudMusicPlayerConstants;
import com.ypyproductions.cloudplayer.setting.PlayPauseButton;
import com.ypyproductions.cloudplayer.setting.SettingManager;
import com.ypyproductions.cloudplayer.setting.SquareImageView;
import com.ypyproductions.soundclound.ISoundCloundConstants;
import com.ypyproductions.soundclound.object.TrackObject;
import com.ypyproductions.task.DBTask;
import com.ypyproductions.task.IDBCallback;
import com.ypyproductions.task.IDBConstantURL;
import com.ypyproductions.task.IDBTaskListener;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.ShareActionUtils;
import com.ypyproductions.utils.StringUtils;
import com.ypyproductions.webservice.DownloadUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class QuickControlsFragment extends Fragment implements ICloudMusicPlayerConstants, IDBConstantURL, IDBFragmentConstants, ISoundCloundConstants {


    public static View topContainer;
    private static ProgressBar mProgress;
    private PlayPauseButton mPlayPause;
    private PlayPauseButton mPlayPausePlayer;
    private TextView mTitle;
    private TextView mArtist;
    private TextView mExtraInfo;
    private ImageView mAlbumArt, mBlurredArt;
    private String mArtUrl;
    private View rootView;
    private View playPauseWrapper;
    private View playPauseWrapperPlayer;
    public static final String TAG = MainActivity.class.getSimpleName();
    public ArrayList<TrackObject> mListTrackObjects;
    private ProgressDialog mProgressDialog;
    private SeekBar mSeekbar;
    private TextView mTvCurrentTime;
    private TextView mTvDuration;
    private Button mBtnPlay;
    private TextView mTvTitleSongs;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private MediaPlayer mPlayer;
    private TrackObject mCurrentTrack;
    private ProgressBar mProgressBar;
    private ProgressBar mCardProgressBar;
    //private AdView adView;
    InterstitialAd mInterstitial;
    private DisplayImageOptions mAvatarOptions;
    private Button mBtnPrev;
    private Button mBtnNext;
    private TextView mTvLink;

    private ImageView mNowPlayingImage;
    private ProgressBar mQuickProgressBar;
    private TextView mQuickTitle;
    private TextView mQuickArtist;

    public Typeface mTypefaceNormal;
    public Typeface mTypefaceLight;
    public Typeface mTypefaceBold;
    public Typeface mTypefaceLogo;


    private boolean duetoplaypause = false;

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;

            if (!mPlayPause.isPlayed() && !mPlayPausePlayer.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPausePlayer.setPlayed(true);
                mPlayPause.startAnimation();
                mPlayPausePlayer.startAnimation();
            } else {
                mPlayPause.setPlayed(false);
                mPlayPausePlayer.setPlayed(false);
                mPlayPause.startAnimation();
                mPlayPausePlayer.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playOrPause();
                }
            }, 200);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);

        this.rootView = rootView;

        mTypefaceNormal= Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), "fonts/Roboto-Regular.ttf");
        mTypefaceLight=Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
        mTypefaceBold=Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), "fonts/Roboto-Bold.ttf");
        mTypefaceLogo=Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), "fonts/Biko_Regular.otf");

        mPlayPause = (PlayPauseButton) rootView.findViewById(R.id.play_pause);
        playPauseWrapper = rootView.findViewById(R.id.play_pause_wrapper);
        mPlayPause.setEnabled(true);
        playPauseWrapper.setOnClickListener(mButtonListener);
        mPlayPause.setColor(ContextCompat.getColor(mPlayPause.getContext(), R.color.white));

        mPlayPausePlayer = (PlayPauseButton) rootView.findViewById(R.id.playpause);
        playPauseWrapperPlayer = rootView.findViewById(R.id.playpausewrapper);
        mPlayPausePlayer.setEnabled(true);
        playPauseWrapperPlayer.setOnClickListener(mButtonListener);
        mPlayPausePlayer.setColor(ContextCompat.getColor(mPlayPause.getContext(), R.color.white));


        mSeekbar = (SeekBar) rootView.findViewById(R.id.seekBar1);
        mTvCurrentTime = (TextView) rootView.findViewById(R.id.tv_current_time);
        mTvCurrentTime.setTypeface(mTypefaceLight);

        mTvDuration = (TextView) rootView.findViewById(R.id.tv_duration);
        mTvDuration.setTypeface(mTypefaceLight);

        mTvLink = (TextView) rootView.findViewById(R.id.tv_link);
        mTvLink.setTypeface(mTypefaceLight);

        mTvTitleSongs = (TextView) rootView.findViewById(R.id.tv_name_songs);
        mTvTitleSongs.setTypeface(mTypefaceBold);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        mCardProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar2);

        mBtnPrev = (Button) rootView.findViewById(R.id.btn_prev);
        mBtnNext = (Button) rootView.findViewById(R.id.btn_next);

        mNowPlayingImage = (ImageView) rootView.findViewById(R.id.album_art_nowplayingcard);
        mQuickProgressBar = (ProgressBar) rootView.findViewById(R.id.song_progress_normal);
        mQuickTitle = (TextView) rootView.findViewById(R.id.title);


        SettingManager.setFirstTime(getContext().getApplicationContext(), true);
        setUpPlayMusicLayout();
        return rootView;
    }

    private void setUpPlayMusicLayout() {

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        int currentPos = (int) (progress * mCurrentTrack.getDuration() / 100f);
                        DBLog.d(TAG, "=================>currentPos=" + currentPos);
                        if (mPlayer != null) {
                            mPlayer.seekTo(currentPos);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTrack();
            }
        });

        mTvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTrack != null && !StringUtils.isEmptyString(mCurrentTrack.getPermalinkUrl())) {
                    ShareActionUtils.goToUrl(getActivity(), mCurrentTrack.getPermalinkUrl());
                }
            }
        });
        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevTrack();
            }
        });
    }

    public void initPlayerControls(ArrayList<TrackObject> mListTrackObjects) {
        if (mListTrackObjects != null && mListTrackObjects.size() > 0) {
            final TrackObject trackObject = mListTrackObjects.get(0);

            mCurrentTrack = trackObject;

            mQuickProgressBar.setProgress(0);
            mQuickTitle.setText(trackObject.getTitle());

            mTvTitleSongs.setText(trackObject.getTitle());
            mTvLink.setText(trackObject.getPermalinkUrl());
            mTvCurrentTime.setText("00:00");
            mSeekbar.setProgress(0);
            new DownloadImageTask().execute(trackObject.getArtworkUrl());

            startGetLinkStream(new IDBCallback() {
                @Override
                public void onAction() {
                    onCreateMedia(trackObject);
                }
            }, false);


        }
    }

    private void onCreateMedia(final TrackObject mTrackObject){
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mProgressBar.setVisibility(View.GONE);
                mCardProgressBar.setVisibility(View.GONE);

                mTvLink.setVisibility(View.VISIBLE);

                startUpdatePosition();
                //requestNewInterstitial();

            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onMusicStop();
                nextTrack();

            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                showToast(R.string.info_play_error);
                onMusicStop();
                mCurrentTrack.setLinkStream("");
                return false;
            }
        });
        String url = mTrackObject.getLinkStream();
        try {
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            onMusicStop();
        }
        catch (SecurityException e) {
            e.printStackTrace();
            onMusicStop();
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
            onMusicStop();
        }
        catch (IOException e) {
            onMusicStop();
        }
    }

    private void playOrPause() {
        try {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.start();
                    //requestNewInterstitial();


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void nextTrack() {
        if (mListTrackObjects != null && mListTrackObjects.size() > 0 && mCurrentTrack != null) {
            int size = mListTrackObjects.size();
            int currentIndex = mListTrackObjects.indexOf(mCurrentTrack);
            currentIndex++;
            if (currentIndex < size) {
                TrackObject mTrackObject = mListTrackObjects.get(currentIndex);
                onMusicStop();
                onListenMusicDemo(mTrackObject);
            }
        }
    }
    private void prevTrack() {
        if (mListTrackObjects != null && mListTrackObjects.size() > 0 && mCurrentTrack != null) {
            int currentIndex = mListTrackObjects.indexOf(mCurrentTrack);
            currentIndex--;
            if (currentIndex >= 0) {
                TrackObject mTrackObject = mListTrackObjects.get(currentIndex);
                onMusicStop();
                onListenMusicDemo(mTrackObject);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onMusicStop();
    }

    public void onMusicStop() {
        mHandler.removeCallbacksAndMessages(null);
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayPause.setPlayed(false);
                mPlayPausePlayer.setPlayed(false);
                mPlayPause.startAnimation();
                mPlayPausePlayer.startAnimation();
                mPlayer.release();
                mPlayer = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onListenMusicDemo(final TrackObject mTrackObject) {
        mCurrentTrack = mTrackObject;
        mQuickProgressBar.setProgress(0);
        mQuickTitle.setText(mTrackObject.getTitle());

        mTvTitleSongs.setText(mTrackObject.getTitle());
        mTvLink.setText(mTrackObject.getPermalinkUrl());
        mTvCurrentTime.setText("00:00");
        mProgressBar.setVisibility(View.VISIBLE);
        mCardProgressBar.setVisibility(View.VISIBLE);
        mSeekbar.setProgress(0);
        new DownloadImageTask().execute(mCurrentTrack.getArtworkUrl());

        long duration = mTrackObject.getDuration() / 1000;
        String minute = String.valueOf((int) (duration / 60));
        String seconds = String.valueOf((int) (duration % 60));
        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        mTvDuration.setText(minute + ":" + seconds);
        startGetLinkStream(new IDBCallback() {
            @Override
            public void onAction() {
                onCreateAndPlayMedia(mTrackObject);
            }
        }, false);

    }


    private Drawable ImageOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, null);
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class DownloadImageTask extends AsyncTask <String, Void, Drawable>{

        @Override
        protected Drawable doInBackground(String... params) {
            return ImageOperations(params[0]);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            mNowPlayingImage.setImageDrawable(result);
        }

        @Override
        protected void onPreExecute() {
        }

    }


    private void startGetLinkStream(final IDBCallback mCallback, final boolean isShowProgress) {
        if (mCurrentTrack != null) {
            String linkStream = mCurrentTrack.getLinkStream();
            if (!StringUtils.isEmptyString(linkStream)) {
                if (mCallback != null) {
                    mCallback.onAction();
                }
                return;
            }
            DBTask mDBTask = new DBTask(new IDBTaskListener() {
                private String finalUrl;

                @Override
                public void onPreExcute() {
                    if (isShowProgress) {
                        showProgressDialog();
                    }
                }

                @Override
                public void onDoInBackground() {
                    if (mCurrentTrack.isStreamable()) {
                        finalUrl = getLinkStreamFromSoundClound(mCurrentTrack.getId());
                    }
                    if (StringUtils.isEmptyString(finalUrl)) {
                        finalUrl = String.format(FORMAT_URL_SONG, mCurrentTrack.getId(), SOUNDCLOUND_CLIENT_ID);
                    }
                }

                @Override
                public void onPostExcute() {
                    if (isShowProgress) {
                        dimissProgressDialog();
                    }
                    DBLog.d(TAG, "========>final Url=" + finalUrl);
                    if (!StringUtils.isEmptyString(finalUrl)) {
                        mCurrentTrack.setLinkStream(finalUrl);
                    }
                    if (mCallback != null) {
                        mCallback.onAction();
                    }
                }

            });
            mDBTask.execute();
        }
    }
    public void showProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(this.getString(R.string.loading));
            if(!mProgressDialog.isShowing()){
                mProgressDialog.show();
            }
        }
    }
    public static String getLinkStreamFromSoundClound(long id) {
        final String manualUrl = String.format(FORMAT_URL_SONG, String.valueOf(id), SOUNDCLOUND_CLIENT_ID);
        String dataServer = DownloadUtils.downloadString(manualUrl);
        DBLog.d(TAG, "=========>dataServer=" + dataServer);
        String finalUrl = null;
        if (!StringUtils.isEmptyString(dataServer)) {
            try {
                JSONObject mJsonObject = new JSONObject(dataServer);
                finalUrl = mJsonObject.getString("http_mp3_128_url");
            }
            catch (Exception e) {
                e.printStackTrace();
                finalUrl = manualUrl;
            }
        }
        return finalUrl;
    }
    public void dimissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void onCreateAndPlayMedia(final TrackObject mTrackObject) {
        if(mPlayer==null){
            mPlayer = new MediaPlayer();
        }
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mProgressBar.setVisibility(View.GONE);
                mCardProgressBar.setVisibility(View.GONE);

                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
                mPlayPausePlayer.setPlayed(true);
                mPlayPausePlayer.startAnimation();

                mTvLink.setVisibility(View.VISIBLE);

                mPlayer.start();

                startUpdatePosition();
                //requestNewInterstitial();

            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onMusicStop();
                nextTrack();

            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                showToast(R.string.info_play_error);
                onMusicStop();
                mCurrentTrack.setLinkStream("");
                return false;
            }
        });
        String url = mTrackObject.getLinkStream();
        try {
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            onMusicStop();
        }
        catch (SecurityException e) {
            e.printStackTrace();
            onMusicStop();
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
            onMusicStop();
        }
        catch (IOException e) {
            onMusicStop();
        }

    }
    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    private void startUpdatePosition() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (mPlayer != null && mCurrentTrack != null) {

                    int current = mPlayer.getCurrentPosition() / 1000;
                    String minute = String.valueOf((int) (current / 60));
                    String seconds = String.valueOf((int) (current % 60));
                    if (minute.length() < 2) {
                        minute = "0" + minute;
                    }
                    if (seconds.length() < 2) {
                        seconds = "0" + seconds;
                    }
                    mTvCurrentTime.setText(minute + ":" + seconds);

                    int percent = (int) (100f * ((float) mPlayer.getCurrentPosition() / (float) mCurrentTrack.getDuration()));
                    mSeekbar.setProgress(percent);
                    mQuickProgressBar.setProgress(percent);
                    if (current < mCurrentTrack.getDuration()) {
                        startUpdatePosition();

                    }
                }
            }
        }, 1000);
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        topContainer = rootView.findViewById(R.id.topContainer);

    }

}
