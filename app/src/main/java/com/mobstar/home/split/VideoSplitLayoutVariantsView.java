package com.mobstar.home.split;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
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

    public VideoSplitLayoutVariantsView(Context context) {
        super(context);
        setupCustomComponentView();
    }


    private void setupCustomComponentView(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View inflatedView = inflater.inflate(R.layout.video_split_layout_variant_view, null);
        findViews(inflatedView);
        addView(inflatedView);
    }

    private void findViews(final View _view){
        frameSelected  = (FrameLayout) _view.findViewById(R.id.flFrameSelected);
        contentContainer = (FrameLayout) _view.findViewById(R.id.flContentContainer);
        ivSelected = (ImageView) _view.findViewById(R.id.ivSelected);
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

}
