package com.mobstar.inbox.newMessagesScreen;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.pojo.MessageThreadPojo;
import com.mobstar.utils.Utility;

import java.util.ArrayList;

/**
 * Created by Alexandr on 28.10.2015.
 */
public class MessagesAdapter extends BaseAdapter {

    private ArrayList<MessageThreadPojo> arrMessages;
    private final Context mContext;
    private final String userId;
    private final boolean isGroup;

    public MessagesAdapter(Context mContext, ArrayList<MessageThreadPojo> arrMessages, String userId, boolean isGroup) {
        this.arrMessages = arrMessages;
        this.mContext = mContext;
        this.userId =userId;
        this.isGroup =isGroup;
    }

    public void setArrMessages(ArrayList<MessageThreadPojo> arrMessages) {
        this.arrMessages = arrMessages;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        LinearLayout llUserThread,llMyThread;
        TextView textMsg,textMyMsg;
        TextView textTime,textMyTime,textStartTime;
        public TextView textMyname;
        public TextView textUsername;
    }

    @Override
    public int getCount() {
        return arrMessages.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = initHolder(isGroup,mInflater, viewHolder, convertView);

        } else
            viewHolder = (ViewHolder) convertView.getTag();


        if(arrMessages.get(position).getMessageReceived()!=null){
            if(position>0){
                if(!arrMessages.get(position-1).getMessageReceived().equalsIgnoreCase(arrMessages.get(position).getMessageReceived())){
                    viewHolder.textStartTime.setText(arrMessages.get(position).getMessageReceived());
                    viewHolder.textStartTime.setVisibility(View.VISIBLE);
                }
                else{
                    viewHolder.textStartTime.setVisibility(View.GONE);
                }
            }
            else{
                viewHolder.textStartTime.setText(arrMessages.get(position).getMessageReceived());
                viewHolder.textStartTime.setVisibility(View.VISIBLE);
            }


        }

        if(userId.equalsIgnoreCase(arrMessages.get(position).getSenderId())){
            //owm message thread
            viewHolder.llUserThread.setVisibility(View.GONE);
            viewHolder.llMyThread.setVisibility(View.VISIBLE);

            viewHolder.textMyMsg.setText(Utility.unescape_perl_string(arrMessages.get(position).getMessage()));
            viewHolder.textMyTime.setText(arrMessages.get(position).getCreated());

            if (isGroup) {
                if (arrMessages.get(position).getSenderdisplayName() != null && arrMessages.get(position).getSenderdisplayName().length() > 0) {
                    viewHolder.textMyname.setText(arrMessages.get(position).getSenderdisplayName());
                } else {
                    viewHolder.textMyname.setText(arrMessages.get(position).getSenderUserName());
                }
            }
//				if (arrMessages.get(position).getSenderprofileImage().equals("")) {
//					viewHolder.imgMyPic.setImageResource(R.drawable.ic_pic_small);
//				} else {
//					viewHolder.imgMyPic.setImageResource(R.drawable.ic_pic_small);
//
//					Picasso.with(mContext).load(arrMessages.get(position).getSenderprofileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
//					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgMyPic);
//
//				}
        }
        else{
            //other userside thread
            viewHolder.llMyThread.setVisibility(View.GONE);
            viewHolder.llUserThread.setVisibility(View.VISIBLE);

            viewHolder.textMsg.setText(Utility.unescape_perl_string(arrMessages.get(position).getMessage()));
            viewHolder.textTime.setText(arrMessages.get(position).getCreated());

            if (isGroup) {
                if(arrMessages.get(position).getSenderdisplayName()!=null && arrMessages.get(position).getSenderdisplayName().length()>0){
                    viewHolder.textUsername.setText(arrMessages.get(position).getSenderdisplayName());
                }
                else{
                    viewHolder.textUsername.setText(arrMessages.get(position).getSenderUserName());
                }
            }
//				if (arrMessages.get(position).getSenderprofileImage().equals("")) {
//					viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
//				} else {
//					viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
//
//					Picasso.with(mContext).load(arrMessages.get(position).getSenderprofileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
//					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);
//
//				}
        }

        return convertView;
    }

    private View initHolder(boolean isGroup, LayoutInflater mInflater, ViewHolder viewHolder, View convertView) {
        if (isGroup){
            convertView = mInflater.inflate(R.layout.row_group_message, null);
            viewHolder.textStartTime= (TextView) convertView.findViewById(R.id.textStartTime);
            viewHolder.llUserThread = (LinearLayout) convertView.findViewById(R.id.llUserThread);
            viewHolder.llMyThread = (LinearLayout) convertView.findViewById(R.id.llMyThread);
            viewHolder.textMsg = (TextView) convertView.findViewById(R.id.textMsg);
            viewHolder.textMyMsg = (TextView) convertView.findViewById(R.id.textMyMsg);
            viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
            viewHolder.textMyTime = (TextView) convertView.findViewById(R.id.textMyTime);
            viewHolder.textMyname =(TextView) convertView.findViewById(R.id.textMyname);
            viewHolder.textUsername =(TextView) convertView.findViewById(R.id.textUsername);
            convertView.setTag(viewHolder);
        }
        else {
            convertView = mInflater.inflate(R.layout.row_item_thread_user, null);
            viewHolder.textStartTime= (TextView) convertView.findViewById(R.id.textStartTime);
            viewHolder.llUserThread = (LinearLayout) convertView.findViewById(R.id.llUserThread);
            viewHolder.llMyThread = (LinearLayout) convertView.findViewById(R.id.llMyThread);
            viewHolder.textMsg = (TextView) convertView.findViewById(R.id.textMsg);
            viewHolder.textMyMsg = (TextView) convertView.findViewById(R.id.textMyMsg);
            viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
            viewHolder.textMyTime = (TextView) convertView.findViewById(R.id.textMyTime);
            convertView.setTag(viewHolder);
        }
        return convertView;
    }
}