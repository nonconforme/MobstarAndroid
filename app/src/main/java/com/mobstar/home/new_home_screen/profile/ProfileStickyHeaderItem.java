package com.mobstar.home.new_home_screen.profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.BaseActivity;
import com.mobstar.R;

/**
 * Created by lipcha on 22.09.15.
 */
public class ProfileStickyHeaderItem extends RecyclerView.ViewHolder{

    private BaseActivity context;
    private TextView textUpdates;
    private TextView textProfile;
    private LinearLayout llSticky;

    public ProfileStickyHeaderItem(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(final View convertView){
        textUpdates = (TextView)convertView.findViewById(R.id.textUpdates);
        textProfile = (TextView)convertView.findViewById(R.id.textProfile);
        llSticky = (LinearLayout) convertView.findViewById(R.id.llSticky);
    }

    public void init(final BaseActivity _baseActivity, final int page){
        context = _baseActivity;
        setupViews(page);
        llSticky.setVisibility(View.VISIBLE);
    }


    public void setupViews(int modeType){
        if(modeType != ProfileEntryAdapter.PROFILE_PAGE){
            textUpdates.setBackgroundColor(context.getResources().getColor(R.color.splash_bg));
            textProfile.setBackgroundColor(context.getResources().getColor(R.color.gray_color));
        }
        else {
            textUpdates.setBackgroundColor(context.getResources().getColor(R.color.gray_color));
            textProfile.setBackgroundColor(context.getResources().getColor(R.color.splash_bg));
        }
    }

}
