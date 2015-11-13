package com.mobstar.home.youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.home.youtube.OnSelectVideoListener;
import com.mobstar.home.youtube.VideoData;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lipcha on 03.11.15.
 */

public class YouTubePlayListAdapter extends BaseAdapter {

    private List<VideoData> mVideos;
    private Context mContext;
    private OnSelectVideoListener onSelectVideoListener;

    public YouTubePlayListAdapter(List<VideoData> videos, Context _context, OnSelectVideoListener _videoListener) {
        mVideos = videos;
        mContext = _context;
        onSelectVideoListener = _videoListener;
    }

    @Override
    public int getCount() {
        return mVideos.size();
    }

    @Override
    public Object getItem(int i) {
        return mVideos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mVideos.get(i).getYouTubeId().hashCode();
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup container) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.you_tube_play_list_item, container, false);
        }

        final VideoData video = mVideos.get(position);
        ((TextView) convertView.findViewById(android.R.id.text1))
                .setText(video.getTitle());
        Picasso.with(mContext).load(video.getThumbUri()).into((ImageView) convertView.findViewById(R.id.thumbnail));

        convertView.findViewById(R.id.main_target).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onSelectVideoListener != null)
                            onSelectVideoListener.onSelectYouTubeVideo(mVideos.get(position));
                    }
                });
        return convertView;
    }
}
