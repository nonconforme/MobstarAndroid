package com.mobstar.help.take_tour;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobstar.R;

/**
 * Created by lipcha on 25.11.15.
 */
public class ImageDetailFragment extends Fragment {
    private final static String IMAGE_POSITION = "position";
    int position;
    private ImageView mImageView;

    static ImageDetailFragment newInstance(int position) {

        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putInt(IMAGE_POSITION, position);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public ImageDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt(IMAGE_POSITION) : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // image_detail_fragment.xml contains just an ImageView
        final View v = inflater.inflate(R.layout.view_flow_image_item, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imgView1);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (position == 0) {
            mImageView.setBackgroundResource(R.drawable.tour1);
        } else if (position == 1) {
            mImageView.setBackgroundResource(R.drawable.tour2);
        } else if (position == 2) {
            mImageView.setBackgroundResource(R.drawable.tour3);
        } else if (position == 3) {
            mImageView.setBackgroundResource(R.drawable.tour4);
        } else if (position == 4) {
            mImageView.setBackgroundResource(R.drawable.tour5);
        } else if (position == 5) {
            mImageView.setBackgroundResource(R.drawable.tour6);
        } else if (position == 6) {
            mImageView.setBackgroundResource(R.drawable.tour7);
        } else if (position == 7) {
            mImageView.setBackgroundResource(R.drawable.tour8);
        } else if (position == 8) {
            mImageView.setBackgroundResource(R.drawable.tour9);
        } else if (position == 9) {
            mImageView.setBackgroundResource(R.drawable.tour10);
        }

    }
}
