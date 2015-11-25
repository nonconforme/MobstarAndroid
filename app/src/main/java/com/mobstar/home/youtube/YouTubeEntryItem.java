package com.mobstar.home.youtube;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.home.new_home_screen.EntryItem;
import com.mobstar.player.YouTubePlayerManager;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Utility;

/**
 * Created by lipcha on 10.11.15.
 */
public class YouTubeEntryItem extends EntryItem {

    private RelativeLayout rlVotingContainer;

    public YouTubeEntryItem(View itemView, boolean isEnableSwipe) {
        super(itemView, isEnableSwipe);
    }

    @Override
    protected void onClickContainerPlayer() {

    }

    @Override
    protected void findView(View convertView) {
        super.findView(convertView);
        rlVotingContainer = (RelativeLayout) convertView.findViewById(R.id.rlVotingContainer);
    }

    @Override
    public void init(EntryP _entryPojo, int _position, BaseActivity _activity, OnChangeEntryListener _onChangeEntryListener) {
        super.init(_entryPojo, _position, _activity, _onChangeEntryListener);
        rlVotingContainer.setVisibility(View.GONE);
        rlVotingContainer.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.background_transparent));
    }

    @Override
    protected void setYouTubeContentType() {
        YouTubePlayerManager.getInstance().initialize(baseActivity, getEntryPojo().getEntryFile(0).getPath(), getContainerPlayer());
    }

    @Override
    public void onStartSwipe() {
        super.onStartSwipe();
        rlVotingContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelSwipe() {
        super.onCancelSwipe();
        rlVotingContainer.setVisibility(View.GONE);
    }

    @Override
    public void onSwipeRight() {
        rlVotingContainer.setVisibility(View.GONE);
        super.onSwipeRight();

    }

    @Override
    public void onSwipeLeft() {
        rlVotingContainer.setVisibility(View.GONE);
        dislikeRequest();
        Utility.disLikeDialog(baseActivity, new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isRemoveItemAfterVotingNo) {
                    if (onChangeEntryListener != null)
                        onChangeEntryListener.onRemoveEntry(getPos());
                } else swipeCardView.resetTopView();
            }
        });
    }
}
