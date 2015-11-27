package com.mobstar.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.custom.ProgressWheel;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Timer;
import java.util.TimerTask;

public class StatisticsActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = StatisticsActivity.class.getName();
	private ProgressWheel progressWheel;
	private int count = 0;
	private EntryP entryPojo;
	private TextView textUserName, textTime, textDescription, textUpvotes, textDownvotes, textRank, textPositiveCount;
	private ImageView imgUserPic;
	private int positive_count = 0;
	private ImageButton btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		entryPojo = (EntryP) getIntent().getSerializableExtra("entry");
		findViews();
		setListeners();
		setupControls();

		Utility.SendDataToGA("Statistics Screen", StatisticsActivity.this);
	}

	private void findViews(){
		btnClose            = (ImageButton) findViewById(R.id.btnClose);
		textUserName        = (TextView) findViewById(R.id.textUserName);
		textTime            = (TextView) findViewById(R.id.textTime);
		textDescription     = (TextView) findViewById(R.id.textDescription);
		textUpvotes         = (TextView) findViewById(R.id.textUpvotes);
		textDownvotes       = (TextView) findViewById(R.id.textDownvotes);
		textRank            = (TextView) findViewById(R.id.textRank);
		textPositiveCount   = (TextView) findViewById(R.id.textPositiveCount);
		imgUserPic          = (ImageView) findViewById(R.id.imgUserPic);
		progressWheel       = (ProgressWheel) findViewById(R.id.progressWheel);

	}

	private void setListeners(){
		btnClose.setOnClickListener(this);
	}

	void setupControls() {
		setupProfileImage();
        positive_count = getPositiveCount();
		initialProgress();

		textUserName.setText(entryPojo.getUser().getDisplayName());
		textDescription.setText(StringEscapeUtils.unescapeJava(entryPojo.getEntry().getName()));
		textTime.setText(entryPojo.getEntry().getCreatedAgo());
		textUpvotes.setText(Integer.toString(entryPojo.getEntry().getUpVotes()));
		textDownvotes.setText(Integer.toString(entryPojo.getEntry().getDownVots()));
		textRank.setText(Integer.toString(entryPojo.getEntry().getRank()));
		textPositiveCount.setText(positive_count + "%");


	}

	private void initialProgress(){
		if (positive_count != 0) {
			final Timer timer = new Timer();
			final TimerTask task = new TimerTask() {
				@Override
				public void run() {
					count++;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
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
	}

	private void setupProfileImage(){
		if (entryPojo.getUser().getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso
					.with(this)
					.load(entryPojo.getUser().getProfileImage())
					.resize(Utility.dpToPx(this, 45), Utility.dpToPx(this, 45))
					.centerCrop()
					.placeholder(R.drawable.ic_pic_small)
					.error(R.drawable.ic_pic_small)
					.transform(new RoundedTransformation(Utility.dpToPx(this, 45), 0))
					.into(imgUserPic);
		}
	}

	private int getPositiveCount(){
		if (entryPojo.getEntry().getUpVotes() + entryPojo.getEntry().getDownVots() == 0) {
			return 0;
		} else {
			return entryPojo.getEntry().getUpVotes() * 100 / (entryPojo.getEntry().getUpVotes() + entryPojo.getEntry().getDownVots());
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnClose:
				onBackPressed();
				break;
		}
	}
}
