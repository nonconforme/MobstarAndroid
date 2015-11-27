package com.mobstar.talentconnect;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.api.new_api_model.Star;
import com.mobstar.home.ShareActivity;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lipcha on 27.11.15.
 */
public class MyStarAdapter extends BaseAdapter {

    private ArrayList<Star> stars;
    private Activity context;
    private LayoutInflater layoutInflater;

    public MyStarAdapter(final Activity context, final ArrayList<Star> stars) {
        this.context = context;
        this.stars = stars;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return stars.size();
    }

    @Override
    public Object getItem(int arg0) {
        return stars.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_item_stars, null);
            viewHolder = new ViewHolder();
            findViews(viewHolder, convertView);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        setupItem(viewHolder, position);
        return convertView;
    }

    private void findViews(final ViewHolder viewHolder, final View convertView){
        viewHolder.llDefault         = (LinearLayout)convertView.findViewById(R.id.llDefault);
        viewHolder.llTalent          = (LinearLayout)convertView.findViewById(R.id.llTalent);
        viewHolder.textStarName      = (TextView) convertView.findViewById(R.id.textStarName);
        viewHolder.imgUserPic        = (ImageView) convertView.findViewById(R.id.imgUserPic);
        viewHolder.imgStarInfo       = (ImageView) convertView.findViewById(R.id.imgStarInfo);
        viewHolder.imgUserPicTalent  = (ImageView)convertView.findViewById(R.id.imgUserPicTalent);
        viewHolder.textStat          = (TextView)convertView.findViewById(R.id.textStat);
        viewHolder.imgShare          = (ImageView)convertView.findViewById(R.id.imgShare);
        viewHolder.textRank          = (TextView)convertView.findViewById(R.id.textRank);
        convertView.setTag(viewHolder);
    }

    private void setupItem(final ViewHolder viewHolder, final int position){
        viewHolder.textStarName.setText(stars.get(position).getProfile().getDisplayName());
        viewHolder.textRank.setText("#" + stars.get(position).getRank());
        viewHolder.textStat.setText("#" + stars.get(position).getStat());


        if(!stars.get(position).isChecked()){
            if(viewHolder.llTalent.getVisibility() == View.VISIBLE){
                viewHolder.llTalent.setVisibility(View.GONE);
            }
        }

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.imgShare:
                        startShareActivity(position);
                        break;
                    case R.id.imgUserPic:
                        startProfileActivity(position);
                        break;
                    case R.id.imgUserPicTalent:
                        startProfileActivity(position);
                        break;
                    case R.id.imgStarInfo:
                        selectStars(position, viewHolder);
                        break;
                    case R.id.llTalent:
                        hideLLTalent(viewHolder);
                        break;
                }
            }
        };

        setListeners(viewHolder, onClickListener);

        viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
        viewHolder.imgUserPicTalent.setImageResource(R.drawable.ic_pic_small);
        if (!stars.get(position).getProfile().getProfileImage().equals("")) {
            Picasso.with(context).
                    load(stars.get(position).getProfile().getProfileImage())
                    .resize(Utility.dpToPx(context, 45), Utility.dpToPx(context, 45))
                    .centerCrop()
                    .placeholder(R.drawable.ic_pic_small)
                    .error(R.drawable.ic_pic_small)
                    .into(viewHolder.imgUserPic);


            Picasso.with(context)
                    .load(stars.get(position).getProfile().getProfileImage())
                    .resize(Utility.dpToPx(context, 45), Utility.dpToPx(context, 45)).centerCrop()
                    .placeholder(R.drawable.ic_pic_small)
                    .error(R.drawable.ic_pic_small)
                    .into(viewHolder.imgUserPicTalent);
        }
    }

    private void hideLLTalent(final ViewHolder viewHolder){
        TranslateAnimation anim = new TranslateAnimation(0f, 0f, 0f, -100f);
        anim.setDuration(300);
        viewHolder.llTalent.setAnimation(anim);
        viewHolder.llTalent.setVisibility(View.GONE);
    }

    private void selectStars(final int position, final ViewHolder viewHolder){
        stars.get(position).setChecked(true);

        for(int i = 0; i < stars.size(); i++){
            if(position != i){
                stars.get(i).setChecked(false);
            }

        }
        TranslateAnimation anim1 = new TranslateAnimation(0f, 0f, -100f, 0f);
        anim1.setDuration(300);

        viewHolder.llTalent.setAnimation(anim1);
        viewHolder.llTalent.setVisibility(View.VISIBLE);
        notifyDataSetChanged();
    }

    private void setListeners(final ViewHolder viewHolder, final View.OnClickListener onClickListener){
        viewHolder.imgShare.setOnClickListener(onClickListener);
        viewHolder.imgUserPic.setOnClickListener(onClickListener);
        viewHolder.imgUserPicTalent.setOnClickListener(onClickListener);
        viewHolder.imgStarInfo.setOnClickListener(onClickListener);
        viewHolder.llTalent.setOnClickListener(onClickListener);
    }

    private void startShareActivity(final int position){
        final Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra("isTalent", true);
        intent.putExtra("UserName", stars.get(position).getProfile().getDisplayName());
        intent.putExtra("UserImg", stars.get(position).getProfile().getProfileImage());
        startActivity(intent);

    }

    private void startActivity(final Intent intent){
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startProfileActivity(int position){
        final Intent intent = new Intent(context, NewProfileActivity.class);
        final UserProfile userProfile = UserProfile.newBuilder()
                .setUserId(stars.get(position).getProfile().getId())
                .setUserName(stars.get(position).getProfile().getDisplayName())
                .setIsMyStar(true)
                .setUserPic(stars.get(position).getProfile().getProfileImage())
                .setUserCoverImage(stars.get(position).getProfile().getCoverImage())
                .build();
        intent.putExtra(NewProfileActivity.USER, userProfile);
        startActivity(intent);

    }



    private class ViewHolder {
        TextView textStarName;
        ImageView imgUserPic;
        ImageView imgStarInfo;
        LinearLayout llTalent,llDefault;
        ImageView imgUserPicTalent;
        TextView textStat;
        ImageView imgShare;
        TextView textRank;
    }
}
