package com.mobstar.home.youtube;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.mobstar.R;
import com.mobstar.upload.UploadFileActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lipcha on 02.11.15.
 */
public class YouTubeListActivity extends Activity implements View.OnClickListener, OnSelectVideoListener {

    public static final String ACCOUNT_KEY                = "accountName";
    private static final int REQUEST_ACCOUNT_PICKER       = 2;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    private static final int REQUEST_AUTHORIZATION        = 3;

    private ImageButton btnClose;
    private ListView playList;
    private GoogleAccountCredential credential;
    private String mChosenAccountName;
    private String categoryId;
    private String subCategory;
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = new GsonFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_list);
        findViews();
        setListeners();
        getArgs();
        setupCredential();
        chooseAccount();
    }

    private void findViews(){
        btnClose = (ImageButton) findViewById(R.id.btnClose);
        playList = (ListView) findViewById(R.id.lvYouTubeList);
    }

    private void setListeners(){
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnClose:
                onBackPressed();
                break;
        }
    }

    private void getArgs(){
        final Bundle args = getIntent().getExtras();
        if(args != null) {
            if(args.containsKey("categoryId")) {
                categoryId = args.getString("categoryId");
                subCategory = args.getString("subCat");
            }
        }
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    private void setupCredential(){
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(Auth.SCOPES));
        // set exponential backoff policy
        credential.setBackOff(new ExponentialBackOff());
        loadAccount();

        credential.setSelectedAccountName(mChosenAccountName);
    }

    private void loadAccount() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        mChosenAccountName = sp.getString(ACCOUNT_KEY, null);
        invalidateOptionsMenu();
    }

    private void saveAccount() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        sp.edit().putString(ACCOUNT_KEY, mChosenAccountName).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(
                            AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mChosenAccountName = accountName;
                        credential.setSelectedAccountName(accountName);
                        saveAccount();
                        loadData();
                    }
                }
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
        }
    }

    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        }
    }

    private void loadData() {
        if (mChosenAccountName == null) {
            return;
        }

        loadUploadedVideos();
    }

    private void loadUploadedVideos() {
        if (mChosenAccountName == null) {
            return;
        }

        Utility.ShowProgressDialog(YouTubeListActivity.this, getString(R.string.loading));
        new AsyncTask<Void, Void, List<VideoData>>() {
            @Override
            protected List<VideoData> doInBackground(Void... voids) {
                YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                        credential).setApplicationName(Constant.APP_NAME)
                        .build();

                try {
                    /*
                     * Now that the user is authenticated, the app makes a
					 * channels list request to get the authenticated user's
					 * channel. Returned with that data is the playlist id for
					 * the uploaded videos.
					 * https://developers.google.com/youtube
					 * /v3/docs/channels/list
					 */
                    ChannelListResponse clr = youtube.channels()
                            .list("contentDetails").setMine(true).execute();

                    // Get the user's uploads playlist's id from channel list
                    // response
                    String uploadsPlaylistId = clr.getItems().get(0)
                            .getContentDetails().getRelatedPlaylists()
                            .getUploads();

                    List<VideoData> videos = new ArrayList<VideoData>();

                    // Get videos from user's upload playlist with a playlist
                    // items list request
                    PlaylistItemListResponse pilr = youtube.playlistItems()
                            .list("id,contentDetails")
                            .setPlaylistId(uploadsPlaylistId)
                            .setMaxResults(20l).execute();
                    List<String> videoIds = new ArrayList<String>();

                    // Iterate over playlist item list response to get uploaded
                    // videos' ids.
                    for (PlaylistItem item : pilr.getItems()) {
                        videoIds.add(item.getContentDetails().getVideoId());
                    }

                    // Get details of uploaded videos with a videos list
                    // request.
                    VideoListResponse vlr = youtube.videos()
                            .list("id,snippet,status")
                            .setId(TextUtils.join(",", videoIds)).execute();

                    // Add only the public videos to the local videos list.
                    for (Video video : vlr.getItems()) {
                        if ("public".equals(video.getStatus()
                                .getPrivacyStatus())) {
                            VideoData videoData = new VideoData();
                            videoData.setVideo(video);
                            videos.add(videoData);
                        }
                    }

                    // Sort videos by title
                    Collections.sort(videos, new Comparator<VideoData>() {
                        @Override
                        public int compare(VideoData videoData,
                                           VideoData videoData2) {
                            return videoData.getTitle().compareTo(
                                    videoData2.getTitle());
                        }
                    });

                    return videos;

                } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                    showGooglePlayServicesAvailabilityErrorDialog(availabilityException
                            .getConnectionStatusCode());
                } catch (UserRecoverableAuthIOException userRecoverableException) {
                    startActivityForResult(
                            userRecoverableException.getIntent(),
                            REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    Utility.logAndShow(YouTubeListActivity.this, Constant.APP_NAME, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<VideoData> videos) {
                Utility.HideDialog(YouTubeListActivity.this);
                if (videos == null) {
                    return;
                }
                    setupPlayList(videos);
            }

        }.execute((Void) null);
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, YouTubeListActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private void setupPlayList(List<VideoData> videos){
        final YouTubePlayListAdapter adapter = new YouTubePlayListAdapter(videos, this, this);
        playList.setAdapter(adapter);
    }

    @Override
    public void onSelectYouTubeVideo(VideoData videoData) {
        startUploadFileActivity(videoData);
    }

    private void startUploadFileActivity(VideoData videoData){
        Intent intent = new Intent(this, UploadFileActivity.class);
        intent.putExtra("type", "video_youtube");
        intent.putExtra("categoryId", categoryId);
        intent.putExtra(UploadFileActivity.YOUTUBE_VIDEO, new YouTubeVideo(videoData));
        if(subCategory != null && subCategory.length()>0){
            intent.putExtra("subCat", subCategory);
        }
        startActivityForResult(intent, 26);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

//    private class UploadBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(REQUEST_AUTHORIZATION_INTENT)) {
//                Log.d(TAG, "Request auth received - executing the intent");
//                Intent toRun = intent
//                        .getParcelableExtra(REQUEST_AUTHORIZATION_INTENT_PARAM);
//                startActivityForResult(toRun, REQUEST_AUTHORIZATION);
//            }
//        }
//    }
}
