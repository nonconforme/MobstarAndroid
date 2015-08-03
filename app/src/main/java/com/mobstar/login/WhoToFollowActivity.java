package com.mobstar.login;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.ProfileActivity;
import com.mobstar.R;
import com.mobstar.home.HomeActivity;
import com.mobstar.pojo.WhoToFollowPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class WhoToFollowActivity extends Activity implements OnClickListener{

	GridView grid;
	Button btnSkip,btnFollow;
	Context mContext;
	SharedPreferences preferences;
	ArrayList<WhoToFollowPojo> arrFollowPojos = new ArrayList<WhoToFollowPojo>();
	String sErrorMessage = "";
	private GridAdapter gridListAdapter;
	LinearLayout llBottom;
	String star="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_who_to_follow);

		mContext = WhoToFollowActivity.this;
		gridListAdapter=new GridAdapter();
		preferences = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
		InitControls();
		Utility.ShowProgressDialog(mContext, "Loading");
		sErrorMessage = "";
		if (Utility.isNetworkAvailable(mContext)) {
			new WhoToFollowList().start();
		} else {
			Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
		}
		
		Utility.SendDataToGA("WhoToFollow Screen", WhoToFollowActivity.this);

	}

	private void InitControls() {

		grid=(GridView)findViewById(R.id.gried_whoTOFollow);
		llBottom=(LinearLayout)findViewById(R.id.llBottom);
		btnFollow=(Button)findViewById(R.id.btnFollow);
		btnFollow.setOnClickListener(this);

		btnSkip=(Button)findViewById(R.id.btnSkip);
		btnSkip.setOnClickListener(this);
	

	}

	class WhoToFollowList extends Thread {

		@Override
		public void run() {

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.WHO_TO_FOLLOW_LIST, null,null,preferences.getString("token", null));

//			Log.v(Constant.TAG, "WhoToFollow List response " + response);

			if (response != null) {

				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					JSONArray jsonArrayEntries = null;
					if (jsonObject.has("users")){
						jsonArrayEntries = jsonObject.getJSONArray("users");
					}
					if(jsonArrayEntries.length()>0){
						
						arrFollowPojos.clear();

						for (int i = 0; i < jsonArrayEntries.length(); i++) {

							JSONObject jsonObj=jsonArrayEntries.getJSONObject(i);
							JSONObject jsonObjEntry=null;
							WhoToFollowPojo whoToFollowPojo = new WhoToFollowPojo();
							if (jsonObj.has("user")) {
								jsonObjEntry=jsonObj.getJSONObject("user");
								whoToFollowPojo.setID(jsonObjEntry.getString("id"));
								whoToFollowPojo.setDisplayName(jsonObjEntry.getString("displayName"));
								whoToFollowPojo.setProfileImage(jsonObjEntry.getString("profileImage"));
								whoToFollowPojo.setSelected(false);
								arrFollowPojos.add(whoToFollowPojo);
							}

						}
					}



					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerWhoToFollow.sendEmptyMessage(0);
					} else {
						handlerWhoToFollow.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerWhoToFollow.sendEmptyMessage(0);
				}

			} else {

				handlerWhoToFollow.sendEmptyMessage(0);
			}

		}
	}

	void OkayAlertDialog(final String msg) {

		if (!isFinishing()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

					// set title
					alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

					// set dialog message
					alertDialogBuilder.setMessage(msg).setCancelable(false).setNeutralButton("OK", null);

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			});
		}

	}

	Handler handlerWhoToFollow= new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				grid.setAdapter(gridListAdapter);
//				gridListAdapter.notifyDataSetChanged();
//				grid.invalidate();
			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	class WhoToFollowAdd extends Thread {

		String stars;

		WhoToFollowAdd(String mystar){
			stars=mystar;
		}

		@Override
		public void run() {

			String[] name = {"star"};
			String[] value = {stars};

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.WHO_TO_FOLLOW, name,value,preferences.getString("token", null));

//			Log.v(Constant.TAG, "WhoToFollow List response " + response);

			if (response != null) {

				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerAddFollow.sendEmptyMessage(0);
					} else {
						handlerAddFollow.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerWhoToFollow.sendEmptyMessage(0);
				}

			} else {

				handlerAddFollow.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerAddFollow= new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				Intent intent = new Intent(mContext,HomeActivity.class);
				intent.putExtra("isHomeInfo",true);
				startActivity(intent);
				finish();
			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	class GridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return arrFollowPojos.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			final int pos=position;
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_who_to_follow, null);
				viewHolder=new ViewHolder();
				viewHolder.textName = (TextView) convertView.findViewById(R.id.textName);
				viewHolder.cbFollow = (CheckBox)convertView.findViewById(R.id.cbFollow);
				viewHolder.imgUserPic=(ImageView)convertView.findViewById(R.id.imgUserPic);
				
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.textName.setText(arrFollowPojos.get(position).getDisplayName());
			

			if (arrFollowPojos.get(position).getProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				//						imgUserPic.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrFollowPojos.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);
			}

			viewHolder.cbFollow.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						arrFollowPojos.get(pos).setSelected(true);
						notifyDataSetChanged();
					}
					else {
						notifyDataSetChanged();
						arrFollowPojos.get(pos).setSelected(false);
					}

				}
			});

			
			return convertView;
		}
		
		class ViewHolder{
			TextView textName;
			CheckBox cbFollow;
			ImageView imgUserPic;
		}

	}

	@Override
	public void onClick(View view) {
		gridListAdapter.notifyDataSetChanged();
		if (btnFollow.equals(view)) {
			for (int i = 0; i < arrFollowPojos.size(); i++) {
				if(arrFollowPojos.get(i).getSelected()){
					if(star.equals(null) || star.equalsIgnoreCase("")){
						star=arrFollowPojos.get(i).getID();
					}
					else {
						star=star+","+arrFollowPojos.get(i).getID();
					}
				}
			}
			sErrorMessage = "";
			if (Utility.isNetworkAvailable(mContext)) {
				new WhoToFollowAdd(star).start();
			} else {
				Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
			}
		}
		else if(btnSkip.equals(view)){
			Intent intent = new Intent(mContext,HomeActivity.class);
			intent.putExtra("isHomeInfo",true);
			startActivity(intent);
			finish();
		}

	}

}
