package com.mobstar.info.report;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.custom.TagView;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;
import com.tokenautocomplete.TokenCompleteTextView.TokenClickStyle;

public class InformationDetailActivity extends Activity {

	private SharedPreferences preferences;
	private TextView textInformation;
	private EntryP entryPojo;
	private TextView textUserName, textTime, textDescription;
	private ImageView imgUserPic;
	private TextView textCategoryName, textLanguage;
	private LinearLayout llGender;
	private TextView textGender,textHeight,textAge;
	private TagView textTags;
	private ArrayList<String> arrayTags = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information_detail);
		getBundleExtra();
		findViews();
		initControls();
		Utility.SendDataToGA("InformationDetail Screen", InformationDetailActivity.this);
	}

	private void getBundleExtra(){
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			entryPojo = (EntryP) extras.getSerializable("entry");
		}
	}

	private void findViews(){
		textUserName      = (TextView) findViewById(R.id.textUserName);
		textTime          = (TextView) findViewById(R.id.textTime);
		textDescription   = (TextView) findViewById(R.id.textDescription);
		llGender          = (LinearLayout)findViewById(R.id.llGender);
		textGender        = (TextView)findViewById(R.id.textGender);
		textHeight        = (TextView)findViewById(R.id.textHeight);
		textAge           = (TextView)findViewById(R.id.textAge);
		textCategoryName  = (TextView) findViewById(R.id.textCategoryName);
		textLanguage      = (TextView) findViewById(R.id.textLanguage);
		textTags          = (TagView) findViewById(R.id.textTags);
	}

	private void initControls() {

		textUserName.setText(entryPojo.getUser().getDisplayName());
		textDescription.setText(Utility.unescape_perl_string(entryPojo.getEntry().getName()));
		textTime.setText(entryPojo.getEntry().getCreatedAgo());


		if(entryPojo.getEntry().getSubcategory()!="" && entryPojo.getEntry().getSubcategory().length()>0){
			textGender.setText(entryPojo.getEntry().getSubcategory().toUpperCase());
			llGender.setVisibility(View.VISIBLE);
		}
		else{
			llGender.setVisibility(View.GONE);
		}
		
		if(entryPojo.getEntry().getHeight()!="" && entryPojo.getEntry().getHeight().length()>0){
			textHeight.setText(entryPojo.getEntry().getHeight());
		}
		if(entryPojo.getEntry().getAge()!="" && entryPojo.getEntry().getAge().length()>0){
			textAge.setText(entryPojo.getEntry().getAge());
		}
		textTags.setTokenClickStyle(TokenClickStyle.None);

		textTags.setEnabled(false);

		textCategoryName.setText(Long.toString(entryPojo.getEntry().getCategiryId()));
		textLanguage.setText(entryPojo.getEntry().getLanguage().toUpperCase());

		textInformation = (TextView) findViewById(R.id.textInformation);
		textInformation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		imgUserPic = (ImageView) findViewById(R.id.imgUserPic);

		if (entryPojo.getUser().getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso.with(this).load(entryPojo.getUser().getProfileImage()).resize(Utility.dpToPx(this, 45), Utility.dpToPx(this, 45)).centerCrop()
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

		arrayTags = entryPojo.getEntry().getTags();
		for (int i = 0; i < arrayTags.size(); i++) {
			textTags.addObject(arrayTags.get(i).toUpperCase());
		}
	}

}
