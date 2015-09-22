package com.mobstar.home.new_home_screen.profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.custom.CustomTextview;
import com.mobstar.custom.CustomTextviewBold;

/**
 * Created by lipcha on 22.09.15.
 */
public class ProfileItem extends RecyclerView.ViewHolder {

    private TextView

    public ProfileItem(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(final View convertView){
        textUserName = (CustomTextviewBold) convertView.findViewById(R.id.textUserName);
        textUserDisplayName = (CustomTextviewBold)convertView.findViewById(R.id.textUserDisplayName);
        textTagline = (CustomTextview)convertView.findViewById(R.id.textTagline);
        textFollowers = (CustomTextview)convertView.findViewById(R.id.textFollowers);
        imgUserPic = (ImageView)convertView.findViewById(R.id.imgUserPic);
        imgCoverPage = (ImageView)convertView.findViewById(R.id.imgCoverPage);
        imgFollow = (TextView)convertView.findViewById(R.id.imgFollow);
        imgMsg = (ImageView)convertView.findViewById(R.id.imgMsg);
        btnEdit = (CustomTextviewBold)convertView.findViewById(R.id.btnEdit);
    }

    public void init(){

    }
}
