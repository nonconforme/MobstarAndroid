package com.mobstar.inbox;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.ProfileActivity;
import com.mobstar.R;
import com.mobstar.pojo.ParticipantsPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class GroupMember extends Activity{

	Context mContext;
	private SharedPreferences preferences;
	private ListView listUser;
	private TextView textNoData,textTalentPool;
	private String sErrorMessage="",threadId;
	private ArrayList<ParticipantsPojo> arrParticipants;
	private MemberAdapter memberAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talent_pool);
		mContext=GroupMember.this;
		preferences = mContext.getSharedPreferences("mobstar_pref", Context.MODE_PRIVATE);
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			threadId=bundle.getString("threadId");
		}
		initControlls();
		
		Utility.ShowProgressDialog(mContext, "Loading");

		if (Utility.isNetworkAvailable(mContext)) {

			new GroupMemberCall(threadId).start();

		} else {

			Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}
		
		listUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent intent=new Intent(mContext,ProfileActivity.class);
				intent.putExtra("UserID", arrParticipants.get(position).getUserId());
				intent.putExtra("UserDisplayName",arrParticipants.get(position).getDisplayName());
				intent.putExtra("UserPic",arrParticipants.get(position).getProfileImage());
				intent.putExtra("UserCoverImage",arrParticipants.get(position).getProfileCover());
				startActivity(intent);
				
			}
		});

	}

	void initControlls(){
		textTalentPool=(TextView)findViewById(R.id.textTalentPool);
		textTalentPool.setText(getString(R.string.group_member));
		listUser=(ListView) findViewById(R.id.listUser);
		textNoData=(TextView) findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);
		arrParticipants=new ArrayList<ParticipantsPojo>();
		memberAdapter=new MemberAdapter();
		listUser.setAdapter(memberAdapter);
	}

	class GroupMemberCall extends Thread {

		private String threadId;
		
		public GroupMemberCall(String threadId){
			this.threadId=threadId;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = {"thread"};
			//			String[] value = {"307",phoneNumber,country };
			String[] value = {threadId};
			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.MESSAGE_SHOW_PARTICIPANTS, name, value,preferences.getString("token", null));


			Log.v(Constant.TAG, "ReplayMessage response " + response);

			if (response != null) {
				try {
					sErrorMessage = "";
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					arrParticipants.clear();

					JSONObject jsonObjThread = jsonObject.getJSONObject("thread");

					JSONArray jsonArrayMessages = jsonObjThread.getJSONArray("participants");

					for (int i = 0; i < jsonArrayMessages.length(); i++) {
						JSONObject jsonObjMessage = jsonArrayMessages.getJSONObject(i);

						ParticipantsPojo tempPojo=new ParticipantsPojo();
						tempPojo.setUserId(jsonObjMessage.getString("userId"));
						tempPojo.setProfileImage(jsonObjMessage.getString("profileImage"));
						tempPojo.setProfileCover(jsonObjMessage.getString("profileCover"));
						tempPojo.setDisplayName(jsonObjMessage.getString("displayName"));
						arrParticipants.add(tempPojo);
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerGroup.sendEmptyMessage(0);
					} else {
						handlerGroup.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerGroup.sendEmptyMessage(0);
				}

			} else {

				handlerGroup.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerGroup = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			if (msg.what == 1) {
				memberAdapter.notifyDataSetChanged();

			} else {

			}
		}
	};

	public class MemberAdapter extends BaseAdapter {

		private class ViewHolder {
			TextView textUserName;
			ImageView imgUserPic;
		}

		@Override
		public int getCount() {
			return arrParticipants.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;

			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.row_item_user, null);
				viewHolder = new ViewHolder();

				viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			viewHolder.textUserName.setText(arrParticipants.get(position).getDisplayName());	

			if (arrParticipants.get(position).getProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrParticipants.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);

			}

			return convertView;
		}
	}

}
