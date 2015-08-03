package com.mobstar.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.mobstar.R;
import com.mobstar.utils.Utility;

public class ApprovePhotoActivity extends Activity {

	Context mContext;

	String sImagePath;
	ImageView imageFrame;

	Button btnRetake, btnApprove;

	Typeface typefaceBtn;
	String categoryId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approve_photo);

		mContext = ApprovePhotoActivity.this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sImagePath = extras.getString("image_path");
			Bundle b=getIntent().getExtras();

			if(b.containsKey("categoryId")) {
				categoryId=b.getString("categoryId");
			}
			Log.d("mobstar","ApprovePhotoActivity=> categoryid "+categoryId);

		}

		InitControls();

		Utility.SendDataToGA("ApprovePhoto Screen", ApprovePhotoActivity.this);
	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		imageFrame = (ImageView) findViewById(R.id.imageFrame);

		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap imgbitmap = BitmapFactory.decodeFile(sImagePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 1080, 1920);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		imgbitmap = BitmapFactory.decodeFile(sImagePath, options);

		imageFrame.setImageBitmap(imgbitmap);

		btnRetake = (Button) findViewById(R.id.btnRetake);
		btnRetake.setTypeface(typefaceBtn);
		btnRetake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, TakePictureActivity.class);
				intent.putExtra("categoryId", categoryId);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

		btnApprove = (Button) findViewById(R.id.btnApprove);
		btnApprove.setTypeface(typefaceBtn);
		btnApprove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, UploadFileActivity.class);
				intent.putExtra("categoryId",categoryId);
				intent.putExtra("file1", sImagePath);
				intent.putExtra("type", "image");
				startActivityForResult(intent, 26);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 26) {
				onBackPressed();
			}
		} else {

		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
