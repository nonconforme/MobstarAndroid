package com.mobstar.home.split;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mobstar.R;

/**
 * Created by vasia on 06.08.15.
 */
public class VideoSplitLayoutVariantsView extends FrameLayout implements Checkable {

    private boolean mChecked;
    private FrameLayout frameSelected, contentContainer;
    private ImageView ivSelected;
    private VideoPositionVariants positionVariants;
    private FrameLayout comingSoonMessage;

    public VideoSplitLayoutVariantsView(Context context, VideoPositionVariants _positionVariants) {
        super(context);
        positionVariants = _positionVariants;
        setupCustomComponentView();
    }


    private void setupCustomComponentView(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View inflatedView = inflater.inflate(R.layout.video_split_layout_variant_view, null);
        findViews(inflatedView);
        addView(inflatedView);
        if (positionVariants.isWorkingPositionVariant())
            comingSoonMessage.setVisibility(GONE);
        else comingSoonMessage.setVisibility(VISIBLE);
        contentContainer.addView(getContentViewFromPositionVariants());
    }

    private void findViews(final View _view){
        frameSelected  = (FrameLayout) _view.findViewById(R.id.flFrameSelected);
        contentContainer = (FrameLayout) _view.findViewById(R.id.flContentContainer);
        ivSelected = (ImageView) _view.findViewById(R.id.ivSelected);
        comingSoonMessage = (FrameLayout) _view.findViewById(R.id.flComingSoonMessage);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        if (checked){
            ivSelected.setVisibility(VISIBLE);
            frameSelected.setVisibility(VISIBLE);
        }
        else {
            ivSelected.setVisibility(GONE);
            frameSelected.setVisibility(GONE);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    public View getContentViewFromPositionVariants(){
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        int layoutId = R.layout.video_variant_origin_left;
        switch (positionVariants.getVariant()){
            case ORIGIN_LEFT:
                layoutId = R.layout.video_variant_origin_left;
                break;
            case ORIGIN_RIGHT:
                layoutId = R.layout.video_variant_origin_right;
                break;
            case ORIGIN_RIGHT_TOP:

                break;
            case ORIGIN_FULLSCREEN:

                break;
            case ORIGIN_TOP:
                layoutId = R.layout.video_variant_origin_top;
                break;
            case ORIGIN_BOTTOM:
                layoutId = R.layout.video_variant_origin_bottom;
                break;
        }
        return layoutInflater.inflate(layoutId, null);
    }

}
