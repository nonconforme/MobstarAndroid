package com.mobstar.home.split.position_variants;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by vasia on 06.08.15.
 */
public class PositionVariantsAdapter extends BaseAdapter {

    private ArrayList<VideoPositionVariantsItem> videoGroupVariants;
    private Context mContext;
    private int lastCheckedPosition = 0;

    public PositionVariantsAdapter(final Context _context){
        mContext = _context;
        setupTestItems();
    }

    private void setupTestItems(){
        videoGroupVariants = new ArrayList<>();
        videoGroupVariants.add(new VideoPositionVariantsItem(PositionVariant.ORIGIN_RIGHT, true));
        videoGroupVariants.add(new VideoPositionVariantsItem(PositionVariant.ORIGIN_LEFT, true));
        videoGroupVariants.add(new VideoPositionVariantsItem(PositionVariant.ORIGIN_RIGHT_TOP, true));
        videoGroupVariants.add(new VideoPositionVariantsItem(PositionVariant.ORIGIN_FULLSCREEN, true));
        videoGroupVariants.add(new VideoPositionVariantsItem(PositionVariant.ORIGIN_TOP, true));
        videoGroupVariants.add(new VideoPositionVariantsItem(PositionVariant.ORIGIN_BOTTOM, true));
    }

    public PositionVariant getSelectedPositionVariant(){
        return videoGroupVariants.get(lastCheckedPosition).getVariant();
    }

    @Override
    public int getCount() {
        return videoGroupVariants.size();
    }

    @Override
    public Object getItem(int position) {
        return videoGroupVariants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final VideoSplitPositionVariantsView variantsView;
        if (convertView == null){
            variantsView = new VideoSplitPositionVariantsView(mContext, videoGroupVariants.get(position));
        }
        else variantsView = (VideoSplitPositionVariantsView) convertView;
        variantsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoGroupVariants.get(position).isWorkingPositionVariant()) {
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
