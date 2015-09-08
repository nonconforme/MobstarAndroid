package com.mobstar.home.split;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.mobstar.R;
import com.mobstar.home.split.position_variants.PositionVariant;
import com.mobstar.home.split.position_variants.PositionVariantsFragment;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Utility;

/**
 * Created by vasia on 06.08.15.
 */
public class SplitActivity extends Activity {

    public static final String ENTRY_SPLIT = "entry split";
    private EntryPojo entry;
    private String videoFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        if (getIntent() != null)
            entry = (EntryPojo) getIntent().getSerializableExtra(ENTRY_SPLIT);
        if (videoFilePath == null) {
            setDefaultFilePath();
        }

        if (savedInstanceState == null)
            replaceTopNavigationFragment(new PositionVariantsFragment());
//             replaceTopNavigationFragment(new RecordSplitVideoFragment());
    }


    public void setDefaultFilePath(){
        final String fileName = Utility.GetFileNameFromURl(entry.getVideoLink());
        final String filePath = Utility.getCurrentDirectory(this);
        videoFilePath = filePath + fileName;
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

    public void replaceRecordVideoFragment(final PositionVariant _positionVariant, final Bitmap _imagePreview){
        replaceTopNavigationFragment(RecordSplitVideoFragment.newInstance(_positionVariant, _imagePreview));
    }

    public String getVideoFilePath(){
        return videoFilePath;
    }

    public void setVideoFilePath(final String _filePath){
        videoFilePath = _filePath;
    }

    public EntryPojo getEntry(){
        return entry;
    }

}
