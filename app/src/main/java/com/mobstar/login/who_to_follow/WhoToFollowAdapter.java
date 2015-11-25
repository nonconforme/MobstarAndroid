package com.mobstar.login.who_to_follow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.api.new_api_model.WhoToFollowUser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lipcha on 25.11.15.
 */
public class WhoToFollowAdapter extends BaseAdapter {

    private ArrayList<WhoToFollowUser> whoToFollowList;
    private LayoutInflater layoutInflater;
    private Context context;

    public WhoToFollowAdapter(final Context context, final ArrayList<WhoToFollowUser> whoToFollowList)  {
        this.context = context;
        this.whoToFollowList = whoToFollowList;
        layoutInflater = LayoutInflater.from(context);
    }

    public ArrayList<WhoToFollowUser> getWhoToFollowList() {
        return whoToFollowList;
    }

    @Override
    public int getCount() {
        return whoToFollowList.size();
    }

    @Override
    public Object getItem(int position) {
        return whoToFollowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_who_to_follow, null);
            viewHolder = getNewViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

       setupUI(viewHolder, position);

        return convertView;
    }

    private void setupUI(final ViewHolder viewHolder, final int position){
        final int pos = position;
        viewHolder.textName.setText(whoToFollowList.get(position).getDisplayName());
        if (whoToFollowList.get(position).getProfileImage().equals("")) {
            viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
        } else {
            Picasso.with(context)
                    .load(whoToFollowList.get(position).getProfileImage())
                    .resize(Utility.dpToPx(context, 45), Utility.dpToPx(context, 45))
                    .centerCrop()
                    .placeholder(R.drawable.ic_pic_small)
                    .error(R.drawable.ic_pic_small)
                    .into(viewHolder.imgUserPic);
        }

        viewHolder.cbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    whoToFollowList.get(pos).setIsChecked(true);
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetChanged();
                    whoToFollowList.get(pos).setIsChecked(false);
                }

            }
        });
    }

    private ViewHolder getNewViewHolder(final View convertView){
        final ViewHolder holder = new ViewHolder();
        holder.textName = (TextView) convertView.findViewById(R.id.textName);
        holder.cbFollow = (CheckBox)convertView.findViewById(R.id.cbFollow);
        holder.imgUserPic = (ImageView)convertView.findViewById(R.id.imgUserPic);
        return holder;
    }

    private class ViewHolder{
        private TextView textName;
        private CheckBox cbFollow;
        private ImageView imgUserPic;
    }
}
