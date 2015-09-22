package com.mobstar.home.new_home_screen.profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mobstar.BaseActivity;
import com.mobstar.R;

/**
 * Created by lipcha on 22.09.15.
 */
public class ProfileStickyHeader extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static final int PROFILE_PAGE = 0;
    public static final int UPDATES_PAGE = 1;

    private BaseActivity context;
    private TextView textUpdates;
    private TextView textProfile;
    private boolean isDefault;
    private OnChangeProfilePageListener onChangeProfilePageListener;

    public ProfileStickyHeader(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(final View convertView){
        textUpdates=(TextView)convertView.findViewById(R.id.textUpdates);
        textProfile=(TextView)convertView.findViewById(R.id.textProfile);
    }

    public void init(final BaseActivity _baseActivity, final boolean _isDefault, OnChangeProfilePageListener _profilePageListener){
        context = _baseActivity;
        isDefault = _isDefault;
        onChangeProfilePageListener = _profilePageListener;
        setListeners();
        setupViews();
    }



    private void setListeners(){
        textProfile.setOnClickListener(this);
        textUpdates.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textUpdates:
                onClickUpdates();
                break;
            case R.id.textProfile:
                onClickProfile();
                break;
        }
        setupViews();
    }

    private void onClickUpdates(){
        if (onChangeProfilePageListener == null)
            return;
        isDefault = onChangeProfilePageListener.onChangePage(UPDATES_PAGE);
    }

    private void onClickProfile(){
        if (onChangeProfilePageListener == null)
            return;
        isDefault = onChangeProfilePageListener.onChangePage(PROFILE_PAGE);
    }

    private void setupViews(){
        if(isDefault){
            textUpdates.setBackgroundColor(context.getResources().getColor(R.color.splash_bg));
            textProfile.setBackgroundColor(context.getResources().getColor(R.color.gray_color));
        }
        else {
            textUpdates.setBackgroundColor(context.getResources().getColor(R.color.gray_color));
            textProfile.setBackgroundColor(context.getResources().getColor(R.color.splash_bg));
        }
    }

    public interface OnChangeProfilePageListener{
        boolean onChangePage(int page);
    }


}
