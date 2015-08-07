package com.mobstar.home.split;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.mobstar.R;
import com.mobstar.custom.CropImageView;
import com.mobstar.utils.Constant;
import com.squareup.picasso.Picasso;

/**
 * Created by vasia on 06.08.15.
 */
public class CropVideoFragment extends Fragment implements View.OnClickListener {

    private Button btnNext, btnBack;
    private CropImageView ivVideoImage;
    private SplitActivity mSplitActivity;
    private String videoThumb;

    public static CropVideoFragment newInstance(final String videoThumb){
        final CropVideoFragment fragment = new CropVideoFragment();
        Bundle args = new Bundle();
        args.putString(Constant.IMAGE, videoThumb);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSplitActivity = (SplitActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null)
            videoThumb = args.getString(Constant.IMAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_crop_video, container, false);
        findViews(inflatedView);
        setListeners();
        setupVideoImage();
        return inflatedView;
    }

    private void findViews(final View _view){
        btnBack = (Button) _view.findViewById(R.id.btnBack);
        btnNext = (Button) _view.findViewById(R.id.btnNext);
        ivVideoImage = (CropImageView) _view.findViewById(R.id.ivCropImage);
    }

    private void setListeners(){
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void setupVideoImage(){
        if (videoThumb != null)
        Picasso.with(mSplitActivity).load(videoThumb)
                .placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).into(ivVideoImage);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                mSplitActivity.onBackPressed();
                break;
            case R.id.btnNext:
                mSplitActivity.replaceFragmentWithBackStack(new RecordSplitVideoFragment());
                break;
        }
    }
}
