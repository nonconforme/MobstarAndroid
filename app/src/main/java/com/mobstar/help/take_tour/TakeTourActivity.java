package com.mobstar.help.take_tour;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mobstar.R;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.home.HomeActivity;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class TakeTourActivity extends FragmentActivity implements OnClickListener, OnPageChangeListener, View.OnTouchListener {

	private static final int TOUR_SIZE = 10;

	private ImagePagerAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private Button btnStartOver, btnFinishTour;
	private Typeface typefaceBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_tour);
		findViews();
		setListeners();
		initControls();

		Utility.SendDataToGA("TakeATour Screen", TakeTourActivity.this);
	}

	private void findViews(){
		btnStartOver    = (Button) findViewById(R.id.btnStartOver);
		mPager          = (ViewPager) findViewById(R.id.viewpager);
		btnFinishTour   = (Button) findViewById(R.id.btnFinishTour);
		mIndicator      = (CirclePageIndicator) findViewById(R.id.indicator);
	}

	private void setListeners(){
		btnStartOver.setOnClickListener(this);
		btnFinishTour.setOnClickListener(this);
		mPager.setOnTouchListener(this);
		mIndicator.setOnPageChangeListener(this);
	}

	private void initControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");
		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), TOUR_SIZE);
		mPager.setAdapter(mAdapter);
		mPager.setPageTransformer(true, new ZoomOutPageTransformer());

		mIndicator.setViewPager(mPager);

		btnStartOver.setTypeface(typefaceBtn);
		btnFinishTour.setTypeface(typefaceBtn);

		btnStartOver.setVisibility(View.GONE);
		btnFinishTour.setVisibility(View.GONE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnStartOver:
				mPager.setCurrentItem(0);
				break;
			case R.id.btnFinishTour:
				verifyUserContinent();
				break;
		}
	}

	private void verifyUserContinent(){
		if (UserPreference.existUserContinent(this))
			startHomeActivity();
		else startSelectCurrentRegionActivity();
	}

	private void startSelectCurrentRegionActivity(){
		final Intent intent = new Intent(this, SelectCurrentRegionActivity.class);
		startActivity(intent);
		finish();
	}

	private void startHomeActivity(){
		final Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if (position == 9) {
			btnStartOver.setVisibility(View.VISIBLE);
			btnFinishTour.setVisibility(View.VISIBLE);
		} else {
			btnStartOver.setVisibility(View.GONE);
			btnFinishTour.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mPager.getParent().requestDisallowInterceptTouchEvent(true);
		return false;
	}
}
