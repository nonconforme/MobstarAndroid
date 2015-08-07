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

    private String[] videoGroupVariant = new String[]{"a", "b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c"};
    private Context mContext;
    private int lastCheckedPosition = 0;

    public SplitLayoutAdapter(final Context _context){
        mContext = _context;
    }

    @Override
    public int getCount() {
        return videoGroupVariant.length;
    }

    @Override
    public Object getItem(int position) {
        return videoGroupVariant[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final VideoSplitLayoutVariantsView variantsView;
        if (convertView == null){
            variantsView = new VideoSplitLayoutVariantsView(mContext);
        }
        else variantsView = (VideoSplitLayoutVariantsView) convertView;
        variantsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
