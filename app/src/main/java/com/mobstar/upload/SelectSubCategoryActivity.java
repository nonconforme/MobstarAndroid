package com.mobstar.upload;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.pojo.CategoryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class SelectSubCategoryActivity extends Activity implements OnClickListener {

	Context mContext;
	//	ImageView btnMusic;
	String sErrorMessage="";
	ArrayList<CategoryPojo> arrCategoryPojos = new ArrayList<CategoryPojo>();
	SharedPreferences preferences;
	LinearLayout llMale,llFemale,llCurve;
	String categoryId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_subcategory);

		mContext = SelectSubCategoryActivity.this;
		Bundle b=getIntent().getExtras();
		if(b!=null) {
			if(b.containsKey("categoryId")) {
				categoryId=b.getString("categoryId");
			}
		}
		InitControls();
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		Utility.SendDataToGA("SelectSubCategory Screen", SelectSubCategoryActivity.this);
	}

	void InitControls() {

		llMale=(LinearLayout)findViewById(R.id.llMale);
		llFemale=(LinearLayout)findViewById(R.id.llFemale);
		llCurve=(LinearLayout)findViewById(R.id.llCurve);
		
		llMale.setOnClickListener(this);
		llFemale.setOnClickListener(this);
		llCurve.setOnClickListener(this);

		//		btnMusic.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.equals(llMale)) {
			Intent intent = new Intent(mContext, GuidelinesActivity.class);
			intent.putExtra("categoryId",categoryId);
			intent.putExtra("subCat","male");
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		else if(view.equals(llFemale)){
			Intent intent = new Intent(mContext, GuidelinesActivity.class);
			intent.putExtra("categoryId",categoryId);
			intent.putExtra("subCat","female");
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		else if(view.equals(llCurve)){
			Intent intent = new Intent(mContext, GuidelinesActivity.class);
			intent.putExtra("categoryId",categoryId);
			intent.putExtra("subCat","curve");
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}


}
