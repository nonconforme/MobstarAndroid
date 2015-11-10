package com.mobstar.player;

import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.home.youtube.Auth;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by lipcha on 04.11.15.
 */
public class YouTubePlayerManager implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener {

    private static final String YOUTUBE_FRAGMENT_TAG = "youtube";


    private String videoUrl;
    private FrameLayout playerContainer;

    public YouTubePlayerManager() {
    }

    public static YouTubePlayerManager getInstance(){
        return new YouTubePlayerManager();
    }

    public void initialize(final BaseActivity activity, final String _videoUrl, FrameLayout _playerContainer){
        videoUrl = _videoUrl;
        playerContainer = _playerContainer;
        playerContainer.setId(R.id.youTubePlayerContainer);
        popPlayerFromBackStack(activity);
        final YouTubePlayerSupportFragment playerFragment = YouTubePlayerSupportFragment.newInstance();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.youTubePlayerContainer, playerFragment, YOUTUBE_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
        playerFragment.initialize(Auth.KEY, this);
    }

    public void cancelPlayer(final BaseActivity activity) {
        final YouTubePlayerSupportFragment youTubePlayerFragment = getYouTubePlayerFragment(activity);
        if (youTubePlayerFragment != null) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(youTubePlayerFragment)
                    .commit();
        }
        playerContainer.removeAllViews();
        playerContainer.setId(View.NO_ID);
        playerContainer = null;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
//        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
//        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
        youTubePlayer.setShowFullscreenButton(false);
        youTubePlayer.setPlayerStateChangeListener(this);
        youTubePlayer.loadVideo(getYouTubeVideoId());
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
    }

    private YouTubePlayerSupportFragment getYouTubePlayerFragment(final BaseActivity activity){
        return (YouTubePlayerSupportFragment) activity.getSupportFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG);
    }

    public boolean popPlayerFromBackStack(final BaseActivity activity) {
        if (activity.getFragmentManager().findFragmentByTag(YOUTUBE_FRAGMENT_TAG) != null) {
            activity.getFragmentManager().popBackStack();
            return false;
        }
        return true;
    }

    private String getYouTubeVideoId()  {
        String id = "";
        List<NameValuePair> params = null;
        try {
            params = URLEncodedUtils.parse(new URI(videoUrl), "UTF-8");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assert params != null;
        for (int i = 0; i < params.size(); i ++){
            if (params.get(i).getName().equalsIgnoreCase("v"))
                id = params.get(i).getValue();
            return id;
        }
        return id;
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
