package com.mobstar.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.mobstar.R;
import com.mobstar.home.HomeActivity;
import com.mobstar.utils.Utility;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class TakeTourActivity extends FragmentActivity implements OnClickListener {

	Context mContext;

	private ImagePagerAdapter mAdapter;
	private ViewPager mPager;
	PageIndicator mIndicator;
	int TourSize = 10;
	Button btnStartOver, btnFinishTour;

	Typeface typefaceBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_tour);

		mContext = TakeTourActivity.this;

		InitControls();

		Utility.SendDataToGA("TakeATour Screen", TakeTourActivity.this);
	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), TourSize);
		mPager = (ViewPager) findViewById(R.id.viewpager);
		mPager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mPager.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		mPager.setAdapter(mAdapter);
		mPager.setPageTransformer(true, new ZoomOutPageTransformer());

		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				//Log.v(Constant.TAG, "mPager position " + position);
				// TODO Auto-generated method stub
				if (position == 9) {
					btnStartOver.setVisibility(View.VISIBLE);
					btnFinishTour.setVisibility(View.VISIBLE);
				} else {
					btnStartOver.setVisibility(View.GONE);
					btnFinishTour.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		btnStartOver = (Button) findViewById(R.id.btnStartOver);
		btnStartOver.setTypeface(typefaceBtn);
		btnFinishTour = (Button) findViewById(R.id.btnFinishTour);
		btnFinishTour.setTypeface(typefaceBtn);

		btnStartOver.setVisibility(View.GONE);
		btnFinishTour.setVisibility(View.GONE);

		btnStartOver.setOnClickListener(this);
		btnFinishTour.setOnClickListener(this);
	}

	public static class ImagePagerAdapter extends FragmentStatePagerAdapter {
		private final int mSize;

		public ImagePagerAdapter(FragmentManager fm, int size) {
			super(fm);
			mSize = size;
		}

		@Override
		public int getCount() {
			return mSize;
		}

		@Override
		public Fragment getItem(int position) {
			return ImageDetailFragment.newInstance(position);
		}
	}

	public static class ImageDetailFragment extends Fragment {
		private final static String IMAGE_POSITION = "position";
		int position;
		private ImageView mImageView;

		static ImageDetailFragment newInstance(int position) {

			final ImageDetailFragment f = new ImageDetailFragment();
			final Bundle args = new Bundle();
			args.putInt(IMAGE_POSITION, position);
			f.setArguments(args);
			return f;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

		}

		public ImageDetailFragment() {

		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			position = getArguments() != null ? getArguments().getInt(IMAGE_POSITION) : 0;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// image_detail_fragment.xml contains just an ImageView
			final View v = inflater.inflate(R.layout.view_flow_image_item, container, false);
			mImageView = (ImageView) v.findViewById(R.id.imgView1);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			if (position == 0) {
				mImageView.setBackgroundResource(R.drawable.tour1);
			} else if (position == 1) {
				mImageView.setBackgroundResource(R.drawable.tour2);
			} else if (position == 2) {
				mImageView.setBackgroundResource(R.drawable.tour3);
			} else if (position == 3) {
				mImageView.setBackgroundResource(R.drawable.tour4);
			} else if (position == 4) {
				mImageView.setBackgroundResource(R.drawable.tour5);
			} else if (position == 5) {
				mImageView.setBackgroundResource(R.drawable.tour6);
			} else if (position == 6) {
				mImageView.setBackgroundResource(R.drawable.tour7);
			} else if (position == 7) {
				mImageView.setBackgroundResource(R.drawable.tour8);
			} else if (position == 8) {
				mImageView.setBackgroundResource(R.drawable.tour9);
			} else if (position == 9) {
				mImageView.setBackgroundResource(R.drawable.tour10);
			}

		}
	}

	public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as
				// well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(btnStartOver)) {
			mPager.setCurrentItem(0);
		} else if (v.equals(btnFinishTour)) {
			startHomeActivity();
		}
	}

	private void startHomeActivity(){
		Intent intent = new Intent(mContext,HomeActivity.class);
		startActivity(intent);
		finish();
	}
}
