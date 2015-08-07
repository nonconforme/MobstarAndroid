package com.mobstar.upload;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class MessageActivity extends Activity implements OnClickListener{
	private Context mContext;
	private Button btnBack,btnFinish;
	SharedPreferences preferences;
	public String sErrorMessage;
	private String UserID="";
	private EditText editMsg;
	private String recipent;
	private TextView textMsgHint;
	private boolean fromIsMsg=false,isRefresh=false,isDisableCompose=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=MessageActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);
		UserID = preferences.getString("userid", "");	
		Utility.SendDataToGA("NewMessage Screen",MessageActivity.this);
		setContentView(R.layout.activity_new_message);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			if(bundle.containsKey("recipent")){
				recipent=bundle.getString("recipent");
			}
			if(bundle.containsKey("fromIsMsg")){
				fromIsMsg=bundle.getBoolean("fromIsMsg");
			}
			if(bundle.containsKey("isDisableCompose")){
				isDisableCompose=bundle.getBoolean("isDisableCompose");
			}
			
		}
       
		initControlls();

	}

	private void initControlls() {
		btnBack=(Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnFinish=(Button)findViewById(R.id.btnFinish);
		btnFinish.setOnClickListener(this);
		editMsg=(EditText)findViewById(R.id.editMsg);
		textMsgHint=(TextView)findViewById(R.id.textMsgHint);
		textMsgHint.setVisibility(View.INVISIBLE);
		
	}


	@Override
	public void onClick(View v) {
		if(btnBack.equals(v)){
			if(fromIsMsg){
				Intent intent=new Intent();
				intent.putExtra("isRefresh", isRefresh);
				setResult(RESULT_OK,intent);
				finish();
			}
			else if(isDisableCompose){
				finish();
			}
			else {
				Intent intent=new Intent(mContext,MessageComposeActivity.class);
				intent.putExtra("fromIsMsg", false);
				startActivity(intent);
				finish();
			}
		}
		else if (btnFinish.equals(v)) {
			boolean isValid=false;
			if (editMsg.getText().toString().trim().length() == 0) {
				editMsg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				editMsg.setText("Enter Message");
				textMsgHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else {
				isValid=true;
			}
			
			if(isValid){
				Log.d("mobstar","recipeant=>"+recipent);
				Utility.ShowProgressDialog(mContext,getString(R.string.loading));
				if (Utility.isNetworkAvailable(mContext)) {
					new postMessageCall().start();
				} else {

					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			}
			
			
		}
	}

	
	class postMessageCall extends Thread {


		@Override
		public void run() {
			Log.d("mobstar","recipients id"+recipent);
			String strMsg=editMsg.getText().toString().trim();
			String ContentMsg=strMsg.replace("\"","");
			String[] name = {"recipients","message"};
			String[] value = {recipent,StringEscapeUtils.escapeJava(ContentMsg)};
			String response = JSONParser.LikepostRequest(Constant.SERVER_URL + Constant.NEW_MESSAGE, name, value,preferences.getString("token", null));

			Log.v(Constant.TAG, "PostMessage response " + response);

			if (response != null) {

				try {
					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage="Sorry, Please send message again!!";
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerMessage.sendEmptyMessage(0);
					} else {
						handlerMessage.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerMessage.sendEmptyMessage(0);
				}

			} else {

				handlerMessage.sendEmptyMessage(0);
			}
		}
	}

	Handler handlerMessage = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			isRefresh=true;
			onBackPressed();
		}
	};
	
	public void onBackPressed() {
		if(fromIsMsg){
			Intent intent=new Intent();
			intent.putExtra("isRefresh", isRefresh);
			setResult(RESULT_OK,intent);
			
		}
		finish();
		
	};

}
