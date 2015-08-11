package com.mobstar.home.split;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.mobstar.R;
import com.mobstar.home.split.position_variants.PositionVariant;
import com.mobstar.home.split.position_variants.PositionVariantsFragment;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;

/**
 * Created by vasia on 06.08.15.
 */
public class SplitActivity extends Activity {

    private EntryPojo entry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
        if (getIntent() != null)
            entry = (EntryPojo) getIntent().getSerializableExtra(Constant.ENTRY);
        if (savedInstanceState == null)
             replaceTopNavigationFragment(new PositionVariantsFragment());
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

    public EntryPojo getEntry(){
        return entry;
    }

}
