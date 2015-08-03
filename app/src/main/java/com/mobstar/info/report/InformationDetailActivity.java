package com.mobstar.info.report;

import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.mobstar.R;
import com.mobstar.custom.TagView;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;
import com.tokenautocomplete.TokenCompleteTextView.TokenClickStyle;

public class InformationDetailActivity extends Activity {

	Context mContext;
	SharedPreferences preferences;

	TextView textInformation;

	EntryPojo entryPojo;

	TextView textUserName, textTime, textDescription;
	ImageView imgUserPic;
	TextView textCategoryName, textLanguage;
	
	LinearLayout llGender;
	TextView textGender,textHeight,textAge;

	TagView textTags;

	ArrayList<String> arrayTags = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_information_detail);
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		mContext = InformationDetailActivity.this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			entryPojo = (EntryPojo) extras.getSerializable("entry");
		}

		InitControls();

		Utility.SendDataToGA("InformationDetail Screen", InformationDetailActivity.this);
	}

	void InitControls() {

		textUserName = (TextView) findViewById(R.id.textUserName);
		textTime = (TextView) findViewById(R.id.textTime);
		textDescription = (TextView) findViewById(R.id.textDescription);

		textUserName.setText(entryPojo.getName());
		textDescription.setText(Utility.unescape_perl_string(entryPojo.getDescription()));
		textTime.setText(entryPojo.getCreated());
		
		llGender=(LinearLayout)findViewById(R.id.llGender);
		
		textGender=(TextView)findViewById(R.id.textGender);
		textHeight=(TextView)findViewById(R.id.textHeight);
		textAge=(TextView)findViewById(R.id.textAge);

		if(entryPojo.getSubCategory()!="" && entryPojo.getSubCategory().length()>0){
			textGender.setText(entryPojo.getSubCategory().toUpperCase());
			llGender.setVisibility(View.VISIBLE);
		}
		else{
			llGender.setVisibility(View.GONE);
		}
		
		if(entryPojo.getHeight()!="" && entryPojo.getHeight().length()>0){
			textHeight.setText(entryPojo.getHeight());
		}
		if(entryPojo.getAge()!="" && entryPojo.getAge().length()>0){
			textAge.setText(entryPojo.getAge());
		}
		
		textCategoryName = (TextView) findViewById(R.id.textCategoryName);
		textLanguage = (TextView) findViewById(R.id.textLanguage);
		textTags = (TagView) findViewById(R.id.textTags);
		textTags.setTokenClickStyle(TokenClickStyle.None);

		textTags.setEnabled(false);

		textCategoryName.setText(entryPojo.getCategory().toUpperCase());
		textLanguage.setText(entryPojo.getLanguage().toUpperCase());

		textInformation = (TextView) findViewById(R.id.textInformation);
		textInformation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		imgUserPic = (ImageView) findViewById(R.id.imgUserPic);

		if (entryPojo.getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso.with(mContext).load(entryPojo.getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(imgUserPic);

			// Ion.with(mContext).load(entryPojo.getProfileImage()).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).resize(Utility.dpToPx(mContext,
			// 45), Utility.dpToPx(mContext,
			// 45)).centerCrop().asBitmap().setCallback(new
			// FutureCallback<Bitmap>() {
			//
			// @Override
			// public void onCompleted(Exception exception, Bitmap bitmap) {
			// // TODO Auto-generated method stub
			// if (exception == null) {
			// imgUserPic.setImageBitmap(bitmap);
			// } else {
			// // Log.v(Constant.TAG, "Exception " +
			// // exception.toString());
			// }
			// }
			// });
		}

		arrayTags = entryPojo.getArrayTags();
		for (int i = 0; i < arrayTags.size(); i++) {
			textTags.addObject(arrayTags.get(i).toUpperCase());
		}
	}

}
