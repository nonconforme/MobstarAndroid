package com.mobstar.home.youtube;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;

import com.mobstar.home.new_home_screen.EntryItem;
import com.mobstar.player.YouTubePlayerManager;
import com.mobstar.utils.Utility;

/**
 * Created by lipcha on 10.11.15.
 */
public class YouTubeEntryItem extends EntryItem {

    public YouTubeEntryItem(View itemView, boolean isEnableSwipe) {
        super(itemView, isEnableSwipe);
    }

    @Override
    protected void onClickContainerPlayer() {

    }

    @Override
    protected void setYouTubeContentType() {
        YouTubePlayerManager.getInstance().initialize(baseActivity, getEntryPojo().getVideoLink(), getContainerPlayer());
    }



    @Override
    public void onSwipeLeft() {
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
