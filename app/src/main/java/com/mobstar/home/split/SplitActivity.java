package com.mobstar.home.split;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.mobstar.R;
import com.mobstar.home.split.ffmpeg.AfterDoneBackground;
import com.mobstar.home.split.ffmpeg.CropBackground;
import com.mobstar.home.split.position_variants.PositionVariant;
import com.mobstar.home.split.position_variants.PositionVariantsFragment;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import org.apache.http.Header;

import java.io.File;

/**
 * Created by vasia on 06.08.15.
 */
public class SplitActivity extends Activity {

    private EntryPojo entry;
    private String videoFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        if (getIntent() != null)
            entry = (EntryPojo) getIntent().getSerializableExtra(Constant.ENTRY);
        if (savedInstanceState == null)
             replaceTopNavigationFragment(new PositionVariantsFragment());
//             replaceTopNavigationFragment(new RecordSplitVideoFragment());
        if (videoFilePath == null)
            downloadVideoFile();
    }

    @Override
    public void onBackPressed() {
        final int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public final void replaceFragmentWithBackStack(final Fragment _fragment) {
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragmentContainer, _fragment)
                .addToBackStack(null)
                .commit();

    }

    public final void replaceTopNavigationFragment(final Fragment _fragment) {
        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, _fragment)
                .commit();
    }

    public void replaceCropVideoFragment(final PositionVariant _positionVariant){
        replaceFragmentWithBackStack(CropVideoFragment.newInstance(entry.getVideoThumb(), _positionVariant));
    }

    public void replaceRecordVideoFragment(){
        replaceFragmentWithBackStack(new RecordSplitVideoFragment());
    }

    public String getVideoFilePath(){

        return videoFilePath;
    }

    public EntryPojo getEntry(){
        return entry;
    }

    public void downloadVideoFile(){
        final String sFileName = Utility.GetFileNameFromURl(entry.getVideoLink());
        try {
            final String currentDirectory = Environment.getExternalStorageDirectory().getPath()
                    + "/Android/data/" + this.getPackageName() +"/";
            final File file = new File(currentDirectory + sFileName);
            if (file != null && !file.exists()) {

                if (Utility.isNetworkAvailable(this)) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    final int DEFAULT_TIMEOUT = 60 * 1000;

                    client.setTimeout(DEFAULT_TIMEOUT);
                    client.get(entry.getVideoLink(), new FileAsyncHttpResponseHandler(file) {

                        @Override
                        public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
                            videoFilePath="error";
                        }

                        @Override
                        public void onSuccess(int arg0, Header[] arg1, File file) {
                            videoFilePath = currentDirectory + sFileName;
//                            cropFunction(videoFilePath);
                        }
                    });
                }
                else {
                    Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
                }

            } else {
                videoFilePath = currentDirectory + sFileName;
//                cropFunction(videoFilePath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cropFunction(String filePath) {
        final String newpath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO, this).toString();
        new CropBackground(this, filePath, Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO, this).toString(), new AfterDoneBackground() {
            @Override
            public void onAfterDone() {
                videoFilePath = newpath;
            }
        }).runTranscoding();
    }
}
