package com.mobstar.home.new_home_screen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobstar.BaseActivity;
import com.mobstar.ProfileActivity;
import com.mobstar.R;
import com.mobstar.api.RestClient;
import com.mobstar.custom.swipe_card_view.SwipeCardView;
import com.mobstar.home.CommentActivity;
import com.mobstar.home.ShareActivity;
import com.mobstar.home.StatisticsActivity;
import com.mobstar.home.split.SplitActivity;
import com.mobstar.info.report.InformationReportActivity;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.upload.MessageActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by lipcha on 14.09.15.
 */
public class EntryItem extends RecyclerView.ViewHolder implements View.OnClickListener, SwipeCardView.OnSwipeDismissListener {

    private TextView textUserName, textDescription, textTime,textViews, buttonVideoSplit;
    private ImageView imageFrame;
    private ProgressBar progressbar;
    private TextureView textureView;
    private FrameLayout btnShare;
    private TextView btnFollow;
    private FrameLayout btnInfo;
    private ImageView ivAudioIcon;
    private ImageView imgUserPic;
    private TextView textCommentCount;
    private ImageView imgPlaceHolder;
    private FrameLayout flPlaceHolder;
    private FrameLayout layoutStatastics;
    private TextView textStatasticCount;
    private ImageView imgMsg,ivIndicator;
    private SwipeCardView swipeCardView;

    private View votingYes;
    private View votingNo;

    private int position;

    private BaseActivity baseActivity;
    private EntryPojo entryPojo;
    private SharedPreferences preferences;
    private OnRemoveEntryListener onRemoveEntryListener;


    public EntryItem(View itemView) {
        super(itemView);
        findView(itemView);
    }

    public int getPos(){
        return position;
    }


    private void findView(final View convertView){
        buttonVideoSplit = (TextView) convertView.findViewById(R.id.splitVideoButton);
        textUserName = (TextView) convertView.findViewById(R.id.textUserName);
        textTime = (TextView) convertView.findViewById(R.id.textTime);
        textViews = (TextView) convertView.findViewById(R.id.textViews);
        textDescription = (TextView) convertView.findViewById(R.id.textDescription);
        imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);
        progressbar = (ProgressBar) convertView.findViewById(R.id.progressbar);
        textureView = (TextureView) convertView.findViewById(R.id.textureView);
        btnShare = (FrameLayout) convertView.findViewById(R.id.btnShare);
        btnFollow = (TextView) convertView.findViewById(R.id.btnFollow);
        btnInfo = (FrameLayout) convertView.findViewById(R.id.btnInfo);
        layoutStatastics = (FrameLayout) convertView.findViewById(R.id.layoutStatastic);
        textStatasticCount = (TextView) convertView.findViewById(R.id.textStatasticCount);
        ivAudioIcon = (ImageView) convertView.findViewById(R.id.ivAudioIcon);
        textCommentCount = (TextView) convertView.findViewById(R.id.textCommentCount);
        imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
        imgPlaceHolder = (ImageView) convertView.findViewById(R.id.imgPlaceHolder);
        flPlaceHolder = (FrameLayout) convertView.findViewById(R.id.flPlaceHolder);
        imgMsg=(ImageView) convertView.findViewById(R.id.imgMsg);
        ivIndicator=(ImageView) convertView.findViewById(R.id.ivIndicator);
        swipeCardView = (SwipeCardView) convertView.findViewById(R.id.swipe_card_view);
        votingNo = convertView.findViewById(R.id.voting_no);
        votingYes = convertView.findViewById(R.id.voting_yes);
    }

    public void init(final EntryPojo _entryPojo, int _position, final BaseActivity _activity, OnRemoveEntryListener _onRemoveEntryListener){
        entryPojo = _entryPojo;
        baseActivity = _activity;
        position = _position;
        onRemoveEntryListener = _onRemoveEntryListener;
        setupViews();
        setListeners();
        setupImage();
        initItemContentType();
    }

    private void setupViews(){

        swipeCardView.setSwipeLeftViewIndicator(votingNo);
        swipeCardView.setSwipeRightViewIndicator(votingYes);

        textCommentCount.setText(entryPojo.getTotalComments());
        textUserName.setText(entryPojo.getUserDisplayName());
        textDescription.setText(Utility.unescape_perl_string(entryPojo.getDescription()));

        textTime.setText(entryPojo.getCreated());
        textViews.setText(entryPojo.getTotalViews());

        textStatasticCount.setText(entryPojo.getUpVotesCount());
        if (preferences == null)
            preferences = baseActivity.getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);
        if(preferences.getString("userid", "0").equalsIgnoreCase(entryPojo.getUserID())){
            btnFollow.setVisibility(View.GONE);
        }
        else {
            btnFollow.setVisibility(View.VISIBLE);
            if (entryPojo.getIsMyStar() != null) {
                if (!entryPojo.getIsMyStar().equalsIgnoreCase("0")) {
                    btnFollow.setBackground(baseActivity.getResources().getDrawable(R.drawable.yellow_btn));
                    btnFollow.setText(baseActivity.getString(R.string.following));
                } else {
                    btnFollow.setBackground(baseActivity.getResources().getDrawable(R.drawable.selector_oval_button));
                    btnFollow.setText(baseActivity.getString(R.string.follow));
                }
            }
        }

        try {
            if (!entryPojo.getType().equals("video") || entryPojo.getSplitVideoId() != null) {
                buttonVideoSplit.setEnabled(false);
                buttonVideoSplit.setTextColor(baseActivity.getResources().getColor(R.color.comment_color_state_disable));
            } else if (entryPojo.getType().equals("video")){
                buttonVideoSplit.setEnabled(true);
                buttonVideoSplit.setTextColor(baseActivity.getResources().getColor(R.color.comment_color));
                buttonVideoSplit.setOnClickListener(this);
            }
        }
        catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    private void setListeners(){
        btnFollow.setOnClickListener(this);
        imgMsg.setOnClickListener(this);
        textUserName.setOnClickListener(this);
        imgUserPic.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        layoutStatastics.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        textCommentCount.setOnClickListener(this);
        swipeCardView.setOnSwipeDismissListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.splitVideoButton:
                startSplitActivity();
                break;
            case R.id.btnFollow:
                onClickBtnFollow();
                break;
            case R.id.imgMsg:
                startMessageActivity();
                break;
            case R.id.textUserName:
            case R.id.imgUserPic:
                startProfileActivity();
                break;
            case R.id.btnShare:
                startShareActivity();
                break;
            case R.id.layoutStatastic:
                startStatisticsActivity();
                break;
            case R.id.btnInfo:
                startInformationReportActivity();
                break;
            case R.id.textCommentCount:
                startCommentActivity();
                break;
        }
    }

    private void setupImage(){
        if (entryPojo.getIAmStar()!=null && entryPojo.getIAmStar().equalsIgnoreCase("1")) {
            Picasso.with(baseActivity).load(R.drawable.msg_act_btn).into(imgMsg);
        }
        else{
            Picasso.with(baseActivity).load(R.drawable.msg_btn).into(imgMsg);
        }

        if (entryPojo.getProfileImage().equals("")) {
            imgUserPic.setImageResource(R.drawable.ic_pic_small);
        } else {
            imgUserPic.setImageResource(R.drawable.ic_pic_small);

            Picasso.with(baseActivity).load(entryPojo.getProfileImage()).resize(Utility.dpToPx(baseActivity, 45), Utility.dpToPx(baseActivity, 45)).centerCrop()
                    .placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(imgUserPic);
        }


    }

    private void initItemContentType(){
        switch (entryPojo.getType()) {
            case "image":
                setImageContentType();
                break;
            case "audio":
                setAudioContentType();
                break;
            case "video":
                setVideoContentType();
                break;
        }
    }

    private void setImageContentType(){
        Picasso.with(baseActivity).load(R.drawable.indicator_image).into(ivIndicator);
        // Log.v(Constant.TAG, "image position " + position);

        ivAudioIcon.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        imgPlaceHolder.setVisibility(View.VISIBLE);
        imgPlaceHolder.setImageResource(R.drawable.image_placeholder);
        imageFrame.setVisibility(View.GONE);

        Picasso.with(baseActivity).load(entryPojo.getImageLink())
                .placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).into(imageFrame, new Callback() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                progressbar.setVisibility(View.GONE);
                imageFrame.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                // TODO Auto-generated method stub

            }
        });
    }

    private void setAudioContentType(){
        Picasso.with(baseActivity).load(R.drawable.indicator_audio).into(ivIndicator);

        ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
        ivAudioIcon.setVisibility(View.INVISIBLE);
        progressbar.setVisibility(View.VISIBLE);
        imgPlaceHolder.setVisibility(View.VISIBLE);
        imgPlaceHolder.setImageResource(R.drawable.audio_placeholder);
        imageFrame.setVisibility(View.GONE);

//        Picasso.with(mContext).load(arrEntryPojos.get(position).getImageLink())
//                .into(viewHolder.imageFrame, new Callback() {
//
//                    @Override
//                    public void onSuccess() {
//                        // TODO Auto-generated method stub
//                        viewHolder.progressbar.setVisibility(View.GONE);
//                        viewHolder.imageFrame.setVisibility(View.VISIBLE);
//
//                        if (!listDownloadingFile.contains(sFileName)) {
//
//                            //							File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);
//
//                            try {
//                                File file = new File(FILEPATH + sFileName);
//                                if (file != null && !file.exists()) {
//
//                                    listDownloadingFile.add(sFileName);
//
//                                    Log.d(LOG_TAG, "sFileName=" + sFileName);
////                            Log.d(LOG_TAG, "arrEntryPojos.get(position).getVideoLink()="+arrEntryPojos.get(position).getVideoLink());
//                                    Log.d(LOG_TAG, "what to dowmload=" + arrEntryPojos.get(position).getDescription());
//
//                                    if (Utility.isNetworkAvailable(mContext)) {
////                                        downloadFile(sFileName, listDownloadingFile, arrEntryPojos.get(position).getAudioLink(), new FileAsyncHttpResponseHandler(file) {
////
////                                            @Override
////                                            public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
////                                                Log.d(LOG_TAG,"Download fail audio=>"+arrEntryPojos.get(position).getVideoLink());
////                                            }
////
////                                            @Override
////                                            public void onSuccess(int arg0, Header[] arg1, File file) {
////                                                Log.d(LOG_TAG, "download audio file=" + file.getName());
////                                                listDownloadingFile.remove(file.getName());
////                                                mActivity.runOnUiThread(new Runnable() {
////                                                    @Override
////                                                    public void run() {
////                                                        notifyDataSetChanged();
////                                                    }
////                                                });
////                                            }
////                                        });
//                                        downloadFile(FILEPATH + sFileName, listDownloadingFile, arrEntryPojos.get(position).getAudioLink(), new OnFileDownloadCallback() {
//                                            @Override
//                                            public void onDownload(File file) {
//                                                Log.d(LOG_TAG, "download audio file=" + file.getName());
//                                                listDownloadingFile.remove(file.getName());
//                                                mActivity.runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        notifyDataSetChanged();
//                                                    }
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onFailure(String errorMessage) {
//
//                                            }
//                                        });
//                                    } else {
//                                        Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    viewHolder.progressbar.setVisibility(View.GONE);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        } else {
//                            viewHolder.progressbar.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                    @Override
//                    public void onError() {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
    }

    private void setVideoContentType(){
        Picasso.with(baseActivity).load(R.drawable.indicator_video).into(ivIndicator);
        ivAudioIcon.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        imgPlaceHolder.setVisibility(View.VISIBLE);
        imgPlaceHolder.setImageResource(R.drawable.video_placeholder);
        imageFrame.setVisibility(View.GONE);

        Picasso.with(baseActivity).load(entryPojo.getVideoThumb())
                .placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).into(imageFrame, new Callback() {
            @Override
            public void onSuccess() {
                progressbar.setVisibility(View.GONE);
                imageFrame.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

            }
        });
    }


    private void onClickBtnFollow(){
//        if (entryPojo.getIsMyStar()!=null && !entryPojo.getIsMyStar().equalsIgnoreCase("0")) {
//            //unfollow
//            Utility.ShowProgressDialog(baseActivity, baseActivity.getString(R.string.loading));
//
//            if (Utility.isNetworkAvailable(baseActivity)) {
//                unFollowUserId = entryPojo.getUserID();
//                new DeleteStarCall(entryPojo.getUserID()).start();
//            } else {
//                Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//                Utility.HideDialog(mContext);
//            }
//        } else {
//            Utility.ShowProgressDialog(mContext, getString(R.string.loading));
//
//            if (Utility.isNetworkAvailable(mContext)) {
//
//                new AddStarCall(arrEntryPojos.get(pos).getUserID()).start();
//
//                final Dialog dialog = new Dialog(mContext, R.style.DialogAnimationTheme);
//                dialog.setContentView(R.layout.dialog_add_star);
//                dialog.show();
//
//                Timer timer = new Timer();
//                TimerTask task = new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        dialog.dismiss();
//                    }
//                };
//                timer.schedule(task, 1000);
//
//            } else {
//
//                Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//                Utility.HideDialog(mContext);
//            }
//
//        }
    }

    private void startCommentActivity(){
        final Intent intent = new Intent(baseActivity, CommentActivity.class);
        intent.putExtra("entry_id", entryPojo.getID());
        startActivity(intent);
        baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startInformationReportActivity(){
        final Intent intent = new Intent(baseActivity, InformationReportActivity.class);
        intent.putExtra("entry", entryPojo);
        startActivity(intent);
        baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startStatisticsActivity(){
        final Intent intent = new Intent(baseActivity, StatisticsActivity.class);
        intent.putExtra("entry", entryPojo);
        startActivity(intent);
        baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startShareActivity(){
        final Intent intent = new Intent(baseActivity, ShareActivity.class);
        intent.putExtra("entry", entryPojo);
        startActivity(intent);
        baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startProfileActivity(){
        final Intent intent = new Intent(baseActivity, ProfileActivity.class);
        intent.putExtra("UserID", entryPojo.getUserID());
        intent.putExtra("UserName", entryPojo.getUserName());
        intent.putExtra("UserDisplayName", entryPojo.getUserDisplayName());
        intent.putExtra("UserPic", entryPojo.getProfileImage());
        intent.putExtra("UserCoverImage", entryPojo.getProfileCover());
        intent.putExtra("IsMyStar", entryPojo.getIsMyStar());
        intent.putExtra("UserTagline", entryPojo.getTagline());
        startActivity(intent);
        baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startSplitActivity(){
        if (entryPojo.getVideoLink() == null)
            return;
        Intent intent = new Intent(baseActivity, SplitActivity.class);
        intent.putExtra(SplitActivity.ENTRY_SPLIT, entryPojo);
        startActivity(intent);
        baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startMessageActivity(){
        if (entryPojo.getIAmStar() != null && entryPojo.getIAmStar().equalsIgnoreCase("1")) {
            //following
            final Intent intent=new Intent(baseActivity, MessageActivity.class);
            intent.putExtra("recipent", entryPojo.getUserID());
            intent.putExtra("isDisableCompose",true);
            startActivity(intent);
        }
    }

    private void startActivity(final Intent intent){
        baseActivity.startActivity(intent);
    }

    @Override
    public void onSwipeLeft() {
        dislikeRequest();
        if (onRemoveEntryListener != null)
            onRemoveEntryListener.onRemoveEntry(position);
    }

    @Override
    public void onSwipeRight() {
        likeRequest();
        if (onRemoveEntryListener != null)
            onRemoveEntryListener.onRemoveEntry(position);
    }

    private void likeRequest(){
        final HashMap<String, String> params = new HashMap<>();
        params.put("entry", entryPojo.getID());
        params.put("type", "up");
        RestClient.getInstance(baseActivity).postRequest(Constant.VOTE, params, null);
    }

    private void dislikeRequest(){
        final HashMap<String, String> params = new HashMap<>();
        params.put("entry", entryPojo.getID());
        params.put("type", "down");
        RestClient.getInstance(baseActivity).postRequest(Constant.VOTE, params, null);
    }

    public interface OnRemoveEntryListener{
        void onRemoveEntry(int position);
    }

    public EntryPojo getEntryPojo() {
        return entryPojo;
    }

    public TextureView getTextureView() {
        return textureView;
    }

    public void playVideoState() {
        flPlaceHolder.setVisibility(View.GONE);
        ivAudioIcon.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        textureView.setVisibility(View.VISIBLE);
    }


    public void playAudioState() {
        flPlaceHolder.setVisibility(View.VISIBLE);
        imgPlaceHolder.setVisibility(View.GONE);
        ivAudioIcon.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        textureView.setVisibility(View.GONE);
        imageFrame.setVisibility(View.VISIBLE);
    }
    public void pauseVideoState() {
        flPlaceHolder.setVisibility(View.GONE);
        ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
        ivAudioIcon.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);
        textureView.setVisibility(View.VISIBLE);
    }
    public void pauseAudioState() {
        flPlaceHolder.setVisibility(View.VISIBLE);
        imgPlaceHolder.setVisibility(View.GONE);
        ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
        ivAudioIcon.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);
        textureView.setVisibility(View.GONE);
        imageFrame.setVisibility(View.VISIBLE);
    }

//    public FrameLayout getFlPlaceHolder() {
//        return flPlaceHolder;
//    }
//
//    public ImageView getIvAudioIcon() {
//        return ivAudioIcon;
//    }
}
