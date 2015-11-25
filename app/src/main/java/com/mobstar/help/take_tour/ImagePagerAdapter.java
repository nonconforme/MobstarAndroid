package com.mobstar.help.take_tour;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by lipcha on 25.11.15.
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private final int mSize;

    public ImagePagerAdapter(FragmentManager fm, int size) {
        super(fm);
        mSize = size;
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageDetailFragment.newInstance(position);
    }
}
