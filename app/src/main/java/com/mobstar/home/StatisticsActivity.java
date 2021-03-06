package com.mobstar.home;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.custom.ProgressWheel;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class StatisticsActivity extends Activity {
	Context mContext;

	ProgressWheel progressWheel;
	int count = 0;

	EntryPojo entryPojo;

	TextView textUserName, textTime, textDescription, textUpvotes, textDownvotes, textRank, textPositiveCount;
	ImageView imgUserPic;
	int positive_count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);

		mContext = StatisticsActivity.this;

		entryPojo = (EntryPojo) getIntent().getSerializableExtra("entry");

		InitControls();

		Utility.SendDataToGA("Statistics Screen", StatisticsActivity.this);
	}

	void InitControls() {

		textUserName = (TextView) findViewById(R.id.textUserName);
		textTime = (TextView) findViewById(R.id.textTime);
		textDescription = (TextView) findViewById(R.id.textDescription);

		textUpvotes = (TextView) findViewById(R.id.textUpvotes);
		textDownvotes = (TextView) findViewById(R.id.textDownvotes);
		textRank = (TextView) findViewById(R.id.textRank);
		textPositiveCount = (TextView) findViewById(R.id.textPositiveCount);

		if (Integer.parseInt(entryPojo.getUpVotesCount()) + Integer.parseInt(entryPojo.getDownvotesCount()) == 0) {
			positive_count = 0;
		} else {
			positive_count = Integer.parseInt(entryPojo.getUpVotesCount()) * 100
					/ (Integer.parseInt(entryPojo.getUpVotesCount()) + Integer.parseInt(entryPojo.getDownvotesCount()));
		}

		textUserName.setText(entryPojo.getName());
		textDescription.setText(StringEscapeUtils.unescapeJava(entryPojo.getDescription()));
		textTime.setText(entryPojo.getCreated());
		textUpvotes.setText(entryPojo.getUpVotesCount());
		textDownvotes.setText(entryPojo.getDownvotesCount());
		textRank.setText(entryPojo.getRank());
		textPositiveCount.setText(positive_count + "%");

		progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);

		if (positive_count != 0) {
			final Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					count++;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progressWheel.setProgress(count);
						}
					});

					if (count == positive_count) {
						timer.cancel();
					}
				}
			};
			timer.scheduleAtFixedRate(task, 300, 20);
		}

		imgUserPic = (ImageView) findViewById(R.id.imgUserPic);

		if (entryPojo.getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso.with(mContext).load(entryPojo.getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).transform(new RoundedTransformation(Utility.dpToPx(mContext, 45), 0)).into(imgUserPic);

			// Ion.with(mContext).load(entryPojo.getProfileImage()).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).resize(Utility.dpToPx(mContext,
			// 45), Utility.dpToPx(mContext,
			// 45)).centerCrop().asBitmap().setCallback(new
			// FutureCallback<Bitmap>() {
			//
			// @Override
			// public void onCompleted(Exception exception, Bitmap bitmap) {
			// // TODO Auto-generated method stub
			// if (exception == null) {
			// if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
			// Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
			// bitmap.getHeight(), Config.ARGB_8888);
			// Canvas canvas = new Canvas(output);
			//
			// final int color = 0xff424242;
			// final Paint paint = new Paint();
			// final Rect rect = new Rect(0, 0, bitmap.getWidth(),
			// bitmap.getHeight());
			//
			// paint.setAntiAlias(true);
			// canvas.drawARGB(0, 0, 0, 0);
			// paint.setColor(color);
			// canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
			// bitmap.getWidth() / 2, paint);
			// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			// canvas.drawBitmap(bitmap, rect, rect, paint);
			//
			// imgUserPic.setImageBitmap(output);
			// imgUserPic.invalidate();
			// }
			// }
			// }
			// });
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
