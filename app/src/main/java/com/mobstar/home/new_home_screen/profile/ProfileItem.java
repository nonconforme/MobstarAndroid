package com.mobstar.home.new_home_screen.profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

/**
 * Created by lipcha on 23.09.15.
 */
public class ProfileItem extends RecyclerView.ViewHolder {

    private ImageView imgUser;
    private TextView textBio;

    public ProfileItem(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(final View convertView){
        imgUser = (ImageView) convertView.findViewById(R.id.imgUser);
        textBio = (TextView) convertView.findViewById(R.id.textBio);
    }

    public void init(final BaseActivity _baseActivity, final String userPic, final String userBio){
        if (userPic.equals("")) {
            imgUser.setImageResource(R.drawable.profile_pic_new);
        } else {
            imgUser.setImageResource(R.drawable.profile_pic_new);

            Picasso.with(_baseActivity).load(userPic).into(imgUser);
        }

        if(userBio!=null && userBio.length()>0){
            textBio.setText(Utility.unescape_perl_string(userBio));
            textBio.setVisibility(View.VISIBLE);
        }
        else {
            textBio.setVisibility(View.GONE);
        }
    }
}
