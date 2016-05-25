package com.ypyproductions.cloudplayer;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ypyproductions.cloudplayer.fragments.MainFragment;
import com.ypyproductions.cloudplayer.fragments.QuickControlsFragment;
import com.ypyproductions.dialog.utils.AlertDialogUtils.IOnDialogListener;

import android.content.DialogInterface.OnKeyListener;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.ypyproductions.abtractclass.fragment.IDBFragmentConstants;
import com.ypyproductions.cloudplayer.R;
import com.ypyproductions.cloudplayer.adapter.TrackAdapter;
import com.ypyproductions.cloudplayer.constants.ICloudMusicPlayerConstants;
import com.ypyproductions.cloudplayer.setting.SettingManager;
import com.ypyproductions.dialog.utils.AlertDialogUtils;
import com.ypyproductions.cloudplayer.slidinguppanel.SlidingUpPanelLayout;
import com.ypyproductions.soundclound.ISoundCloundConstants;
import com.ypyproductions.soundclound.SoundCloundAPI;
import com.ypyproductions.soundclound.object.TrackObject;
import com.ypyproductions.task.DBTask;
import com.ypyproductions.task.IDBCallback;
import com.ypyproductions.task.IDBConstantURL;
import com.ypyproductions.task.IDBTaskListener;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.ShareActionUtils;
import com.ypyproductions.utils.StringUtils;
import com.ypyproductions.webservice.DownloadUtils;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.KeyEvent;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.TextView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.widget.Toast;

import org.json.JSONObject;

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
public class MainActivity extends AppCompatActivity implements ICloudMusicPlayerConstants, IDBConstantURL, IDBFragmentConstants, ISoundCloundConstants {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout mListView;
    private TextView mCatTextView;
    private SearchView searchView;
    private Menu mMenu;
    private ArrayList<String> mListSuggestionStr;
    private ArrayList<TrackObject> mListTrackObjects;
    private TrackAdapter mAdapter;
    private TextView mTvNoResult;
    private DBTask mDBTask;
    private ProgressDialog mProgressDialog;
    public SoundCloundAPI mSoundClound = new SoundCloundAPI(SOUNDCLOUND_CLIENT_ID, SOUNDCLOUND_CLIENT_SECRET);

    public static final String TAG = MainActivity.class.getSimpleName();

    private DisplayImageOptions mAvatarOptions;
    public Typeface mTypefaceNormal;
    public Typeface mTypefaceLight;
    public Typeface mTypefaceBold;
    public Typeface mTypefaceLogo;
    private DisplayImageOptions mImgTrackOptions;
    private TrackObject mCurrentTrack;
    protected ProgressDialog progressDialog;
    public static final int BUFFER_SIZE = 1024;


    SlidingUpPanelLayout panelLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setLogo(R.drawable.ic_launcher);

        Fragment mainFragment = new MainFragment();
        
        mainFragment.setArguments(getIntent().getExtras());
        FragmentManager fragmentManager = getSupportFragmentManager();
         android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction()
        			.add(R.id.fragment, mainFragment);
        transaction.commit();

        this.mAvatarOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_account_circle_grey).resetViewBeforeLoading(false).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();
        this.mImgTrackOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.music_note).resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true).build();

        mTypefaceNormal=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        mTypefaceLight=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        mTypefaceBold=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        mTypefaceLogo=Typeface.createFromAsset(getAssets(), "fonts/Biko_Regular.otf");
        panelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        setPanelSlideListeners();
        new initQuickControls().execute("");
        
    }
    private void showDiaglogAboutUs() {
        AlertDialog mDialog = new AlertDialog.Builder(this).setTitle(R.string.title_about_us).setItems(R.array.list_share, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    ShareActionUtils.shareViaEmail(MainActivity.this, YOUR_EMAIL_CONTACT, "", "");
                }
                else if (which == 1) {
                    Intent mIntent = new Intent(MainActivity.this, ShowUrlActivity.class);
                    mIntent.putExtra(KEY_URL, URL_YOUR_WEBSITE);
                    mIntent.putExtra(KEY_HEADER, getString(R.string.title_website));
                    startActivity(mIntent);
                }
                else if (which == 2) {
                    Intent mIntent = new Intent(MainActivity.this, ShowUrlActivity.class);
                    mIntent.putExtra(KEY_URL, URL_YOUR_FACE_BOOK);
                    mIntent.putExtra(KEY_HEADER, getString(R.string.title_facebook));
                    startActivity(mIntent);
                }
                else if (which == 3) {
                    Intent mIntent = new Intent(MainActivity.this, ShowUrlActivity.class);
                    mIntent.putExtra(KEY_URL, URL_YOUR_TWITTER);
                    mIntent.putExtra(KEY_HEADER, getString(R.string.title_twitter));
                    startActivity(mIntent);
                }
                else if (which == 4) {
                    String url = String.format(URL_FORMAT_LINK_APP, getPackageName());
                    ShareActionUtils.goToUrl(MainActivity.this, url);
                }

            }
        }).setPositiveButton(getString(R.string.title_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        mDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        if(GET_TRACK_FROM_PROFILE){
            menuItem.setVisible(false);
        }
        else{
            searchView = (SearchView) menuItem.getActionView();
            searchView.setSubmitButtonEnabled(true);
            searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.title_search) + "</font>"));

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    processSearchData(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                  //  startSuggestion(newText);
                    return false;
                }
            });
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean queryTextFocused) {
                    if (!queryTextFocused) {
                        MenuItem mMenuSearchItem = mMenu.findItem(R.id.action_search);
                        if (mMenuSearchItem != null) {
                            mMenuSearchItem.collapseActionView();
                        }
                        searchView.setQuery("", false);
                    }
                }
            });

            searchView.setOnSuggestionListener(new OnSuggestionListener() {

                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    if (mListSuggestionStr != null && mListSuggestionStr.size() > 0) {
                        searchView.setQuery(mListSuggestionStr.get(position), false);
                        processSearchData(mListSuggestionStr.get(position));
                    }
                    return false;
                }
            });

        }

        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                showDiaglogAboutUs();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void processSearchData(final String query) {
        if (!StringUtils.isEmptyString(query)) {
            final String mQuery = StringUtils.urlEncodeString(query);
            startGetData(false, mQuery);
        }
    }
    private void startGetData(final boolean isRefresh, final String keyword) {
        if (!ApplicationUtils.isOnline(this)) {

            //mListView.onRefreshComplete();

            if (isRefresh) {
                showToast(R.string.info_lose_internet);
            }
            if (mAdapter == null) {
                this.mTvNoResult.setVisibility(View.VISIBLE);
            }
            return;
        }
        mDBTask = new DBTask(new IDBTaskListener() {

            private ArrayList<TrackObject> mListNewTrackObjects;

            @Override
            public void onPreExcute() {
                if (!isRefresh) {
                    showProgressDialog();
                }
            }

            @Override
            public void onDoInBackground() {
                if(GET_TRACK_FROM_PROFILE){
                    mListNewTrackObjects = mSoundClound.getListTrackObjectsOfUser(USER_ID);
                }
                else{
                    mListNewTrackObjects = mSoundClound.getListTrackObjectsByQuery(keyword, 0, 80);
                    if (mListNewTrackObjects != null && mListNewTrackObjects.size() > 0) {
                        SettingManager.setLastKeyword(MainActivity.this, keyword);
                    }
                }

            }

            @Override
            public void onPostExcute() {
                dimissProgressDialog();

                //mListView.onRefreshComplete();

                hiddenVirtualKeyBoard();
                setUpInfo(mListNewTrackObjects);
            }

        });
        mDBTask.execute();

    }
    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void showProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(this.getString(R.string.loading));
            if(!mProgressDialog.isShowing()){
                mProgressDialog.show();
            }
        }
    }
    public void dimissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void hiddenVirtualKeyBoard() {
        if (searchView != null) {
            searchView.clearFocus();
            ApplicationUtils.hiddenVirtualKeyboard(this, searchView);
        }
    }
    private void setUpInfo(ArrayList<TrackObject> mListNewTrackObjects) {

       // mListView.setAdapter(null);

        if (mListTrackObjects != null) {
            mListTrackObjects.clear();
            mListTrackObjects = null;
        }
        this.mListTrackObjects = mListNewTrackObjects;
        if (mListNewTrackObjects != null && mListNewTrackObjects.size() > 0) {
            this.mTvNoResult.setVisibility(View.GONE);
            this.mListView.setVisibility(View.VISIBLE);
            mAdapter = new TrackAdapter(this, mListNewTrackObjects, mTypefaceBold, mTypefaceLight, mImgTrackOptions, mAvatarOptions);

            //mListView.setAdapter(mAdapter);

            mAdapter.setTrackAdapterListener(new TrackAdapter.ITrackAdapterListener() {
                @Override
                public void onDownload(TrackObject mTrackObject) {
                    showAlertDownload(mTrackObject);
                }

                @Override
                public void onListenDemo(TrackObject mTrackObject) {
                    if (!ApplicationUtils.isOnline(MainActivity.this)) {
                        showToast(R.string.info_server_error);
                        return;
                    }
                   // onListenMusicDemo(mTrackObject);
                }
            });
        }
        else {
            this.mTvNoResult.setVisibility(View.VISIBLE);
        }
    }
    private void showAlertDownload(final TrackObject mTrackObject) {
        showFullDialog(R.string.title_confirm, R.string.info_download, R.string.title_ok, R.string.title_cancel, new IDBCallback() {

            @Override
            public void onAction() {
                mCurrentTrack = mTrackObject;
                startGetLinkStream(new IDBCallback() {

                    @Override
                    public void onAction() {
                        dowloadSong(mTrackObject);

                    }
                }, true);
            }
        });
    }

    public void showFullDialog(int titleId, int message,int idPositive,int idNegative, final IDBCallback mDBCallback) {
        Dialog mAlertDialog = AlertDialogUtils.createFullDialog(this, -1, titleId, idPositive, idNegative, message,new IOnDialogListener() {

            @Override
            public void onClickButtonPositive() {
                if(mDBCallback!=null){
                    mDBCallback.onAction();
                }
            }

            @Override
            public void onClickButtonNegative() {

            }
        });
        mAlertDialog.show();
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
    private void dowloadSong(final TrackObject mTrackObject) {
        final File mCacheDir = new File(Environment.getExternalStorageDirectory(), NAME_FOLDER_DOWNLOAD);
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
        final String url = mTrackObject.getLinkStream();
        DBLog.d(TAG, "=================>download url=" + url);

        final String nameFile = mTrackObject.getTitle() + ".mp3";
        final File mFile = new File(mCacheDir, nameFile);
        if (mFile.exists() && mFile.isFile()) {
            showToast(R.string.info_download_exits);
            return;
        }
        mDBTask = new DBTask(new IDBTaskListener() {

            private boolean isDownloadSuccess = false;

            @Override
            public void onPreExcute() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle(R.string.title_download);
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.setOnKeyListener(new OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return true;
                        }
                        return false;
                    }
                });
                progressDialog.show();
            }

            @Override
            public void onDoInBackground() {
                InputStream is = null;
                URL u = null;
                try {
                    u = new URL(url);
                    is = u.openStream();
                    HttpURLConnection huc = (HttpURLConnection) u.openConnection();
                    huc.setReadTimeout(5000);

                    boolean redirect = false;
                    int status = huc.getResponseCode();
                    if (status != HttpURLConnection.HTTP_OK) {
                        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                            redirect = true;
                        }
                    }
                    if (redirect) {
                        String newUrl = huc.getHeaderField("Location");
                        DBLog.d(TAG, "==============>Redirect to URL : " + newUrl);

                        u = new URL(newUrl);
                        is = u.openStream();

                        huc = (HttpURLConnection) u.openConnection();
                        huc.setReadTimeout(5000);
                        status = huc.getResponseCode();
                    }
                    DBLog.d(TAG, "=============>HttpURLConnection=" + status);
                    if (huc != null && is != null && status == HttpURLConnection.HTTP_OK) {
                        int size = huc.getContentLength();
                        FileOutputStream fos = new FileOutputStream(mFile);
                        byte[] buffer = new byte[BUFFER_SIZE];
                        long total = 0;
                        int len1 = 0;
                        while ((len1 = is.read(buffer)) > 0) {
                            total += len1;
                            fos.write(buffer, 0, len1);
                            final int progress = (int) ((total * 100) / size);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog != null) {
                                        progressDialog.setProgress(progress);
                                    }
                                }
                            });
                        }
                        if (fos != null) {
                            fos.close();
                        }
                        isDownloadSuccess = true;
                    }
                }
                catch (MalformedURLException mue) {
                    mue.printStackTrace();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }

            @Override
            public void onPostExcute() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (isDownloadSuccess) {
                    String pathImage = mCacheDir.getAbsolutePath() + "/" + nameFile;
                    String info = String.format(getString(R.string.info_download_success), pathImage);
                    showToast(info);
                }
                else {
                    showToast(R.string.info_download_error);
                }
            }

        });
        mDBTask.execute();
    }
    private void setPanelSlideListeners() {
        panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1);
            }

            @Override
            public void onPanelExpanded(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(0);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }
    private class initQuickControls extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            QuickControlsFragment fragment1 = new QuickControlsFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.quickcontrols_container, fragment1).commitAllowingStateLoss();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }
    }

}
