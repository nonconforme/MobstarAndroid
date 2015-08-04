package com.mobstar.upload;

import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mobstar.R;
import com.mobstar.pojo.StarPojo;
import com.mobstar.upload.MessageActivity.postMessageCall;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;

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
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageComposeActivity extends Activity implements OnClickListener,TokenListener{

	private Context mContext;
	private com.mobstar.custom.ContactsCompletionView autoCompleteTextView;
	private Button btnBack,btnFinish;
	SharedPreferences preferences;
	public String sErrorMessage;
	private String UserID="";
	private ArrayList<StarPojo> arrStarPojos = new ArrayList<StarPojo>();
	private ArrayAdapter<StarPojo> adapter;
	private String recipent;
	private boolean fromIsMsg=false,isRefresh=false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=MessageComposeActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);
		UserID = preferences.getString("userid", "");	
		Utility.SendDataToGA("NewMessage Screen",MessageComposeActivity.this);
		setContentView(R.layout.activity_message_compose);

		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			fromIsMsg=bundle.getBoolean("fromIsMsg");

		}

		adapter = new FilteredArrayAdapter<StarPojo>(this, R.layout.row_item_message, arrStarPojos) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {

					LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
					convertView = l.inflate(R.layout.row_item_message, parent, false);
				}

				StarPojo p = getItem(position);
				((TextView)convertView.findViewById(R.id.textName)).setText(p.getStarName());
				ImageView userPic=(ImageView)convertView.findViewById(R.id.imgUserPic);
				if (p.getProfileImage().equals("")) {
					userPic.setImageResource(R.drawable.ic_pic_small);
				} else {
					userPic.setImageResource(R.drawable.ic_pic_small);

					Picasso.with(mContext).load(p.getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(userPic);

				}
				//                ((TextView)convertView.findViewById(R.id.email)).setText(p.getEmail());

				return convertView;
			}

			@Override
			protected boolean keepObject(StarPojo obj, String mask) {
				mask = mask.toLowerCase();
				return obj.getStarName().toLowerCase().startsWith(mask) || obj.getStarName().toLowerCase().startsWith(mask);
			}
		};

		initControlls();


		if (Utility.isNetworkAvailable(mContext)) {
			new UsersFanCall().start();
		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

	}

	private void initControlls() {
		autoCompleteTextView=(com.mobstar.custom.ContactsCompletionView)findViewById(R.id.autoCompleteTextView);
		btnBack=(Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnFinish=(Button)findViewById(R.id.btnFinish);
		btnFinish.setOnClickListener(this);
		autoCompleteTextView.setTokenListener(MessageComposeActivity.this);
		autoCompleteTextView.setThreshold(1);
		autoCompleteTextView.allowDuplicates(false);
		autoCompleteTextView.setSplitChar(',');
		autoCompleteTextView.allowCollapse(false);
		autoCompleteTextView.setAdapter(adapter);

	}


	@Override
	public void onClick(View v) {
		if(btnBack.equals(v)){
			onBackPressed();
		}
		else if (btnFinish.equals(v)) {
			boolean isValid=false;
			if (recipent.length() == 0) {
				isValid = false;
			} else {
				isValid=true;
			}

			if(isValid){
				Log.d("mobstar","recipeant=>"+recipent);
				if(fromIsMsg){
					Intent intent=new Intent(mContext,MessageActivity.class);
					intent.putExtra("fromIsMsg",fromIsMsg);
					intent.putExtra("recipent",recipent);
					startActivityForResult(intent, 102);
				}
				else{
					Intent intent=new Intent(mContext,MessageActivity.class);
					intent.putExtra("fromIsMsg",false);
					intent.putExtra("recipent",recipent);
					startActivity(intent);
					finish();
				}
				
			}

		}
	}

	class UsersFanCall extends Thread {

		@Override
		public void run() {

			String[] name = {"user"};
			String[] value = {UserID};
			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.GET_USER_FOLLOWER, name, value,preferences.getString("token", null));

			Log.v(Constant.TAG, "UsersFanCall response " + response);

			if (response != null) {

				try {

					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					JSONArray jsonArrayStars = jsonObject.getJSONArray("starredBy");

					arrStarPojos.clear();

					for (int j = 0; j < jsonArrayStars.length(); j++) {

						JSONObject jsonObjStar = jsonArrayStars.getJSONObject(j);

						StarPojo tempPojo = new StarPojo();
						tempPojo.setStarID(jsonObjStar.getString("starId"));
						tempPojo.setStarName(jsonObjStar.getString("starName"));
						tempPojo.setProfileImage(jsonObjStar.getString("profileImage"));
						tempPojo.setProfileCover(jsonObjStar.getString("profileCover"));
						tempPojo.setStarredDate(jsonObjStar.getString("starredDate"));

						arrStarPojos.add(tempPojo);
					}


					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerUserFan.sendEmptyMessage(0);
					} else {
						handlerUserFan.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerUserFan.sendEmptyMessage(0);
				}

			} else {
				handlerUserFan.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerUserFan = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (arrStarPojos.size() == 0) {
				//				textNoData.setVisibility(View.VISIBLE);
			} else {
			}

			if (msg.what == 1) {
				adapter.notifyDataSetChanged();
			} else {

			}
		}
	};

	private void updateTokenConfirmation() {
		StringBuilder sb = new StringBuilder();
		for (Object token: autoCompleteTextView.getObjects()) {
			StarPojo obj=(StarPojo) token;
			sb.append(obj.getStarID());
			sb.append(",");
		}
		String toStr=String.valueOf(sb);
		int index=toStr.lastIndexOf(',');
		recipent=toStr.substring(0,index);
		System.out.println("total: " + recipent);
	}

	@Override
	public void onTokenAdded(Object token) {
		StarPojo obj=(StarPojo) token;
		System.out.println("Added: " + obj.getStarName());
		updateTokenConfirmation();
	}

	@Override
	public void onTokenRemoved(Object token) {
		StarPojo obj=(StarPojo) token;
		System.out.println("Removed: " + obj.getStarName());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("log_tag","onactivity result messagecompose");
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {  
			Log.i("mobstar", String.valueOf(requestCode));
			switch (requestCode) {  
			case 102:
				boolean isRefresh=data.getBooleanExtra("isRefresh",false);
				if(isRefresh){
					Intent intent=new Intent();
					intent.putExtra("isRefresh", isRefresh);
					setResult(RESULT_OK,intent);
					finish();
				}

				break;
			}
		}
	}


}
