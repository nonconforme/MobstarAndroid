package com.mobstar.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;

import com.mobstar.home.new_home_screen.EntryItem;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alexandr on 16.09.2015.
 */
public class PlayerManager {
    private static final String LOG_TAG = PlayerManager.class.getName();
    private MediaPlayer mediaPlayer;
    private Context mContext;
    private EntryItem mEntryItem;
    private static PlayerManager instance;
    private String mFilePath;
    private boolean isMediaPlayerError = false;
    private boolean isVideoFile;

    public static PlayerManager getInstance(){
        if (instance == null){
            instance = new PlayerManager();
        }
        return instance;
    }

    public void init(Context context, EntryItem entryItem, String fileName){
        this.mContext = context;
        this.mEntryItem = entryItem;
        isVideoFile = (mEntryItem.getEntryPojo().getType().equals("video"));
        mFilePath = fileName;
    }

    private void sendRequstAddCount() {
        Log.d(LOG_TAG,"sendRequstAddCount");

    }

    public boolean tryToPlayNew(){
        releaseMP();
        mediaPlayer = new MediaPlayer();
        File file = new File(mFilePath);
        boolean result = false;
        if (file.exists()) {
            try {
                mediaPlayer.setDataSource(mFilePath);

                if (isVideoFile) {
                    Surface surface = new Surface(mEntryItem.getTextureView().getSurfaceTexture());
                    mediaPlayer.setSurface(surface);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }

                // Play video when the media source is ready for
                // playback.
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                         Log.v(LOG_TAG,"setOnPreparedListener.onPrepared");
                    }
                });
                //														mediaPlayer.setLooping(true);
                //Added by Khyati
                mediaPlayer.setLooping(false);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.v(LOG_TAG,"setOnCompletionListener.onCompletion");
                        if (!isMediaPlayerError) {
                            sendRequstAddCount();
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                        } else {
                            isMediaPlayerError = false;
                        }
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.v(LOG_TAG,"setOnErrorListener.onError");
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying())
                                mediaPlayer.pause();
                            isMediaPlayerError = true;
                            mediaPlayer.reset();

                        }
                        return false;
                    }
                });

                mediaPlayer.prepareAsync();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    public boolean tryToPause() {
        Log.v(LOG_TAG, "tryToPause");
        if (mediaPlayer !=null){
            mediaPlayer.pause();
            return true;
        }
        return false;
    }

    private boolean tryToStop() {
        Log.v(LOG_TAG,"tryToStop");
        if (mediaPlayer !=null){
            mediaPlayer.stop();
            return true;
        }
        return false;
    }

    private boolean tryToReset() {
        Log.v(LOG_TAG,"tryToReset");
        if (mediaPlayer !=null){
            mediaPlayer.reset();
            return true;
        }
        return false;
    }

    public boolean finalizePlayer() {
        Log.v(LOG_TAG,"finalizePlayer");
        if (mediaPlayer !=null){
            try {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            return true;
            } catch (Exception e) {
                Log.v(LOG_TAG,"finalizePlayer.error="+e.toString());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
