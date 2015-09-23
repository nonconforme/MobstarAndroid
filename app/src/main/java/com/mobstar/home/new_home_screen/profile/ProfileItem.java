package com.mobstar.home.new_home_screen.profile;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.custom.CustomTextview;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.fanconnect.FansActivity;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

/**
 * Created by lipcha on 22.09.15.
 */
public class ProfileItem extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView textUserName, textUserDisplayName, textTagline, textFollowers;
    private ImageView imgUserPic, imgCoverPage;
    private BaseActivity baseActivity;
    private UserProfileData userData;

    public ProfileItem(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(final View convertView){
        textUserDisplayName = (CustomTextviewBold)convertView.findViewById(R.id.textUserDisplayName);
        textTagline = (CustomTextview)convertView.findViewById(R.id.textTagline);
        textFollowers = (CustomTextview)convertView.findViewById(R.id.textFollowers);
        imgUserPic = (ImageView)convertView.findViewById(R.id.imgUserPic);
        imgCoverPage = (ImageView)convertView.findViewById(R.id.imgCoverPage);
    }

    public void init(BaseActivity _baseActivity, UserProfileData _userData){
        baseActivity = _baseActivity;
        userData = _userData;
        setupViews();
        setListeners();
    }

    private void setListeners(){
        textFollowers.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textFollowers:
                startFansActivity();
                break;
        }
    }

    private void startFansActivity(){
        Intent i=new Intent(baseActivity, FansActivity.class);
        i.putExtra("UserId", userData.getUserId());
        baseActivity.startActivity(i);
    }

    private void setupViews(){
        if(userData.getUserDisplayName() != null && userData.getUserDisplayName().length()>0){
            textUserDisplayName.setText(userData.getUserDisplayName());
        }
        else {
            textUserDisplayName.setText(userData.getUserName());
        }

        if(userData.getUserTagline() != null && userData.getUserTagline().length() > 0){
            textTagline.setVisibility(View.VISIBLE);
            textTagline.setText(Utility.unescape_perl_string(userData.getUserTagline()));
        }
        else {
            textTagline.setVisibility(View.GONE);
        }
        textFollowers.setVisibility(View.VISIBLE);
        textFollowers.setText(userData.getUserFan() + " Followers");

        if (userData.getUserPic().equals("")) {
            imgUserPic.setImageResource(R.drawable.profile_pic_new);
        }
        else {
            imgUserPic.setImageResource(R.drawable.profile_pic_new);

            Picasso.with(baseActivity).load(userData.getUserPic()).resize(Utility.dpToPx(baseActivity, 126), Utility.dpToPx(baseActivity, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
                    .error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(baseActivity, 126), 0)).into(imgUserPic);
        }

        if (userData.getUserCoverImage() == null || userData.getUserCoverImage().equals("")) {
            imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
        } else {
            imgCoverPage.setBackgroundResource(R.drawable.cover_bg);

            Picasso.with(baseActivity).load(userData.getUserCoverImage()).fit().centerCrop().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg).into(imgCoverPage);
        }

    }
}
