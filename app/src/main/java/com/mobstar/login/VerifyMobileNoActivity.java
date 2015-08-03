package com.mobstar.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class VerifyMobileNoActivity extends Activity implements OnClickListener{

	Context mContext;
	SharedPreferences preferences;
	Button btnSendCode;
	Spinner spinnerCountry;
	Typeface typefaceBtn;
	EditText edtMobileno;
	TextView textCountry,textMobilenoHint;
	Map<String, String> map = new HashMap<String,String>();
	Map<String, String> mapCountry = new HashMap<String,String>();
	ArrayList<String> arrCountry = new ArrayList<String>();
	String selectedCountry,selectedCountryCode,sUserId;
	String sErrorMessage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_mobileno);
		mContext=VerifyMobileNoActivity.this;
		preferences = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
		sUserId = preferences.getString("userid", "");
		InitControls();
	}

	private void InitControls() {
		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnSendCode = (Button) findViewById(R.id.btnSendCode);
		btnSendCode.setTypeface(typefaceBtn);
		btnSendCode.setOnClickListener(this);

		textMobilenoHint = (TextView) findViewById(R.id.textMobilenoHint);
		textMobilenoHint.setVisibility(View.INVISIBLE);

		edtMobileno = (EditText) findViewById(R.id.edtMobileno);
		edtMobileno.setTypeface(typefaceBtn);
		edtMobileno.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				textMobilenoHint.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textMobilenoHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				edtMobileno.setOnKeyListener(new OnKeyListener() {                 
					public boolean onKey(View v, int keyCode, KeyEvent event) {                 
						if(keyCode == KeyEvent.KEYCODE_DEL){     
							if(edtMobileno.getText().toString().equalsIgnoreCase(selectedCountryCode+"-")){
								return true; //Disable backspace key here
							}
						}                  
						return false;       
					}
				});  
				if(selectedCountryCode!=null && edtMobileno.getText().toString().length()>0){
					if(!edtMobileno.getText().toString().contains(selectedCountryCode)) {
						edtMobileno.setText(selectedCountryCode+"-"+s.toString());
						edtMobileno.setSelection(edtMobileno.getText().length());
					}
				}

			}
		});

		textCountry = (TextView) findViewById(R.id.textCountry);
		textCountry.setTypeface(typefaceBtn);

		spinnerCountry=(Spinner) findViewById(R.id.spinnerCountry);

		setCountriesData();

		textCountry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spinnerCountry.performClick();
			}
		});

		spinnerCountry.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selectedCountry=spinnerCountry.getSelectedItem().toString();
				textCountry.setText(selectedCountry);
				getCountryCode(selectedCountry);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void setCountriesData(){
		String[] locales = Locale.getISOCountries();

		for (String countryCode : locales) {
			Locale obj = new Locale("", countryCode);
			arrCountry.add(obj.getDisplayCountry());
			map.put(obj.getDisplayCountry(),obj.getCountry());

		}
		if(arrCountry!=null && arrCountry.size()>0){
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrCountry); 
			spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerCountry.setAdapter(spinnerArrayAdapter);
		}

		mapCountry.put("AC", "+247");
		mapCountry.put("AD", "+376");
		mapCountry.put("AE", "+971");
		mapCountry.put("AF", "+93");
		mapCountry.put("AG", "+1-268");
		mapCountry.put("AI", "+1-264");
		mapCountry.put("AL", "+355");
		mapCountry.put("AM", "+374");
		mapCountry.put("AN", "+599");
		mapCountry.put("AO", "+244");
		mapCountry.put("AR", "+54");
		mapCountry.put("AS", "+1-684");
		mapCountry.put("AT", "+43");
		mapCountry.put("AU", "+61");
		mapCountry.put("AW", "+297");
		mapCountry.put("AX", "+358-18");
		mapCountry.put("AZ", "+374-97");
		mapCountry.put("AZ", "+994");
		mapCountry.put("BA", "+387");
		mapCountry.put("BB", "+1-246");
		mapCountry.put("BD", "+880");
		mapCountry.put("BE", "+32");
		mapCountry.put("BF", "+226");
		mapCountry.put("BG", "+359");
		mapCountry.put("BH", "+973");
		mapCountry.put("BI", "+257");
		mapCountry.put("BJ", "+229");
		mapCountry.put("BM", "+1-441");
		mapCountry.put("BN", "+673");
		mapCountry.put("BO", "+591");
		mapCountry.put("BR", "+55");
		mapCountry.put("BS", "+1-242");
		mapCountry.put("BT", "+975");
		mapCountry.put("BW", "+267");
		mapCountry.put("BY", "+375");
		mapCountry.put("BZ", "+501");
		mapCountry.put("CA", "+1");
		mapCountry.put("CC", "+61");
		mapCountry.put("CD", "+243");
		mapCountry.put("CF", "+236");
		mapCountry.put("CG", "+242");
		mapCountry.put("CH", "+41");
		mapCountry.put("CI", "+225");
		mapCountry.put("CK", "+682");
		mapCountry.put("CL", "+56");
		mapCountry.put("CM", "+237");
		mapCountry.put("CN", "+86");
		mapCountry.put("CO", "+57");
		mapCountry.put("CR", "+506");
		mapCountry.put("CS", "+381");
		mapCountry.put("CU", "+53");
		mapCountry.put("CV", "+238");
		mapCountry.put("CX", "+61");
		mapCountry.put("CY", "+90-392");
		mapCountry.put("CY", "+357");
		mapCountry.put("CZ", "+420");
		mapCountry.put("DE", "+49");
		mapCountry.put("DJ", "+253");
		mapCountry.put("DK", "+45");
		mapCountry.put("DM", "+1-767");
		mapCountry.put("DO", "+1-809"); // and 1-829?
		mapCountry.put("DZ", "+213");
		mapCountry.put("EC", "+593");
		mapCountry.put("EE", "+372");
		mapCountry.put("EG", "+20");
		mapCountry.put("EH", "+212");
		mapCountry.put("ER", "+291");
		mapCountry.put("ES", "+34");
		mapCountry.put("ET", "+251");
		mapCountry.put("FI", "+358");
		mapCountry.put("FJ", "+679");
		mapCountry.put("FK", "+500");
		mapCountry.put("FM", "+691");
		mapCountry.put("FO", "+298");
		mapCountry.put("FR", "+33");
		mapCountry.put("GA", "+241");
		mapCountry.put("GB", "+44");
		mapCountry.put("GD", "+1-473");
		mapCountry.put("GE", "+995");
		mapCountry.put("GF", "+594");
		mapCountry.put("GG", "+44");
		mapCountry.put("GH", "+233");
		mapCountry.put("GI", "+350");
		mapCountry.put("GL", "+299");
		mapCountry.put("GM", "+220");
		mapCountry.put("GN", "+224");
		mapCountry.put("GP", "+590");
		mapCountry.put("GQ", "+240");
		mapCountry.put("GR", "+30");
		mapCountry.put("GT", "+502");
		mapCountry.put("GU", "+1-671");
		mapCountry.put("GW", "+245");
		mapCountry.put("GY", "+592");
		mapCountry.put("HK", "+852");
		mapCountry.put("HN", "+504");
		mapCountry.put("HR", "+385");
		mapCountry.put("HT", "+509");
		mapCountry.put("HU", "+36");
		mapCountry.put("ID", "+62");
		mapCountry.put("IE", "+353");
		mapCountry.put("IL", "+972");
		mapCountry.put("IM", "+44");
		mapCountry.put("IN", "+91");
		mapCountry.put("IO", "+246");
		mapCountry.put("IQ", "+964");
		mapCountry.put("IR", "+98");
		mapCountry.put("IS", "+354");
		mapCountry.put("IT", "+39");
		mapCountry.put("JE", "+44");
		mapCountry.put("JM", "+1-876");
		mapCountry.put("JO", "+962");
		mapCountry.put("JP", "+81");
		mapCountry.put("KE", "+254");
		mapCountry.put("KG", "+996");
		mapCountry.put("KH", "+855");
		mapCountry.put("KI", "+686");
		mapCountry.put("KM", "+269");
		mapCountry.put("KN", "+1-869");
		mapCountry.put("KP", "+850");
		mapCountry.put("KR", "+82");
		mapCountry.put("KW", "+965");
		mapCountry.put("KY", "+1-345");
		mapCountry.put("KZ", "+7");
		mapCountry.put("LA", "+856");
		mapCountry.put("LB", "+961");
		mapCountry.put("LC", "+1-758");
		mapCountry.put("LI", "+423");
		mapCountry.put("LK", "+94");
		mapCountry.put("LR", "+231");
		mapCountry.put("LS", "+266");
		mapCountry.put("LT", "+370");
		mapCountry.put("LU", "+352");
		mapCountry.put("LV", "+371");
		mapCountry.put("LY", "+218");
		mapCountry.put("MA", "+212");
		mapCountry.put("MC", "+377");
		mapCountry.put("MD", "+373");
		mapCountry.put("ME", "+382");
		mapCountry.put("MG", "+261");
		mapCountry.put("MH", "+692");
		mapCountry.put("MK", "+389");
		mapCountry.put("ML", "+223");
		mapCountry.put("MM", "+95");
		mapCountry.put("MN", "+976");
		mapCountry.put("MO", "+853");
		mapCountry.put("MP", "+1-670");
		mapCountry.put("MQ", "+596");
		mapCountry.put("MR", "+222");
		mapCountry.put("MS", "+1-664");
		mapCountry.put("MT", "+356");
		mapCountry.put("MU", "+230");
		mapCountry.put("MV", "+960");
		mapCountry.put("MW", "+265");
		mapCountry.put("MX", "+52");
		mapCountry.put("MY", "+60");
		mapCountry.put("MZ", "+258");
		mapCountry.put("NA", "+264");
		mapCountry.put("NC", "+687");
		mapCountry.put("NE", "+227");
		mapCountry.put("NF", "+672");
		mapCountry.put("NG", "+234");
		mapCountry.put("NI", "+505");
		mapCountry.put("NL", "+31");
		mapCountry.put("NO", "+47");
		mapCountry.put("NP", "+977");
		mapCountry.put("NR", "+674");
		mapCountry.put("NU", "+683");
		mapCountry.put("NZ", "+64");
		mapCountry.put("OM", "+968");
		mapCountry.put("PA", "+507");
		mapCountry.put("PE", "+51");
		mapCountry.put("PF", "+689");
		mapCountry.put("PG", "+675");
		mapCountry.put("PH", "+63");
		mapCountry.put("PK", "+92");
		mapCountry.put("PL", "+48");
		mapCountry.put("PM", "+508");
		mapCountry.put("PR", "+1-787"); // and 1-939 ?
		mapCountry.put("PS", "+970");
		mapCountry.put("PT", "+351");
		mapCountry.put("PW", "+680");
		mapCountry.put("PY", "+595");
		mapCountry.put("QA", "+974");
		mapCountry.put("RE", "+262");
		mapCountry.put("RO", "+40");
		mapCountry.put("RS", "+381");
		mapCountry.put("RU", "+7");
		mapCountry.put("RW", "+250");
		mapCountry.put("SA", "+966");
		mapCountry.put("SB", "+677");
		mapCountry.put("SC", "+248");
		mapCountry.put("SD", "+249");
		mapCountry.put("SE", "+46");
		mapCountry.put("SG", "+65");
		mapCountry.put("SH", "+290");
		mapCountry.put("SI", "+386");
		mapCountry.put("SJ", "+47");
		mapCountry.put("SK", "+421");
		mapCountry.put("SL", "+232");
		mapCountry.put("SM", "+378");
		mapCountry.put("SN", "+221");
		mapCountry.put("SO", "+252");
		mapCountry.put("SO", "+252");
		mapCountry.put("SR", "+597");
		mapCountry.put("ST", "+239");
		mapCountry.put("SV", "+503");
		mapCountry.put("SY", "+963");
		mapCountry.put("SZ", "+268");
		mapCountry.put("TA", "+290");
		mapCountry.put("TC", "+1-649");
		mapCountry.put("TD", "+235");
		mapCountry.put("TG", "+228");
		mapCountry.put("TH", "+66");
		mapCountry.put("TJ", "+992");
		mapCountry.put("TK", "+690");
		mapCountry.put("TL", "+670");
		mapCountry.put("TM", "+993");
		mapCountry.put("TN", "+216");
		mapCountry.put("TO", "+676");
		mapCountry.put("TR", "+90");
		mapCountry.put("TT", "+1-868");
		mapCountry.put("TV", "+688");
		mapCountry.put("TW", "+886");
		mapCountry.put("TZ", "+255");
		mapCountry.put("UA", "+380");
		mapCountry.put("UG", "+256");
		mapCountry.put("US", "+1");
		mapCountry.put("UY", "+598");
		mapCountry.put("UZ", "+998");
		mapCountry.put("VA", "+379");
		mapCountry.put("VC", "+1-784");
		mapCountry.put("VE", "+58");
		mapCountry.put("VG", "+1-284");
		mapCountry.put("VI", "+1-340");
		mapCountry.put("VN", "+84");
		mapCountry.put("VU", "+678");
		mapCountry.put("WF", "+681");
		mapCountry.put("WS", "+685");
		mapCountry.put("YE", "+967");
		mapCountry.put("YT", "+262");
		mapCountry.put("ZA", "+27");
		mapCountry.put("ZM", "+260");
		mapCountry.put("ZW", "+263");
	}

	private void getCountryCode(String country) {
		Set<String> keys = map.keySet();

		for (Iterator<String> i = keys.iterator(); i.hasNext();)
		{
			String key = (String) i.next();
			if(key.equalsIgnoreCase(country)){
				String cCode=(String) map.get(key);
				Set<String> keys1 = mapCountry.keySet();
				for (Iterator<String> j = keys1.iterator(); j.hasNext();)
				{
					String key1 = (String) j.next();
					if(key1.equalsIgnoreCase(cCode)){
						selectedCountryCode=(String) mapCountry.get(key1);
					}
				}
			}
		}

		edtMobileno.setText(selectedCountryCode+"-");
		edtMobileno.setSelection(edtMobileno.getText().length());
		//		edtMobileno.setHint(selectedCountryCode+"-"+getResources().getString(R.string.hint_enter_mobileno));

	}

	@Override
	public void onClick(View view) {
		if (btnSendCode.equals(view)) {
			openConfirmDialog();
		}
	}

	void openConfirmDialog() {

		TextView textDialogMobileno;
		Button btnDialogNo,btnDialogYes;
		ImageView btnDialogClose;

		final Dialog dialog = new Dialog(mContext, R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_verify_mobileno);
		dialog.setCancelable(true);
		textDialogMobileno=(TextView)dialog.findViewById(R.id.textDialogMobileno);
		btnDialogNo = (Button) dialog.findViewById(R.id.btnDialogNo);
		btnDialogYes = (Button) dialog.findViewById(R.id.btnDialogYes);
		btnDialogClose=(ImageView)dialog.findViewById(R.id.btnDialogClose);

		textDialogMobileno.setText(edtMobileno.getText().toString());

		btnDialogYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mContext != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							boolean isValid = true;
							if (edtMobileno.getText().toString().trim().length() == 0) {

								edtMobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
								textMobilenoHint.setText("Enter FullName");
								textMobilenoHint.setVisibility(View.VISIBLE);

								isValid = false;
							} else {
								edtMobileno.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
								textMobilenoHint.setVisibility(View.INVISIBLE);
							}

							if (isValid) {
								Utility.ShowProgressDialog(mContext, "Loading");
								sErrorMessage = "";
								if (Utility.isNetworkAvailable(mContext)) {
									String fullNo=edtMobileno.getText().toString();
									int pos=fullNo.indexOf("-");
									String phone=fullNo.substring(pos+1);
									new VerifyMobileCall(selectedCountryCode,phone).start();

								} else {
									Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
									Utility.HideDialog(mContext);
								}
							}
						}
					});
				}
				dialog.dismiss();
			}
		});

		btnDialogClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btnDialogNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mContext != null) {
					runOnUiThread(new Runnable() {
						public void run() {

						}
					});
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	class VerifyMobileCall extends Thread {

		String country, phoneNumber;

		public VerifyMobileCall(String country, String phoneNumber) {
			this.country = country;
			this.phoneNumber = phoneNumber;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = {"userId","vPhoneNo", "countryCode"};
			//			String[] value = {"307",phoneNumber,country };
			String[] value = {sUserId,phoneNumber,country };
			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.VERIFY_MOBILE, name, value,preferences.getString("token", null));

//			Log.v(Constant.TAG, "Verify mobile response " + response);

			if (response != null) {

				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerVerifyMobileNo.sendEmptyMessage(0);
					} else {
						handlerVerifyMobileNo.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
					handlerVerifyMobileNo.sendEmptyMessage(0);
				}

			} else {

				handlerVerifyMobileNo.sendEmptyMessage(0);
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

	Handler handlerVerifyMobileNo = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				String mobileNo=edtMobileno.getText().toString();
				Intent intent = new Intent(mContext, VerifyMobilenoCodeActivity.class);
				intent.putExtra("mobileNo",mobileNo);
				startActivity(intent);
				finish();

			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};

}
