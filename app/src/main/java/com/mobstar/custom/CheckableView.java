package com.mobstar.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mobstar.R;

/**
 * Created by lipcha on 08.09.15.
 */
public class CheckableView extends FrameLayout implements View.OnClickListener, Checkable {

    private boolean mChecked;
    private ImageView ivLeftImage, ivRightImage;
    private FrameLayout flCheckedFrame;
    private CustomTextviewBold tvTitle;
    private String title;
    private Drawable leftImage;
    private boolean isVisibleChecked;
    private OnCheckedChangeListener onCheckedChangeListener;

    public CheckableView(Context context) {
        super(context);
    }

    public CheckableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CheckableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setTitle(final String _title){
        if (title == null)
            return;
        title = _title;
        if (tvTitle != null)
            tvTitle.setText(title);
    }

    public void setLeftImageDrawable(final Drawable _drawable){
        if (_drawable == null)
            return;
        leftImage = _drawable;
        if (ivLeftImage != null)
            ivLeftImage.setImageDrawable(leftImage);
    }

    public void setVisibleChecked(final boolean _visible){
        isVisibleChecked = _visible;
        if (isVisibleChecked)
            ivRightImage.setVisibility(VISIBLE);
        else ivRightImage.setVisibility(GONE);
    }

    private void init(final AttributeSet attributeSet){
        getAttributeData(attributeSet);

        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View inflatedView = inflater.inflate(R.layout.view_checkable, null);
        findViews(inflatedView);
        inflatedView.setOnClickListener(this);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDimension(R.dimen.checked_view_height)));
        addView(inflatedView, layoutParams);
        setupView();
        refreshView();
    }

    private void getAttributeData(final AttributeSet attrs){
        final TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CheckableView, 0, 0);
        title = array.getString(R.styleable.CheckableView_checkedTitle);
        isVisibleChecked = array.getBoolean(R.styleable.CheckableView_isChowChecked, false);
        leftImage = array.getDrawable(R.styleable.CheckableView_leftImage);
    }

    private void findViews(final View inflatedView){
        ivLeftImage = (ImageView) inflatedView.findViewById(R.id.ivLeftImage);
        ivRightImage = (ImageView) inflatedView.findViewById(R.id.ivRightImage);
        tvTitle = (CustomTextviewBold) inflatedView.findViewById(R.id.tvTitle);
        flCheckedFrame = (FrameLayout) inflatedView.findViewById(R.id.flChecked);
    }

    private void setupView(){
        if (leftImage != null)
            ivLeftImage.setImageDrawable(leftImage);
        if (isVisibleChecked)
            ivRightImage.setVisibility(VISIBLE);
        else ivRightImage.setVisibility(GONE);
        if (title != null)
             tvTitle.setText(title);
    }

    public void setOnCheckedChangeListener(final OnCheckedChangeListener _onCheckedChangeListener){
        onCheckedChangeListener = _onCheckedChangeListener;
    }


    @Override
    public void onClick(View v) {
        toggle();
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshView();
            if (onCheckedChangeListener != null)
                onCheckedChangeListener.onCheckedChange(this, mChecked);
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

    private void refreshView(){
        if (ivRightImage.getVisibility() == VISIBLE){
            if (mChecked)
                ivRightImage.setImageResource(R.drawable.check_act);
            else ivRightImage.setImageResource(R.drawable.check);
        }
        if (!mChecked)
            flCheckedFrame.setVisibility(VISIBLE);
        else flCheckedFrame.setVisibility(GONE);
    }

    public interface OnCheckedChangeListener{
        void onCheckedChange(final CheckableView _view, boolean _checked);
    }
}
