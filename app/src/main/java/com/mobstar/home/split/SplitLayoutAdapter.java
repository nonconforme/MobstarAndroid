package com.mobstar.home.split;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by vasia on 06.08.15.
 */
public class SplitLayoutAdapter extends BaseAdapter {

    private ArrayList<VideoPositionVariants> videoGroupVariant;
    private Context mContext;
    private int lastCheckedPosition = 0;

    public SplitLayoutAdapter(final Context _context){
        mContext = _context;
        setupTestItems();
    }

    private void setupTestItems(){
        videoGroupVariant = new ArrayList<>();
        videoGroupVariant.add(new VideoPositionVariants(PositionVariant.ORIGIN_RIGHT, true));
        videoGroupVariant.add(new VideoPositionVariants(PositionVariant.ORIGIN_LEFT, true));
        videoGroupVariant.add(new VideoPositionVariants(PositionVariant.ORIGIN_RIGHT_TOP, false));
        videoGroupVariant.add(new VideoPositionVariants(PositionVariant.ORIGIN_FULLSCREEN, false));
        videoGroupVariant.add(new VideoPositionVariants(PositionVariant.ORIGIN_TOP, false));
        videoGroupVariant.add(new VideoPositionVariants(PositionVariant.ORIGIN_BOTTOM, false));
    }

    @Override
    public int getCount() {
        return videoGroupVariant.size();
    }

    @Override
    public Object getItem(int position) {
        return videoGroupVariant.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final VideoSplitLayoutVariantsView variantsView;
        if (convertView == null){
            variantsView = new VideoSplitLayoutVariantsView(mContext, videoGroupVariant.get(position));
        }
        else variantsView = (VideoSplitLayoutVariantsView) convertView;
        variantsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoGroupVariant.get(position).isWorkingPositionVariant()) {
                    return;
                }
                if (lastCheckedPosition != position) {
                    lastCheckedPosition = position;
                    notifyDataSetChanged();
                }
            }
        });
        variantsView.setChecked(lastCheckedPosition == position);
        return variantsView;
    }

}
