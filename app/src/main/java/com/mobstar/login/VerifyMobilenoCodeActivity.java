//package com.mobstar.login;
//
//import org.json.JSONObject;
//
//import com.mobstar.R;
//import com.mobstar.home.HomeActivity;
//import com.mobstar.utils.Constant;
//import com.mobstar.utils.JSONParser;
//import com.mobstar.utils.Utility;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnKeyListener;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class VerifyMobilenoCodeActivity extends Activity implements OnClickListener{
//
//	Context mContext;
//	EditText edtDigit1,edtDigit2,edtDigit3,edtDigit4;
//	Button btnVerifyNo,btnResendCode;
//	Typeface typefaceBtn;
//	TextView textVerifyMobileno;
//	SharedPreferences preferences;
//	String sUserId;
//	String sErrorMessage = "";
//	String mobileNo;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_verify_confirm_mobileno);
//		mContext=VerifyMobilenoCodeActivity.this;
//		preferences = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
//		sUserId = preferences.getString("userid", "");
//		getBundleExtras();
//		InitControls();
//	}
//
//	private void getBundleExtras() {
//		Bundle b=getIntent().getExtras();
//		if(b!=null){
//			mobileNo=b.getString("mobileNo");
//		}
//	}
//
//	private void InitControls() {
//		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");
//
//		textVerifyMobileno=(TextView) findViewById(R.id.textVerifyMobileno);
//		textVerifyMobileno.setTypeface(typefaceBtn);
//
//		textVerifyMobileno.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				onBackPressed();
//			}
//		});
//
//		edtDigit1 = (EditText) findViewById(R.id.edtDigit1);
//		edtDigit1.setTypeface(typefaceBtn);
//
//		edtDigit2 = (EditText) findViewById(R.id.edtDigit2);
//		edtDigit2.setTypeface(typefaceBtn);
//
//		edtDigit3 = (EditText) findViewById(R.id.edtDigit3);
//		edtDigit3.setTypeface(typefaceBtn);
//
//		edtDigit4 = (EditText) findViewById(R.id.edtDigit4);
//		edtDigit4.setTypeface(typefaceBtn);
//
//		edtDigit1.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if(keyCode != KeyEvent.KEYCODE_DEL){
//					if(edtDigit1.getText().toString().length()==1){
//						edtDigit2.requestFocus();
//					}
//				}
//				return false;
//			}
//		});
//
//		edtDigit2.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if(keyCode != KeyEvent.KEYCODE_DEL){
//					if(edtDigit2.getText().toString().length()==1){
//						edtDigit3.requestFocus();
//					}
//				}
//				return false;
//			}
//		});
//
//		edtDigit3.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if(keyCode != KeyEvent.KEYCODE_DEL){
//					if(edtDigit3.getText().toString().length()==1){
//						edtDigit4.requestFocus();
//					}
//				}
//				return false;
//			}
//		});
//
//		btnVerifyNo=(Button) findViewById(R.id.btnVerifyNo);
//		btnVerifyNo.setTypeface(typefaceBtn);
//		btnVerifyNo.setOnClickListener(this);
//
//		btnResendCode=(Button) findViewById(R.id.btnResendCode);
//		btnResendCode.setTypeface(typefaceBtn);
//		btnResendCode.setOnClickListener(this);
//
//	}
//
//	void openConfirmDialog() {
//		TextView textConfDialogMobileno;
//		ImageView btnConfDialogClose;
//
//		final Dialog dialog = new Dialog(mContext, R.style.DialogTheme);
//		dialog.setContentView(R.layout.dialog_verify_confirm_mobileno);
//		dialog.setCancelable(true);
//		textConfDialogMobileno=(TextView)dialog.findViewById(R.id.textConfDialogMobileno);
//		btnConfDialogClose=(ImageView)dialog.findViewById(R.id.btnConfDialogClose);
//
//		textConfDialogMobileno.setText(mobileNo);
//
//		btnConfDialogClose.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//				SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
//				pref.edit().putBoolean("isVerifyMobileCode",true).commit();
//				Intent intent = new Intent(mContext, HomeActivity.class);
//				intent.putExtra("isHomeInfo",true);
//				startActivity(intent);
//				finish();
//			}
//		});
//
//		dialog.show();
//	}
//
//	@Override
//	public void onClick(View v) {
//		if(btnVerifyNo.equals(v)) {
//			boolean isValid = true;
//			if (edtDigit1.getText().toString().trim().length() == 0) {
//				isValid = false;
//			}
//
//			if (edtDigit2.getText().toString().trim().length() == 0) {
//				isValid = false;
//			}
//
//			if (edtDigit3.getText().toString().trim().length() == 0) {
//				isValid = false;
//			}
//
//			if (edtDigit4.getText().toString().trim().length() == 0) {
//				isValid = false;
//			}
//
//			if (isValid) {
//				Utility.ShowProgressDialog(mContext, getString(R.string.loading));
//				sErrorMessage = "";
//
//				if (Utility.isNetworkAvailable(mContext)) {
//					String code=edtDigit1.getText().toString()+edtDigit2.getText().toString()+edtDigit3.getText().toString()+edtDigit4.getText().toString();
//					new VerifyMobileCodeCall(code).start();
//
//				} else {
//					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//					Utility.HideDialog(mContext);
//				}
//			}
//
//		}
//		else if(btnResendCode.equals(v)) {
//			Intent intent = new Intent(mContext, VerifyMobileNoActivity.class);
//			startActivity(intent);
//			finish();
//		}
//	}
//
//	class VerifyMobileCodeCall extends Thread {
//
//		String code;
//
//		public VerifyMobileCodeCall(String code) {
//			this.code = code;
//		}
//
//		@Override
//		public void run() {
//
//			String[] name = {"userId","verificationCode"};
//			String[] value = {sUserId,code};
//
////			Log.d("UserId",sUserId+"code"+code);
//			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.VERIFY_MOBILE_CODE, name, value,preferences.getString("token", null));
//
////			Log.v(Constant.TAG, "Verify mobile code response " + response);
//
//			if (response != null) {
//
//				try {
//					JSONObject jsonObject = new JSONObject(response);
//
//					if (jsonObject.has("error")) {
//						sErrorMessage = jsonObject.getString("error");
//					}
//
//					if (sErrorMessage != null && !sErrorMessage.equals("")) {
//						handlerVerifyCode.sendEmptyMessage(0);
//					} else {
//						handlerVerifyCode.sendEmptyMessage(1);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					handlerVerifyCode.sendEmptyMessage(0);
//				}
//
//			} else {
//
//				handlerVerifyCode.sendEmptyMessage(0);
//			}
//
//		}
//	}
//
//	void OkayAlertDialog(final String msg) {
//
//		if (!isFinishing()) {
//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
//
//					// set title
//					alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
//
//					// set dialog message
//					alertDialogBuilder.setMessage(msg).setCancelable(false).setNeutralButton("OK", null);
//
//					// create alert dialog
//					AlertDialog alertDialog = alertDialogBuilder.create();
//
//					// show it
//					alertDialog.show();
//				}
//			});
//		}
//
//	}
//
//	Handler handlerVerifyCode = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			Utility.HideDialog(mContext);
//
//			if (msg.what == 1) {
//				openConfirmDialog();
//
//			} else {
//				OkayAlertDialog(sErrorMessage);
//			}
//		}
//	};
//
//}
