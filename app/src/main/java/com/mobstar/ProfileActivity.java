package com.mobstar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.mobstar.custom.CustomTextview;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.fanconnect.FansActivity;
import com.mobstar.home.CommentActivity;
import com.mobstar.home.ShareActivity;
import com.mobstar.home.StatisticsActivity;
import com.mobstar.info.report.InformationReportActivity;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.pojo.StarPojo;
import com.mobstar.upload.MessageActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.EntryActionHelper;
import com.mobstar.utils.EntryActionHelper.ActionListener;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class ProfileActivity extends Activity implements OnClickListener,AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
StickyListHeadersListView.OnStickyHeaderChangedListener {

	Context mContext;

	EntryListAdapter entryListAdapter;
	StickyListHeadersListView listEntry;

	ArrayList<EntryPojo> arrEntryPojosParent=new ArrayList<EntryPojo>();

	CustomSurfaceTextureListener surfaceTextureListener;

	protected String sErrorMessage;
	ArrayList<EntryPojo> arrEntryPojos = new ArrayList<EntryPojo>();

	// int mLastVisibleItem = 0;
	int mFirstVisibleItem = 0;
	boolean isScrolling = false;

	MediaPlayer mediaPlayer;
	int indexCurrentPlayAudio = -1;
	int indexCurrentPauseVideo = -1;
	ArrayList<String> listDownloadingFile = new ArrayList<String>();

	private ArrayList<StarPojo> arrFollowers;


	SharedPreferences preferences;

	CustomTextview textTagline,textFollowers;
	CustomTextviewBold textUserName,textUserDisplayName;
	float touchX, touchY;

	EntryActionHelper entryActionHelper = new EntryActionHelper();


	String LatestORPopular = "latest";
	String CategoryId="";

	boolean isDataLoaded = false;

	boolean isEntryIdAPI=false;
	String deeplinkEntryId="";

	boolean isMobitAPI=false;

	private String UserID,EntryId, UserName = "", UserPic = "", IsMyStar = "", IAmStar= "", UserDisplayName = "", UserCoverImage = "", UserTagline = "",UserBio="", UserFan="";

	CustomTextviewBold btnEdit;

	ImageView imgUserPic, imgCoverPage, imgMsg;
	private TextView imgFollow;

	boolean isVideoSurfaceReady = false;
	boolean isMoveDone = false;
	boolean isProfile = false;
	boolean isBackPress = false;

	Surface tSurface;

	boolean isDefault=true,isDataNull=true;
	String FILEPATH;
	//	TextView textNoData;

	boolean isInPauseState = false;

	private String MixContactType1="Profile Content";
	private String MixContactType2="MobIT";
	private String addedviewImgId="";

	//	private View mPlaceholderView;
	//	private View topTransparent;
	private boolean fadeHeader = true;
	private ViewGroup bottom;
	View header;
	static int listPosition=0;
	private boolean isNotfiedUser=false;
	private boolean isMediaPlayerError=false;
	Dialog Likedialog,disLikedialog;
	private boolean isPageLoaded=false,isNextPageAvail=false;

	Handler handler;
	private int previousTotal=0;
	private int pageNum=1;
	private int oldCount=0;
	public boolean loading=true;
	public boolean isPagination=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_profile);

		mContext = ProfileActivity.this;
		handler=new Handler();

		FILEPATH = Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + mContext.getPackageName() +"/";

		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		Likedialog = new Dialog(ProfileActivity.this, R.style.DialogAnimationTheme);
		disLikedialog= new Dialog(ProfileActivity.this, R.style.DialogAnimationTheme);


		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			if (extras.containsKey("EntryId")) {
				EntryId = extras.getString("EntryId");
				isNotfiedUser=true;

				UserID=preferences.getString("userid", "0");
			}
			if (extras.containsKey("UserID")) {
				UserID = extras.getString("UserID");
			}
			if (extras.containsKey("UserName")) {
				UserName = extras.getString("UserName");
			}
			if (extras.containsKey("UserPic")) {
				UserPic = extras.getString("UserPic");
			}
			if (extras.containsKey("IsMyStar")) {
				IsMyStar = extras.getString("IsMyStar");
			}
			if (extras.containsKey("UserDisplayName")) {
				UserDisplayName = extras.getString("UserDisplayName");
			}
			if (extras.containsKey("UserCoverImage")) {
				UserCoverImage = extras.getString("UserCoverImage");
			}
			if (extras.containsKey("UserTagline")) {
				UserTagline = extras.getString("UserTagline");
			}
			if (extras.containsKey("isProfile")) {
				isProfile = extras.getBoolean("isProfile", false);
			}

		}

		mediaPlayer = new MediaPlayer();

		entryActionHelper.setActionListener(actionListener);

		InitControls();

		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("profile_image_changed"));
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("star_added"));
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("star_removed"));
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("entry_deleted"));


		if (UserID.equals(preferences.getString("userid", "0"))) {
			Utility.SendDataToGA("UserProfile Screen", ProfileActivity.this);
		} else {
			Utility.SendDataToGA("OtherProfile Screen", ProfileActivity.this);
		}

	}



	@Override
	public void onResume() {

		isInPauseState = false;
		super.onResume();
		//		Log.v(Constant.TAG, "ProfleActivity onResume");
		if (entryListAdapter != null) {
			entryListAdapter.notifyDataSetChanged();
		}
	}


	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent

			if (UserID.equals(preferences.getString("userid", "0")))
			{
				UserPic = preferences.getString("profile_image", "");

				if (UserPic.equals(""))
				{
					imgUserPic.setImageResource(R.drawable.profile_pic_new);
				}
				else
				{
					imgUserPic.setImageResource(R.drawable.profile_pic_new);

					Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
					.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUserPic);

					// Ion.with(mContext).load(UserPic).withBitmap().placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic)
					// .resize(Utility.dpToPx(mContext, 126),
					// Utility.dpToPx(mContext,
					// 126)).centerCrop().asBitmap().setCallback(new
					// FutureCallback<Bitmap>() {
					//
					// @Override
					// public void onCompleted(Exception exception, Bitmap
					// bitmap) {
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
					// canvas.drawCircle(bitmap.getWidth() / 2,
					// bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
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

				UserCoverImage = preferences.getString("cover_image", "");

				if (UserCoverImage.equals("")) {
					imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
				} else {
					imgCoverPage.setBackgroundResource(R.drawable.cover_bg);

					Picasso.with(mContext).load(UserCoverImage).fit().centerCrop().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg).into(imgCoverPage);

					// Ion.with(mContext).load(UserCoverImage).withBitmap().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg)
					// .resize(Utility.dpToPx(mContext, 360),
					// Utility.dpToPx(mContext,
					// 180)).centerCrop().asBitmap().setCallback(new
					// FutureCallback<Bitmap>() {
					//
					// @SuppressWarnings("deprecation")
					// @SuppressLint("NewApi")
					// @Override
					// public void onCompleted(Exception exception, Bitmap
					// bitmap) {
					// // TODO Auto-generated method stub
					// if (exception == null) {
					//
					// Drawable drawable = new BitmapDrawable(getResources(),
					// bitmap);
					//
					// int sdk = android.os.Build.VERSION.SDK_INT;
					// if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					// imgCoverPage.setBackgroundDrawable(drawable);
					// } else {
					// imgCoverPage.setBackground(drawable);
					// }
					//
					// imgCoverPage.invalidate();
					//
					// }
					// }
					// });
				}
			}
			if (intent.getAction().equalsIgnoreCase("star_added")) {

				String tempUserID = intent.getExtras().getString("UserID");

				for (int i = 0; i < arrEntryPojos.size(); i++) {

					if (tempUserID.equals(arrEntryPojos.get(i).getUserID())) {
						arrEntryPojos.get(i).setIsMyStar("1");
					}
				}
				if(imgFollow!=null){
					IsMyStar="1";
					imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
					imgFollow.setText(getString(R.string.following));
				}

			} else if (intent.getAction().equalsIgnoreCase("star_removed")) {

				String tempUserID = intent.getExtras().getString("UserID");

				for (int i = 0; i < arrEntryPojos.size(); i++) {

					if (tempUserID.equals(arrEntryPojos.get(i).getUserID())) {
						arrEntryPojos.get(i).setIsMyStar("0");

					}
				}
				entryListAdapter.notifyDataSetChanged();

			}
			else if (intent.getAction().equalsIgnoreCase("entry_deleted")) {
				if (intent.getStringExtra("deletedEntryId") != null) {
					for (int i = 0; i < arrEntryPojos.size(); i++) {
						if (arrEntryPojos.get(i).getID().equalsIgnoreCase(intent.getStringExtra("deletedEntryId"))) {
							arrEntryPojos.remove(i);
							break;
						}
					}
				}

				if (arrEntryPojos.size() == 0) {
					//					textNoData.setVisibility(View.VISIBLE);
					//					textNoData.setText(getString(R.string.there_are_no_entries_yet));
					isDataNull=true;
				}
				else {
					//					textNoData.setVisibility(View.GONE);
					isDataNull=false;
				}

				entryListAdapter.notifyDataSetChanged();
			}



		}
	};

	void InitControls() {
		entryListAdapter = new EntryListAdapter();
		listEntry = (StickyListHeadersListView) findViewById(R.id.listEntries);

		//		llSticky=(LinearLayout)findViewById(R.id.llSticky);
		//		llHeader=(LinearLayout)findViewById(R.id.llTop);



		//		mFragmentManager = getFragmentManager();

		//		childFragmentContent=(FrameLayout)findViewById(R.id.childFragmentContent);

		//

		//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//		View v = inflater.inflate(R.layout.top_layout, null);
		//
		//		topTransparent=v.findViewById(R.id.topTransparent);
		//		mPlaceholderView = v.findViewById(R.id.placeholder);
		//		v.setClickable(false);
		//		listEntry.addHeaderView(v,null,true);

		//		LayoutInflater inflater = getLayoutInflater();
		//		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.layout_profile_header, listEntry, false);

		//		listEntry.addHeaderView(header, null, false);


		//set height based on screen and minus from header

		//		textNoData = (TextView)findViewById(R.id.textNoData);

		//		Display display = getWindowManager().getDefaultDisplay();
		//		int width = display.getWidth();
		//		int height = display.getHeight();
		//
		//		ViewGroup.LayoutParams params = textNoData.getLayoutParams();
		//		params.height = height-header.getHeight();
		//		textNoData.setLayoutParams(params);
		//		textNoData.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		//		textNoData.setPadding(0,0,0,10);
		//		textNoData.requestLayout();


		//		textNoData.setVisibility(View.GONE);

		listEntry.setOnHeaderClickListener(this);
		listEntry.setOnStickyHeaderChangedListener(this);
		listEntry.setOnStickyHeaderOffsetChangedListener(this);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//		header = (ViewGroup)inflater.inflate(R.layout.layout_profile_header, listEntry, false);
		header = inflater.inflate(R.layout.layout_profile_header, null);
		textUserName = (CustomTextviewBold) header.findViewById(R.id.textUserName);
		textUserDisplayName = (CustomTextviewBold)header.findViewById(R.id.textUserDisplayName);
		textTagline = (CustomTextview)header.findViewById(R.id.textTagline);
		textFollowers = (CustomTextview)header.findViewById(R.id.textFollowers);
		imgUserPic = (ImageView)header.findViewById(R.id.imgUserPic);
		imgCoverPage = (ImageView)header.findViewById(R.id.imgCoverPage);
		imgFollow=(TextView)header.findViewById(R.id.imgFollow);
		imgMsg=(ImageView)header.findViewById(R.id.imgMsg);
		btnEdit = (CustomTextviewBold)header.findViewById(R.id.btnEdit);

		listEntry.addHeaderView(header);
		listEntry.setEmptyView(findViewById(R.id.textNoData));
		listEntry.setDrawingListUnderStickyHeader(true);
		listEntry.setAreHeadersSticky(true);

		textFollowers.setVisibility(View.INVISIBLE);

		btnEdit.setOnClickListener(this);

		if (UserID.equals(preferences.getString("userid", "0"))) {
			btnEdit.setVisibility(View.VISIBLE);
			imgFollow.setVisibility(View.INVISIBLE);
			imgMsg.setVisibility(View.GONE);
		} else if (IsMyStar!=null && !IsMyStar.equalsIgnoreCase("0")) {
			btnEdit.setVisibility(View.GONE);
			imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
			imgFollow.setText(getString(R.string.following));
			imgFollow.setVisibility(View.VISIBLE);
			imgMsg.setVisibility(View.VISIBLE);
		} else {
			btnEdit.setVisibility(View.GONE);
			imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
			imgFollow.setText(getString(R.string.follow));
			imgFollow.setVisibility(View.VISIBLE);
			imgMsg.setVisibility(View.VISIBLE);
		}

		textUserName.setText(UserName);
		textUserName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});


		if(UserDisplayName!=null && UserDisplayName.length()>0){
			textUserDisplayName.setText(UserDisplayName);
		}
		else {
			textUserDisplayName.setText(UserName);
		}

		if(UserTagline!=null && UserTagline.length()>0){
			textTagline.setVisibility(View.VISIBLE);
			textTagline.setText(Utility.unescape_perl_string(UserTagline));
		}
		else {
			textTagline.setVisibility(View.GONE);
		}

		textFollowers.setOnClickListener(this);


		//		textUpdates.setOnClickListener(this);


		//		textProfile.setOnClickListener(this);



		//		UpdatesFragment updatesFragment = new UpdatesFragment();
		//		Bundle extras = new Bundle();
		//		extras.putString("UserID",UserID);
		//		updatesFragment.setArguments(extras);
		//		replaceFragment(updatesFragment, "UpdatesFragment");



		if (UserPic.equals("")) {
			imgUserPic.setImageResource(R.drawable.profile_pic_new);
		} else {
			imgUserPic.setImageResource(R.drawable.profile_pic_new);
			// Ion.with(mContext).load(UserPic).withBitmap().placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic)
			// .resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext,
			// 126)).centerCrop().asBitmap().setCallback(new
			// FutureCallback<Bitmap>() {
			//
			// @Override
			// public void onCompleted(Exception exception, Bitmap bitmap) {
			// // TODO Auto-generated method stub
			// if (exception == null) {
			//
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

			// Log.v(Constant.TAG, "UserPic URl " + UserPic);
			Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
			.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUserPic);

		}

		listEntry.setAdapter(entryListAdapter);

		imgFollow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!IsMyStar.equalsIgnoreCase("0")) {

					Utility.ShowProgressDialog(mContext, getString(R.string.loading));

					if (Utility.isNetworkAvailable(mContext)) {

						new DeleteStarCall(UserID).start();

					} else {

						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
						Utility.HideDialog(mContext);
					}
				} else {
					Utility.ShowProgressDialog(mContext, getString(R.string.loading));

					if (Utility.isNetworkAvailable(mContext)) {

						new AddStarCall(UserID).start();

						final Dialog dialog = new Dialog(mContext, R.style.DialogAnimationTheme);
						dialog.setContentView(R.layout.dialog_add_star);
						dialog.show();

						Timer timer = new Timer();
						TimerTask task = new TimerTask() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						};
						timer.schedule(task, 1000);

					} else {

						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
						Utility.HideDialog(mContext);
					}

				}
			}
		});

		imgMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (IAmStar!=null && IAmStar.length()>0 && IAmStar.equalsIgnoreCase("1")) {
					//following
					Intent intent=new Intent(mContext,MessageActivity.class);
					intent.putExtra("recipent",UserID);
					intent.putExtra("isDisableCompose",true);
					startActivity(intent);
				}

			}
		});


		if (UserCoverImage == null || UserCoverImage.equals("")) {
			Log.d("mobstar","cover img is null");
			imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
		} else {
			//			imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
			Log.d("mobstar","cover img is"+UserCoverImage );
			Picasso.with(mContext).load(UserCoverImage).fit().centerCrop().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg).into(imgCoverPage);

		}



		surfaceTextureListener = new CustomSurfaceTextureListener();

		if(isNotfiedUser && EntryId!=null){

			Utility.ShowProgressDialog(mContext,getString(R.string.loading));
			if (Utility.isNetworkAvailable(mContext)) {
				Log.d("mobstar","api call... isNotifyUser");
				new GetEntryCall(EntryId).start();
			} else {
				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
			}
		}
		else {
			//			if (!isDataLoaded) {
			//				Log.d("mobstar", "call getProfile");
			//				GetData(UserID);
			//				Utility.ShowProgressDialog(mContext,"Loading");
			//				if (Utility.isNetworkAvailable(mContext)) {
			//
			//					new GetProfileCall(UserID).start();
			//
			//				} else {
			//					Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
			//					Utility.HideDialog(mContext);
			//				}

			//			} else {
			GetData(UserID);
			//			}
		}

		listEntry.setOnScrollListener(new EndlessScrollListener());



	}

	public class CustomSurfaceTextureListener implements SurfaceTextureListener {

		int position;

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int arg1, int arg2) {
			// TODO Auto-generated method stub
			// Log.v(Constant.TAG, "onSurfaceTextureAvailable " + arg1 + " " +
			// arg2);
			Surface surface = new Surface(surfaceTexture);
			tSurface = surface;

			if (!isInPauseState) {
				PlayVideo(position);
			}
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
			// TODO Auto-generated method stub
			// Log.v(Constant.TAG, "onSurfaceTextureDestroyed");
			return false;
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int arg1, int arg2) {
			// TODO Auto-generated method stub
			// Log.v(Constant.TAG, "onSurfaceTextureSizeChanged");
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture texture) {
			// TODO Auto-generated method stub
			// Log.v(Constant.TAG, "onSurfaceTextureUpdated");
		}

	}

	public int dpToPx(int dp) {

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int px = Math.round(dp * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;

	}

	void GetData(String sUserID) {

		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
		}

		if(mContext!=null){
			Utility.ShowProgressDialog(mContext, getString(R.string.loading));

			if (Utility.isNetworkAvailable(mContext)) {
				Log.d("mobstar","api call.. get data");
				new EntryCall(sUserID,pageNum).start();

			} else {

				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
			}
		}

		//		if (arrEntryPojos.size() == 0) {
		////			textNoData.setVisibility(View.VISIBLE);
		////			textNoData.setText(getString(R.string.there_are_no_entries_yet));
		//			isDataNull=true;
		//			arrEntryPojos.add(null);
		//		}
		//		else {
		////			textNoData.setVisibility(View.GONE);
		//			isDataNull=false;
		//		}
	}

	public class EntryListAdapter extends BaseAdapter implements  StickyListHeadersAdapter, SectionIndexer {

		private LayoutInflater inflater = null;

		private boolean onVoitingSwipeItem = false;

		public EntryListAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			if(isDefault){
				if(isDataNull){
					return 1;
				}
				else {
					return arrEntryPojos.size();
				}

			}else {
				return 1;
			}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			if(isDefault){
				if(isDataNull){
					return 3;//null data
				}
				else {
					return (arrEntryPojos.get(position).getCategory().equalsIgnoreCase(MixContactType1) || arrEntryPojos.get(position).getCategory().equalsIgnoreCase(MixContactType2)) ? 0 : 1;
				}

			}
			else {
				return 2;//profile
			}

		}

		@Override
		public int getViewTypeCount() {
			return 4;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			int type = getItemViewType(position);

			if(type==0 || type==1){
				convertView=FeedView(convertView,type,parent,position);
			}
			else if(type==2) {
				convertView=ProfileView(convertView,type,parent,position);
			}
			else {
				convertView=NoDataView(convertView,type,parent,position);
			}
			return convertView;
		}

		View NoDataView(View convertView,int itemType,ViewGroup parent,final int position){
			final ViewHolderNoData viewHolderNodata;
			int type = itemType;

			if (convertView == null) {
				viewHolderNodata = new ViewHolderNoData();
				// Inflate the layout with image
				convertView = inflater.inflate(R.layout.layout_profile_nodata, parent, false);
				viewHolderNodata.textNoData=(TextView)convertView.findViewById(R.id.textNoData);
				viewHolderNodata.textNoData.setText(getString(R.string.there_are_no_entries_yet));

				convertView.setTag(viewHolderNodata);

			} else {
				viewHolderNodata = (ViewHolderNoData) convertView.getTag();
			}

			viewHolderNodata.textNoData.setVisibility(View.VISIBLE);

			return convertView;

		}

		View ProfileView(View convertView,int itemType,ViewGroup parent,final int position){
			final ViewHolderProfile viewHolderProfile;
			int type = itemType;

			if (convertView == null) {
				viewHolderProfile = new ViewHolderProfile();
				// Inflate the layout with image
				convertView = inflater.inflate(R.layout.fragment_profile, parent, false);
				viewHolderProfile.imgUser=(ImageView)convertView.findViewById(R.id.imgUser);
				viewHolderProfile.textBio=(TextView)convertView.findViewById(R.id.textBio);


				convertView.setTag(viewHolderProfile);

			} else {
				viewHolderProfile = (ViewHolderProfile) convertView.getTag();
			}


			if (UserPic.equals("")) {
				viewHolderProfile.imgUser.setImageResource(R.drawable.profile_pic_new);
			} else {
				viewHolderProfile.imgUser.setImageResource(R.drawable.profile_pic_new);

				Picasso.with(mContext).load(UserPic).into(viewHolderProfile.imgUser);

				//			Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
				//			.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUser);
			}


			if(UserBio!=null && UserBio.length()>0){
				viewHolderProfile.textBio.setText(Utility.unescape_perl_string(UserBio));
				viewHolderProfile.textBio.setVisibility(View.VISIBLE);
			}
			else {
				viewHolderProfile.textBio.setVisibility(View.INVISIBLE);
			}

			return convertView;

		}

		View FeedView(View convertView,int itemType,ViewGroup parent,final int position){


			final ViewHolder viewHolder;
			int type = itemType;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				//				convertView = inflater.inflate(R.layout.row_item_entry, null);
				if (type == 0) {
					// Inflate the layout with image
					convertView = inflater.inflate(R.layout.row_item_mobit, parent, false);
					viewHolder.btnLike=(LinearLayout)convertView.findViewById(R.id.btnLike);
					viewHolder.textLikeCount=(TextView)convertView.findViewById(R.id.textLikeCount);
					viewHolder.tvLikeText = (TextView) convertView.findViewById(R.id.tvLikeText);
					viewHolder.ivLike = (ImageView) convertView.findViewById(R.id.ivLike);
				}
				else {
					convertView = inflater.inflate(R.layout.row_item_entry, parent, false);
					viewHolder.layoutStatastics = (FrameLayout) convertView.findViewById(R.id.layoutStatastic);
					viewHolder.textStatasticCount = (TextView) convertView.findViewById(R.id.textStatasticCount);
					viewHolder.imgMsg= (ImageView) convertView.findViewById(R.id.imgMsg);


				}

				viewHolder.ivIndicator=(ImageView) convertView.findViewById(R.id.ivIndicator);
				viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
				viewHolder.textViews = (TextView) convertView.findViewById(R.id.textViews);
				viewHolder.textDescription = (TextView) convertView.findViewById(R.id.textDescription);
				viewHolder.imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);
				viewHolder.progressbar = (ProgressBar) convertView.findViewById(R.id.progressbar);
				viewHolder.textureView = (TextureView) convertView.findViewById(R.id.textureView);
				viewHolder.btnShare = (FrameLayout) convertView.findViewById(R.id.btnShare);

				viewHolder.btnInfo = (FrameLayout) convertView.findViewById(R.id.btnInfo);

				viewHolder.ivAudioIcon = (ImageView) convertView.findViewById(R.id.ivAudioIcon);
				viewHolder.textCommentCount = (TextView) convertView.findViewById(R.id.textCommentCount);
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.imgPlaceHolder = (ImageView) convertView.findViewById(R.id.imgPlaceHolder);
				viewHolder.flPlaceHolder = (FrameLayout) convertView.findViewById(R.id.flPlaceHolder);
				viewHolder.btnFollow = (TextView) convertView.findViewById(R.id.btnFollow);
				viewHolder.swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if (!arrEntryPojos.get(mFirstVisibleItem).getCategory().equalsIgnoreCase(MixContactType1) || !arrEntryPojos.get(mFirstVisibleItem).getCategory().equalsIgnoreCase(MixContactType2)) {


//				viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, convertView.findViewById(R.id.rigthView));
//				viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, convertView.findViewById(R.id.leftView));
				viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
					@Override
					public void onStartOpen(SwipeLayout swipeLayout) {

					}

					@Override
					public void onOpen(SwipeLayout swipeLayout) {
						if (!onVoitingSwipeItem)
							return;
						onVoitingSwipeItem = false;
						switch (swipeLayout.getDragEdge()) {
							case Left:
								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
									if (arrEntryPojos.get(mFirstVisibleItem).getCategory().equalsIgnoreCase(MixContactType1) || arrEntryPojos.get(mFirstVisibleItem).getCategory().equalsIgnoreCase(MixContactType2)) {

									} else {
										String[] name = {"entry", "type"};
										String[] value = {arrEntryPojos.get(mFirstVisibleItem).getID(), "down"};
										entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
//									if(view!=null && !isFinishing()){
//										Log.d("mobstar","open dialog dislike");
//																					Utility.DisLikeDialog(ProfileActivity.this);
//										DisLikeDialog();
//									}


										mFirstVisibleItem = 0;
										if (mediaPlayer != null) {
											mediaPlayer.reset();
										}
										indexCurrentPlayAudio = -1;
//									entryListAdapter.notifyDataSetChanged();
									}

								}

								swipeLayout.close();
								break;
							case Right:
								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
									if (arrEntryPojos.get(mFirstVisibleItem).getCategory().equalsIgnoreCase(MixContactType1) || arrEntryPojos.get(mFirstVisibleItem).getCategory().equalsIgnoreCase(MixContactType2)) {

									} else {
										String[] name = {"entry", "type"};
										String[] value = {arrEntryPojos.get(mFirstVisibleItem).getID(), "up"};
										entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
										Log.d("mobstar", "imageFrame touch--- likedialog");

//									if(view!=null && !isFinishing()){
//										Log.d("mobstar","imageFrame touch--- view not null");
//										Log.d("mobstar","open dialog like");
//										//													Utility.LikeDialog(ProfileActivity.this);
//										LikeDialog();
//
//									}

										mFirstVisibleItem = 0;
										if (mediaPlayer != null) {
											mediaPlayer.reset();
										}
										indexCurrentPlayAudio = -1;
//									entryListAdapter.notifyDataSetChanged();

									}

								}
								swipeLayout.close();

								break;
						}

//					entryListAdapter.notifyDataSetChanged();
					}

					@Override
					public void onStartClose(SwipeLayout swipeLayout) {

					}

					@Override
					public void onClose(SwipeLayout swipeLayout) {

					}

					@Override
					public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {
						onVoitingSwipeItem = true;
					}

					@Override
					public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {

					}
				});
			}else {

			}

			viewHolder.textCommentCount.setText(arrEntryPojos.get(position).getTotalComments());
			viewHolder.textUserName.setText(arrEntryPojos.get(position).getName());
			viewHolder.textDescription.setText(Utility.unescape_perl_string(arrEntryPojos.get(position).getDescription()));
			viewHolder.textTime.setText(arrEntryPojos.get(position).getCreated());
			viewHolder.textViews.setText(arrEntryPojos.get(position).getTotalViews());

			if(type==1){

				viewHolder.textStatasticCount.setText(arrEntryPojos.get(position).getUpVotesCount());

				viewHolder.layoutStatastics.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(mContext, StatisticsActivity.class);
						intent.putExtra("entry", arrEntryPojos.get(position));
						startActivity(intent);
						overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
				});

				if (arrEntryPojos.get(position).getIAmStar()!=null && arrEntryPojos.get(position).getIAmStar().equalsIgnoreCase("1")) {
					//					viewHolder.imgMsg.setImageDrawable(drawable)
					Picasso.with(mContext).load(R.drawable.msg_act_btn).into(viewHolder.imgMsg);
				}
				else{
					Picasso.with(mContext).load(R.drawable.msg_btn).into(viewHolder.imgMsg);
				}

				viewHolder.imgMsg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (arrEntryPojos.get(position).getIAmStar() !=null && arrEntryPojos.get(position).getIAmStar().equalsIgnoreCase("1")) {
							//following
							Intent intent=new Intent(mContext,MessageActivity.class);
							intent.putExtra("recipent",arrEntryPojos.get(position).getUserID());
							intent.putExtra("isDisableCompose",true);
							startActivity(intent);
						}
					}
				});

			}
			else { //Profile row

				viewHolder.textLikeCount.setText(arrEntryPojos.get(position).getUpVotesCount());

				viewHolder.textLikeCount.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//open like list screen
						Intent intent=new Intent(mContext,LikesActivity.class);
						intent.putExtra("EntryId",arrEntryPojos.get(position).getID());
						mContext.startActivity(intent);
					}
				});

				if(arrEntryPojos.get(position).getIsVotedByYou().equalsIgnoreCase("0")){
					viewHolder.tvLikeText.setVisibility(View.GONE);
					viewHolder.ivLike.setImageResource(R.drawable.icn_like);
				}
				else {
					viewHolder.tvLikeText.setVisibility(View.VISIBLE);
					viewHolder.ivLike.setImageResource(R.drawable.icn_btn_unlike);

//					viewHolder.btnLike.setImageResource(R.drawable.btn_unlike);
				}

				viewHolder.btnLike.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(arrEntryPojos.get(position).getIsVotedByYou().equalsIgnoreCase("0")){
							//							Log.d("mobstar","vote up");
							String[] name = { "entry", "type" };
							String[] value = { arrEntryPojos.get(position).getID(), "up" };
							entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
							//							Utility.MobitLikeDialog(getActivity());

							if (mediaPlayer != null) {
								mediaPlayer.reset();
							}

							arrEntryPojos.get(position).setIsVotedByYou("1");

							notifyDataSetChanged();
						}
						else {
							//							Log.d("mobstar","vote down");
							String[] name = { "entry", "type" };
							String[] value = { arrEntryPojos.get(position).getID(),"down" };
							entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
							//							Utility.DisLikeDialog(getActivity());
							DisLikeDialog();

							if (mediaPlayer != null) {
								mediaPlayer.reset();
							}
							arrEntryPojos.get(position).setIsVotedByYou("0");
							notifyDataSetChanged();
						}

					}
				});
			}

			if(preferences.getString("userid", "0").equalsIgnoreCase(arrEntryPojos.get(position).getUserID())){
				viewHolder.btnFollow.setVisibility(View.GONE);
			}
			else {
				viewHolder.btnFollow.setVisibility(View.VISIBLE);
				//				if (arrEntryPojos.get(position).getIsMyStar() != null) {
				//					if (!arrEntryPojos.get(position).getIsMyStar().equalsIgnoreCase("0")) {
				//						viewHolder.btnFollow.setImageResource(R.drawable.btn_following);
				//					} else {
				//						viewHolder.btnFollow.setImageResource(R.drawable.btn_follow);
				//					}
				//				}
				if (!IsMyStar.equalsIgnoreCase("0")) {
					viewHolder.btnFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
					viewHolder.btnFollow.setText(getString(R.string.following));
				} else {
					viewHolder.btnFollow.setBackground(getResources().getDrawable(R.drawable.selector_oval_button));
					viewHolder.btnFollow.setText(getString(R.string.follow));
				}
			}



			viewHolder.btnFollow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//					if (arrEntryPojos.get(position).getIsMyStar() != null) {
					if (!IsMyStar.equalsIgnoreCase("0")) {
						//unfollow
						Utility.ShowProgressDialog(mContext, getString(R.string.loading));

						if (Utility.isNetworkAvailable(mContext)) {

							new DeleteStarCall(arrEntryPojos.get(position).getUserID()).start();

						} else {

							Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}

					} else {
						//follow
						Utility.ShowProgressDialog(mContext, getString(R.string.loading));

						if (Utility.isNetworkAvailable(mContext)) {

							new AddStarCall(arrEntryPojos.get(position).getUserID()).start();

							final Dialog dialog = new Dialog(mContext, R.style.DialogAnimationTheme);
							dialog.setContentView(R.layout.dialog_add_star);
							dialog.show();

							Timer timer = new Timer();
							TimerTask task = new TimerTask() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							};
							timer.schedule(task, 1000);

						} else {
							Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}

					}
					//					}

				}
			});


			viewHolder.textUserName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					Intent intent = new Intent(mContext, ProfileActivity.class);
//					intent.putExtra("UserID", arrEntryPojos.get(position).getUserID());
//					intent.putExtra("UserName", arrEntryPojos.get(position).getUserName());
//					intent.putExtra("UserDisplayName", arrEntryPojos.get(position).getUserDisplayName());
//					intent.putExtra("UserPic", arrEntryPojos.get(position).getProfileImage());
//					intent.putExtra("UserCoverImage", arrEntryPojos.get(position).getProfileCover());
//					intent.putExtra("IsMyStar", IsMyStar);
//					intent.putExtra("UserTagline", arrEntryPojos.get(position).getTagline());
//					startActivity(intent);
//					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			if (arrEntryPojos.get(position).getProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrEntryPojos.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);

				// Ion.with(mContext).load(arrEntryPojos.get(position).getProfileImage()).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small)
				// .resize(Utility.dpToPx(mContext, 45),
				// Utility.dpToPx(mContext,
				// 45)).centerCrop().asBitmap().setCallback(new
				// FutureCallback<Bitmap>() {
				//
				// @Override
				// public void onCompleted(Exception exception, Bitmap bitmap) {
				// // TODO Auto-generated method stub
				// if (exception == null) {
				// viewHolder.imgUserPic.setImageBitmap(bitmap);
				// } else {
				// // Log.v(Constant.TAG, "Exception " +
				// // exception.toString());
				// }
				// }
				// });
			}

			viewHolder.imgUserPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(!UserID.equalsIgnoreCase(arrEntryPojos.get(position).getUserID())){
						Intent intent = new Intent(mContext, ProfileActivity.class);
						intent.putExtra("UserCoverImage", arrEntryPojos.get(position).getProfileCover());
						intent.putExtra("UserID", arrEntryPojos.get(position).getUserID());
						intent.putExtra("UserName", arrEntryPojos.get(position).getUserName());
						intent.putExtra("UserDisplayName", arrEntryPojos.get(position).getUserDisplayName());
						intent.putExtra("UserPic", arrEntryPojos.get(position).getProfileImage());
						intent.putExtra("IsMyStar", IsMyStar);
						intent.putExtra("UserTagline", arrEntryPojos.get(position).getTagline());
						startActivity(intent);
						overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}

				}
			});

			viewHolder.btnShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, ShareActivity.class);
					intent.putExtra("entry", arrEntryPojos.get(position));
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

					// if
					// (arrEntryPojos.get(position).getType().equals("image")) {
					// Utility.ShareLink(mContext,
					// arrEntryPojos.get(position).getImageLink());
					// } else if
					// (arrEntryPojos.get(position).getType().equals("audio")) {
					// Utility.ShareLink(mContext,
					// arrEntryPojos.get(position).getAudioLink());
					// } else if
					// (arrEntryPojos.get(position).getType().equals("video")) {
					// Utility.ShareLink(mContext,
					// arrEntryPojos.get(position).getVideoLink());
					// }

				}
			});

			viewHolder.btnInfo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (isDataLoaded) {

					}
					Intent intent = new Intent(mContext, InformationReportActivity.class);
					intent.putExtra("entry", arrEntryPojos.get(position));
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			viewHolder.textCommentCount.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, CommentActivity.class);
					intent.putExtra("entry_id", arrEntryPojos.get(position).getID());
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			final String sFileName;

			if (arrEntryPojos.get(position).getAudioLink() != null) {
				sFileName = Utility.GetFileNameFromURl(arrEntryPojos.get(position).getAudioLink());
			} else if (arrEntryPojos.get(position).getVideoLink() != null) {
				sFileName = Utility.GetFileNameFromURl(arrEntryPojos.get(position).getVideoLink());
			} else {
				sFileName = "null";
			}

			viewHolder.textureView.setVisibility(View.GONE);
			viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
			viewHolder.imageFrame.setVisibility(View.GONE);
			viewHolder.flPlaceHolder.setVisibility(View.VISIBLE);

			if (arrEntryPojos.get(position).getType().equals("image")) {

				if(type==1){
					Picasso.with(mContext).load(R.drawable.indicator_image).into(viewHolder.ivIndicator);
				}

				// Log.v(Constant.TAG, "image position " + position);

				viewHolder.ivAudioIcon.setVisibility(View.GONE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.image_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);
				viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);

				//				Picasso.with(mContext).load(arrEntryPojos.get(position).getImageLink()).resize(Utility.dpToPx(mContext, 360), Utility.dpToPx(mContext, 360)).centerCrop()
				Picasso.with(mContext).load(arrEntryPojos.get(position).getImageLink())
				.placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).into(viewHolder.imageFrame, new Callback() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						viewHolder.progressbar.setVisibility(View.GONE);
						viewHolder.imageFrame.setVisibility(View.VISIBLE);
						notifyDataSetChanged();
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				});
				//
				// Ion.with(mContext).load(arrEntryPojos.get(position).getImageLink()).withBitmap().placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder)
				// .resize(Utility.dpToPx(mContext, 332),
				// Utility.dpToPx(mContext,
				// 360)).centerCrop().asBitmap().setCallback(new
				// FutureCallback<Bitmap>() {
				//
				// @Override
				// public void onCompleted(Exception exception, Bitmap bitmap) {
				// // TODO Auto-generated method stub
				// if (exception == null) {
				// viewHolder.progressbar.setVisibility(View.GONE);
				// viewHolder.imageFrame.setImageBitmap(bitmap);
				// viewHolder.imageFrame.setVisibility(View.VISIBLE);
				// notifyDataSetChanged();
				// } else {
				// // Log.v(Constant.TAG, "Exception " +
				// // exception.toString());
				// }
				// }
				// });

			} else if (arrEntryPojos.get(position).getType().equals("audio")) {

				if(type==1){
					Picasso.with(mContext).load(R.drawable.indicator_audio).into(viewHolder.ivIndicator);
				}


				viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
				viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);

				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.audio_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);

				Picasso.with(mContext).load(arrEntryPojos.get(position).getImageLink())
				.into(viewHolder.imageFrame, new Callback() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						viewHolder.progressbar.setVisibility(View.GONE);
						viewHolder.imageFrame.setVisibility(View.VISIBLE);

						if (!listDownloadingFile.contains(sFileName)) {

							//							File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);

							try {
								File file = new File(FILEPATH + sFileName);

								if (file!=null && !file.exists()) {

									listDownloadingFile.add(sFileName);

									if(Utility.isNetworkAvailable(mContext)){
										AsyncHttpClient client = new AsyncHttpClient();
										final int DEFAULT_TIMEOUT = 60 * 1000;
										client.setTimeout(DEFAULT_TIMEOUT);

										client.get(arrEntryPojos.get(position).getAudioLink(), new FileAsyncHttpResponseHandler(file) {

											@Override
											public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
												//												Log.d("mobstar","Downloading Faill");

											}

											@Override
											public void onSuccess(int arg0, Header[] arg1, File file) {
												// TODO Auto-generated
												// method
												// stub
												// Log.v(Constant.TAG,
												// "onSuccess Audio File  downloaded");

												listDownloadingFile.remove(file.getName());
												notifyDataSetChanged();
											}

										});
									}
									else {
										Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
									}



									// Ion.with(mContext).load(arrEntryPojos.get(position).getAudioLink()).write(file).setCallback(new
									// FutureCallback<File>() {
									// @Override
									// public void onCompleted(Exception e,
									// File file) {
									// if (e == null) {
									// viewHolder.progressbar.setVisibility(View.GONE);
									//
									// // Log.v(Constant.TAG,
									// // "getName " +
									// // file.getName());
									// listDownloadingFile.remove(file.getName());
									// notifyDataSetChanged();
									// }
									//
									// }
									// });
								} else {
									viewHolder.progressbar.setVisibility(View.GONE);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							viewHolder.progressbar.setVisibility(View.VISIBLE);
						}
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				});

				// Ion.with(mContext).load(arrEntryPojos.get(position).getImageLink()).withBitmap().placeholder(R.drawable.audio_placeholder).error(R.drawable.audio_placeholder)
				// .resize(Utility.dpToPx(mContext, 332),
				// Utility.dpToPx(mContext,
				// 360)).centerCrop().asBitmap().setCallback(new
				// FutureCallback<Bitmap>() {
				//
				// @Override
				// public void onCompleted(Exception exception, Bitmap bitmap) {
				// // TODO Auto-generated method stub
				//
				// viewHolder.imageFrame.setImageBitmap(bitmap);
				// viewHolder.imageFrame.setVisibility(View.VISIBLE);
				//
				// if (!listDownloadingFile.contains(sFileName)) {
				//
				// File file = new
				// File(Environment.getExternalStorageDirectory() + "/.mobstar/"
				// + sFileName);
				//
				// if (!file.exists()) {
				//
				// listDownloadingFile.add(sFileName);
				//
				// Ion.with(mContext).load(arrEntryPojos.get(position).getAudioLink()).write(file).setCallback(new
				// FutureCallback<File>() {
				// @Override
				// public void onCompleted(Exception e, File file) {
				// if (e == null) {
				// viewHolder.progressbar.setVisibility(View.GONE);
				//
				// // Log.v(Constant.TAG,
				// // "getName " +
				// // file.getName());
				// listDownloadingFile.remove(file.getName());
				// notifyDataSetChanged();
				// }
				//
				// }
				// });
				// } else {
				// viewHolder.progressbar.setVisibility(View.GONE);
				// }
				// } else {
				// viewHolder.progressbar.setVisibility(View.VISIBLE);
				// }
				// }
				// });

			} else if (arrEntryPojos.get(position).getType().equals("video")) {

				if(type==1){
					Picasso.with(mContext).load(R.drawable.indicator_video).into(viewHolder.ivIndicator);
				}

				viewHolder.ivAudioIcon.setVisibility(View.GONE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);
				//				viewHolder.progressWheel.setVisibility(View.VISIBLE);

				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.video_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);

				Picasso.with(mContext).load(arrEntryPojos.get(position).getVideoThumb())
				.placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).into(viewHolder.imageFrame, new Callback() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						viewHolder.progressbar.setVisibility(View.GONE);
						viewHolder.imageFrame.setVisibility(View.VISIBLE);
						notifyDataSetChanged();
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				});

				// Ion.with(mContext).load(arrEntryPojos.get(position).getVideoThumb()).withBitmap().placeholder(R.drawable.video_placeholder).error(R.drawable.video_placeholder)
				// .resize(Utility.dpToPx(mContext, 332),
				// Utility.dpToPx(mContext,
				// 360)).centerCrop().asBitmap().setCallback(new
				// FutureCallback<Bitmap>() {
				//
				// @Override
				// public void onCompleted(Exception exception, Bitmap bitmap) {
				// // TODO Auto-generated method stub
				// if (exception == null) {
				//
				// viewHolder.progressbar.setVisibility(View.GONE);
				// viewHolder.imageFrame.setVisibility(View.VISIBLE);
				// viewHolder.imageFrame.setImageBitmap(bitmap);
				//
				// // Log.v(Constant.TAG,
				// // "Video thumbnail is loaded " + position);
				//
				// } else {
				// // Log.v(Constant.TAG, "Exception " +
				// // exception.toString());
				// }
				// }
				// });

				// ***************temp comment by khyati

				if (!listDownloadingFile.contains(sFileName)) {

					//					File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);

					try {
						File file = new File(FILEPATH + sFileName);

						if (file!=null && !file.exists()) {

							listDownloadingFile.add(sFileName);

							if(Utility.isNetworkAvailable(mContext)){
								AsyncHttpClient client = new AsyncHttpClient();
								final int DEFAULT_TIMEOUT = 60 * 1000;

								client.setTimeout(DEFAULT_TIMEOUT);
								client.get(arrEntryPojos.get(position).getVideoLink(), new FileAsyncHttpResponseHandler(file) {


									@Override
									public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
										//										Log.d("mobstar","video Downloading Fail"+arrEntryPojos.get(position).getVideoLink());

									}

									@Override
									public void onSuccess(int arg0, Header[] arg1, File file) {
										// TODO Auto-generated method stub
										// Log.v(Constant.TAG,
										// "onSuccess Video File  downloaded");
										viewHolder.progressbar.setVisibility(View.GONE);
										viewHolder.textureView.setVisibility(View.GONE);

										listDownloadingFile.remove(file.getName());

										notifyDataSetChanged();
									}

								});

							}
							else {
								Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							}
							// Log.v(Constant.TAG, "Download video " +
							// arrEntryPojos.get(position).getVideoLink());
							//						commented by khyati


							// Ion.with(mContext).load(arrEntryPojos.get(position).getVideoLink()).write(file).setCallback(new
							// FutureCallback<File>() {
							// @Override
							// public void onCompleted(Exception e, File file) {
							// if (file != null && e == null) {
							//
							// viewHolder.progressbar.setVisibility(View.GONE);
							// viewHolder.textureView.setVisibility(View.GONE);
							//
							// listDownloadingFile.remove(file.getName());
							//
							// notifyDataSetChanged();
							//
							// }
							//
							// }
							// });
						} else {
							viewHolder.progressbar.setVisibility(View.GONE);
							viewHolder.textureView.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				else {
					viewHolder.progressbar.setVisibility(View.VISIBLE);
				}

			}

			viewHolder.imageFrame.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (arrEntryPojos.get(position).getType().equals("audio") && !listDownloadingFile.contains(sFileName)) {
						//will not fire other feed click // khayti
						if(indexCurrentPlayAudio == position){
							if (mediaPlayer != null) {
								if (mediaPlayer.isPlaying()) {
									mediaPlayer.pause();
									viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
									viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);
									indexCurrentPauseVideo = position;
								} else {
									PlayAudio(position);
									viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
									viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);
									indexCurrentPauseVideo = -1;
								}
							} else {
								PlayAudio(position);
								viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
								viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);
								indexCurrentPauseVideo = -1;

							}
						}

					} else if (arrEntryPojos.get(position).getType().equals("video") && !listDownloadingFile.contains(sFileName)) {
						//will not fire other feed click // khayti
						if(indexCurrentPlayAudio == position || indexCurrentPauseVideo == position){
							if (mediaPlayer != null) {
								if (mediaPlayer.isPlaying()) {
									mediaPlayer.pause();
									indexCurrentPauseVideo = position;
									viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
									viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);
								} else {
									// isVideoSurfaceReady = false;
									// Log.v(Constant.TAG,
									// "imageFrame ACTION_UP1");
									indexCurrentPauseVideo = -1;
									isVideoSurfaceReady = true;
									notifyDataSetChanged();
								}
							} else {

								// isVideoSurfaceReady = false;
								// Log.v(Constant.TAG,
								// "imageFrame ACTION_UP2");
								indexCurrentPlayAudio = -1;
								indexCurrentPauseVideo = -1;

								isVideoSurfaceReady = true;
								notifyDataSetChanged();
							}
						}

					}
				}
			});

			viewHolder.textureView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (arrEntryPojos.get(position).getType().equals("video") && !listDownloadingFile.contains(sFileName)) {
						if (mediaPlayer != null) {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.pause();

								indexCurrentPauseVideo = position;

								viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
								viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);

							} else {

								indexCurrentPauseVideo = -1;

								isVideoSurfaceReady = true;
								entryListAdapter.notifyDataSetChanged();

								// Log.v(Constant.TAG,
								// "textureView ACTION_UP1");
							}
						} else {
							indexCurrentPlayAudio = -1;
							indexCurrentPauseVideo = -1;
							isVideoSurfaceReady = true;
							entryListAdapter.notifyDataSetChanged();

							// Log.v(Constant.TAG,
							// "textureView ACTION_UP2");

						}
					}
					else if(arrEntryPojos.get(position).getType().equals("audio") && !listDownloadingFile.contains(sFileName)){
						if (mediaPlayer != null) {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.pause();

								indexCurrentPauseVideo = position;
								Log.d("mobstar","audio pause 2");
								viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
								viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);

							} else {
								Log.d("mobstar","go for play3");
								PlayAudio(position);
								viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
								viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);
								indexCurrentPauseVideo = -1;
								//										 Log.v(Constant.TAG,
								//										 "textureView ACTION_UP1");
							}
						} else {
							indexCurrentPlayAudio = -1;
							indexCurrentPauseVideo = -1;
							isVideoSurfaceReady = true;
							entryListAdapter.notifyDataSetChanged();

							//									 Log.v(Constant.TAG,
							//									 "textureView ACTION_UP2");

						}
					}
				}
			});

			if (mFirstVisibleItem == position && !isScrolling) {

				//				Log.d(Constant.TAG,"same position video");

				if (!listDownloadingFile.contains(sFileName)) {

					if (arrEntryPojos.get(position).getType().equals("audio")) {

						if(indexCurrentPauseVideo == position){
							viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
							viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);
						}
						else{
							viewHolder.textureView.setVisibility(View.GONE);
							// viewHolder.textBgGray.setVisibility(View.GONE);
							viewHolder.imageFrame.setVisibility(View.VISIBLE);

							PlayAudio(position);
						}

					} else if (arrEntryPojos.get(position).getType().equals("video")) {

						//				Log.v(Constant.TAG, "1 Play Video " + position +
						//								" isVideoSurfaceReady " + isVideoSurfaceReady +
						//								" indexCurrentPlayAudio " + indexCurrentPlayAudio);


						if (indexCurrentPauseVideo == position) {
							viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
							viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);

						} else {


							indexCurrentPauseVideo = -1;

							viewHolder.textureView.setVisibility(View.VISIBLE);

							if (mediaPlayer != null && mediaPlayer.isPlaying() && indexCurrentPlayAudio == position) {

								viewHolder.flPlaceHolder.setVisibility(View.GONE);
								viewHolder.progressbar.setVisibility(View.GONE);
								// viewHolder.textBgGray.setVisibility(View.GONE);
							} else if (isVideoSurfaceReady) {
								// Log.v(Constant.TAG,
								// "isVideoSurfaceReady Play Video " +
								// position);

								viewHolder.flPlaceHolder.setVisibility(View.GONE);
								viewHolder.progressbar.setVisibility(View.GONE);
								// viewHolder.textBgGray.setVisibility(View.GONE);
							} else {
								viewHolder.flPlaceHolder.setVisibility(View.VISIBLE);
								viewHolder.progressbar.setVisibility(View.VISIBLE);
								// viewHolder.textBgGray.setVisibility(View.VISIBLE);
							}

							surfaceTextureListener.position = position;
							viewHolder.textureView.setSurfaceTextureListener(surfaceTextureListener);

							if (viewHolder.textureView.isAvailable()) {


								if (indexCurrentPlayAudio != position) {
									Surface surface = new Surface(viewHolder.textureView.getSurfaceTexture());
									tSurface = surface;
								}
								if (!isInPauseState) {
									PlayVideo(position);

								}
							}

						}

					} else {

						if(arrEntryPojos.get(position).getType().equals("image")){
							if(addedviewImgId!=null && !addedviewImgId.equalsIgnoreCase(arrEntryPojos.get(position).getID())){
								if (Utility.isNetworkAvailable(mContext)) {
									addedviewImgId=arrEntryPojos.get(position).getID();
									new UpdateViewCountCall(arrEntryPojos.get(position).getID()).start();
								} else {
									Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
									Utility.HideDialog(mContext);
								}
							}
							else{
							}

						}

						// Log.v(Constant.TAG, "else1 mediaPlayer pause");

						if (mediaPlayer != null) {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.pause();
							}
						}
					}
				} else {

					// Log.v(Constant.TAG, "else2 mediaPlayer pause");

					if (mediaPlayer != null) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.pause();
						}
					}

				}
			}

			return convertView;

		}

		class ViewHolder {
			TextView textUserName, textDescription, textTime,textViews,textLikeCount;
			ImageView imageFrame;
			ProgressBar progressbar;
			TextureView textureView;
			FrameLayout btnShare;
			TextView btnFollow;
			FrameLayout btnInfo;
			LinearLayout btnLike;
			ImageView ivAudioIcon;
			ImageView imgUserPic;
			TextView textCommentCount;
			ImageView imgPlaceHolder;
			FrameLayout flPlaceHolder;
			FrameLayout layoutStatastics;
			TextView textStatasticCount;
			ImageView imgMsg,ivIndicator;
			TextView tvLikeText;
			ImageView ivLike;
			SwipeLayout swipeLayout;
		}

		class ViewHolderProfile {
			TextView textBio ;
			ImageView imgUser;

		}

		class ViewHolderNoData {
			TextView textNoData ;

		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getPositionForSection(int sectionIndex) {
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public View getHeaderView(final int position, View convertView, ViewGroup parent) {
			final HeaderViewHolder holder;

			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = inflater.inflate(R.layout.layout_profile_sticky_header, parent, false);
				holder.textUpdates=(TextView)convertView.findViewById(R.id.textUpdates);
				holder.textProfile=(TextView)convertView.findViewById(R.id.textProfile);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}

			if(isDefault){
				holder.textUpdates.setBackgroundColor(getResources().getColor(R.color.splash_bg));
				holder.textProfile.setBackgroundColor(getResources().getColor(R.color.gray_color));
			}
			else {
				holder.textUpdates.setBackgroundColor(getResources().getColor(R.color.gray_color));
				holder.textProfile.setBackgroundColor(getResources().getColor(R.color.splash_bg));
			}


			holder.textUpdates.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if(!isDefault){

						isDefault=true;

						if(isDataNull){
							//							Log.d("mobstar","array size"+arrEntryPojos.size());
						}
						else {
							isInPauseState = false;
							//						listEntry.smoothScrollToPosition(0);
							notifyDataSetChanged();
							listEntry.setFastScrollEnabled(true);
							listEntry.smoothScrollToPosition(listPosition+1);


							//							View c = listEntry.getChildAt(0);
							//							//						int scrolly = -c.getTop() + listEntry.getFirstVisiblePosition() * c.getHeight();
							//							int scrolly = -c.getTop();
							//							Log.d("mobstar","Scroll y=====>"+scrolly);
							//							listEntry.scrollBy(0,scrolly);
							float x=(float) listEntry.getX();
							float y=(float) listEntry.getY();
							long downTime = SystemClock.uptimeMillis();
							long eventTime = SystemClock.uptimeMillis() + 100;
							//							float x = 0.0f;
							//							float y = 0.0f;
							int metaState = 0;
							MotionEvent motionEvent = MotionEvent.obtain(
									downTime, 
									eventTime, 
									MotionEvent.ACTION_UP, 
									x, 
									y, 
									metaState
									);

							// Dispatch touch event to view
							listEntry.onTouchEvent(motionEvent);
						}

						holder.textUpdates.setBackgroundColor(getResources().getColor(R.color.gray_color));
						holder.textProfile.setBackgroundColor(getResources().getColor(R.color.splash_bg));
						notifyDataSetChanged();



					}
				}
			});

			holder.textProfile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(isDefault){
						isDefault=false;
						if(isDataNull){

						}
						else {
							if (mediaPlayer != null) {
								mediaPlayer.reset();
							}

							isVideoSurfaceReady = false;
							isInPauseState = true;
							indexCurrentPlayAudio = -1;


							listPosition=listEntry.getFirstVisiblePosition();

						}
						holder.textUpdates.setBackgroundColor(getResources().getColor(R.color.splash_bg));
						holder.textProfile.setBackgroundColor(getResources().getColor(R.color.gray_color));
						notifyDataSetChanged();

					}
				}
			});



			return convertView;
		}




		@Override
		public long getHeaderId(int position) {
			// TODO Auto-generated method stub
			return 1;
		}
		class HeaderViewHolder {
			TextView textUpdates,textProfile;
		}
	}

	void PlayVideo(int position) {

		if (indexCurrentPlayAudio == position) {

			//			Log.v(Constant.TAG, "Current video is playing");

			if (!mediaPlayer.isPlaying()) {
				//				Log.v(Constant.TAG, "mediaPlayer.isPlaying() " +
				//						mediaPlayer.isPlaying() + " Set Surface agian");
				mediaPlayer.start();

				entryListAdapter.notifyDataSetChanged();
			}
			if (!isVideoSurfaceReady) {
				isVideoSurfaceReady = true;

				entryListAdapter.notifyDataSetChanged();
				//				Log.v(Constant.TAG, "PlayVideo isVideoSurfaceReady");
			}

		} else {

			// Log.v(Constant.TAG, "Current video is not playing");
			//khyati do webcall for view
			Log.d("Mobstar","updateViewCall first time view....");
			if (Utility.isNetworkAvailable(mContext)) {
				new UpdateViewCountCall(arrEntryPojos.get(position).getID()).start();
			} else {
				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				//				Utility.HideDialog(mContext);
			}

			indexCurrentPlayAudio = position;

			new Thread() {

				public void run() {

					try {
						if (mediaPlayer != null) {
							mediaPlayer.reset();
						}

						final String sFileName = Utility.GetFileNameFromURl(arrEntryPojos.get(indexCurrentPlayAudio).getVideoLink());

						//						File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);

						File file = new File(FILEPATH + sFileName);

						if (file.exists()) {

							//							mediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);

							mediaPlayer.setDataSource(FILEPATH + sFileName);
							mediaPlayer.setSurface(tSurface);
							mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
							// Play video when the media source is ready for
							// playback.
							mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
								@Override
								public void onPrepared(final MediaPlayer mediaPlayer) {

									// Log.v(Constant.TAG,
									// "mediaPlayer onPrepared");
									mediaPlayer.start();

									Timer timer = new Timer();
									TimerTask task = new TimerTask() {

										@Override
										public void run() {
											// TODO Auto-generated
											// method stub

											runOnUiThread(new Runnable() {

												@Override
												public void run() {
													// TODO
													// Auto-generated
													// method
													// stub

													isVideoSurfaceReady = true;

													entryListAdapter.notifyDataSetChanged();
													listEntry.invalidate();

													// Log.v(Constant.TAG,
													// "Video setOnPreparedListener");
												}
											});

										}
									};
									timer.schedule(task, 500);
								}
							});
							//							mediaPlayer.setLooping(true);
							//Added by khyati
							mediaPlayer.setLooping(false);
							mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {

									if(indexCurrentPlayAudio>=0 && !isMediaPlayerError){
										Log.d("mobstar","update view on completion...");
										if (Utility.isNetworkAvailable(mContext)) {
											new UpdateViewCountCall(arrEntryPojos.get(indexCurrentPlayAudio).getID()).start();
										} else {
											Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
											Utility.HideDialog(mContext);
										}
										mediaPlayer.seekTo(0);
										mediaPlayer.start();
									}
									else {
										isMediaPlayerError=false;
									}

								}
							});

							mediaPlayer.setOnErrorListener(new OnErrorListener() {

								@Override
								public boolean onError(MediaPlayer mp, int what, int extra) {
									if(mediaPlayer!=null){
										if(mediaPlayer.isPlaying())
											mediaPlayer.pause();
										isMediaPlayerError=true;
										mediaPlayer.reset();

									}
									return false;
								}
							});
							mediaPlayer.prepareAsync();

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.start();
		}

	}

	void PlayAudio(int position) {


		// Log.v(Constant.TAG, "indexCurrentPlayAudio " +
		// indexCurrentPlayAudio);

		if (indexCurrentPlayAudio == position) {

			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.start();
			}

		} else {

			indexCurrentPlayAudio = position;
			Log.d("mobstar","update view first time...");
			if (Utility.isNetworkAvailable(mContext)) {
				new UpdateViewCountCall(arrEntryPojos.get(position).getID()).start();
			} else {
				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				//				Utility.HideDialog(mContext);
			}

			new Thread() {

				public void run() {

					try {
						if (mediaPlayer != null) {
							mediaPlayer.reset();
						}

						final String sFileName = Utility.GetFileNameFromURl(arrEntryPojos.get(indexCurrentPlayAudio).getAudioLink());

						// Log.v(Constant.TAG, "sFileName " +
						// Environment.getExternalStorageDirectory() +
						// "/.mobstar/" + sFileName);

						//						File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);

						File file = new File(FILEPATH + sFileName);

						if (file.exists()) {
							//							mediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);

							mediaPlayer.setDataSource(FILEPATH + sFileName);

							// Play video when the media source is ready for
							// playback.
							mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
								@Override
								public void onPrepared(MediaPlayer mediaPlayer) {
									mediaPlayer.start();
									// Log.v(Constant.TAG,
									// "setOnPreparedListener");
								}
							});
							//							mediaPlayer.setLooping(true);
							//Added by Khyati
							mediaPlayer.setLooping(false);
							mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {

									if(indexCurrentPlayAudio>=0 && !isMediaPlayerError){
										if (Utility.isNetworkAvailable(mContext)) {
											Log.d("mobstar","update view on completion...");
											new UpdateViewCountCall(arrEntryPojos.get(indexCurrentPlayAudio).getID()).start();
										} else {
											Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
											Utility.HideDialog(mContext);
										}
										mediaPlayer.seekTo(0);
										mediaPlayer.start();
									}
									else {
										isMediaPlayerError=false;
									}
								}
							});

							mediaPlayer.setOnErrorListener(new OnErrorListener() {

								@Override
								public boolean onError(MediaPlayer mp, int what, int extra) {
									if(mediaPlayer!=null){
										if(mediaPlayer.isPlaying())
											mediaPlayer.pause();
										isMediaPlayerError=true;
										mediaPlayer.reset();

									}
									return false;
								}
							});
							mediaPlayer.prepareAsync();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.start();
		}

	}

	class UpdateViewCountCall extends Thread {

		String entryId;

		public UpdateViewCountCall(String entryId) {
			this.entryId = entryId;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = {"entryId","userId"};
			String[] value = {entryId,preferences.getString("userid", "0")};
			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.UPDATE_VIEW_COUNT, name, value,preferences.getString("token", null));

			//			Log.v(Constant.TAG, "UpdateView Response-> " + response);

			if (response != null) {

				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerUpdateViewCount.sendEmptyMessage(0);
					} else {
						handlerUpdateViewCount.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
					handlerUpdateViewCount.sendEmptyMessage(0);
				}

			} else {

				handlerUpdateViewCount.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerUpdateViewCount = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {

			} else {
				//				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	class EntryCall extends Thread {

		String sUserID;
		int pageNo;

		public EntryCall(String sUserID,int page) {
			// TODO Auto-generated constructor stub
			this.sUserID = sUserID;
			this.pageNo = page;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.MIX_ENTRY + "?user=" + sUserID+"&page="+pageNo , preferences.getString("token", null));

			//			Log.v(Constant.TAG, "EntryCall Update Fragment response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}
					arrEntryPojosParent.clear();
					if(jsonObject.has("starredBy")){
						arrFollowers=new ArrayList<StarPojo>();
						JSONArray jsonArrayStarredBy = jsonObject.getJSONArray("starredBy");
						for (int i = 0; i < jsonArrayStarredBy.length(); i++) {
							JSONObject jsonObj = jsonArrayStarredBy.getJSONObject(i);
							StarPojo star=new StarPojo();
							star.setStarID(jsonObj.getString("starId"));
							star.setStarName(jsonObj.getString("starName"));
							star.setStarredDate(jsonObj.getString("starredDate"));
							star.setProfileImage(jsonObj.getString("profileImage"));
							star.setProfileCover(jsonObj.getString("profileCover"));
							arrFollowers.add(star);
						}
					}

					if(jsonObject.has("fans")){
						UserFan=jsonObject.getString("fans");
					}

					if (jsonObject.has("entries")) {



						JSONArray jsonArrayEntries = jsonObject.getJSONArray("entries");

						for (int i = 0; i < jsonArrayEntries.length(); i++) {

							JSONObject jsonObj = jsonArrayEntries.getJSONObject(i);

							JSONObject jsonObjEntry = jsonObj.getJSONObject("entry");

							EntryPojo entryPojo = new EntryPojo();

							if (jsonObjEntry.has("user")) {
								JSONObject jsonObjUser = jsonObjEntry.getJSONObject("user");
								entryPojo.setUserID(jsonObjUser.getString("id"));

								UserID=jsonObjUser.getString("id");
								if(UserPic == ""){
									UserPic=jsonObjUser.getString("profileImage");
								}
								entryPojo.setProfileImage(jsonObjUser.getString("profileImage"));

								if (jsonObjUser.has("isMyStar")) {
									entryPojo.setIsMyStar(jsonObjUser.getString("isMyStar"));
									IsMyStar=jsonObjUser.getString("isMyStar");
								}

								if(jsonObjUser.has("iAmStar")){
									entryPojo.setIAmStar(jsonObjUser.getString("iAmStar"));
									IAmStar=jsonObjUser.getString("iAmStar");
								}

								if(UserCoverImage == null || UserCoverImage == ""){
									UserCoverImage = jsonObjUser.getString("profileCover");
								}

								if(UserDisplayName == "" &&jsonObjUser.has("displayName")){
									UserDisplayName=jsonObjUser.getString("displayName");
								}

								UserTagline = jsonObjUser.getString("tagLine");
								UserBio=jsonObjUser.getString("bio");

								if(jsonObjUser.has("userName")){
									UserName=jsonObjUser.getString("userName");
								}
							}

							//							Log.d("mobstar","id=>"+jsonObjEntry.isNull("id"));
							if(jsonObjEntry.isNull("id")){
								break;
							}
							else {


								entryPojo.setID(jsonObjEntry.getString("id"));



								String category=jsonObjEntry.getString("category");
								entryPojo.setCategory(category);
								//								Log.d("mobstar","Category is=>"+category);
								if(category!=null && category.equalsIgnoreCase("Profile Content") || category.equalsIgnoreCase("MobIT") ){
									entryPojo.setIsVotedByYou(jsonObjEntry.getString("isVotedByYou"));
								}
								entryPojo.setType(jsonObjEntry.getString("type"));
								entryPojo.setName(jsonObjEntry.getString("name"));
								entryPojo.setDescription(jsonObjEntry.getString("description"));
								entryPojo.setCreated(jsonObjEntry.getString("created"));
								entryPojo.setModified(jsonObjEntry.getString("modified"));
								if (jsonObjEntry.has("upVotes")) {
									entryPojo.setUpVotesCount(jsonObjEntry.getString("upVotes"));
								}

								if (jsonObjEntry.has("downVotes")) {
									entryPojo.setDownvotesCount(jsonObjEntry.getString("downVotes"));
								}
								if (jsonObjEntry.has("rank")) {
									entryPojo.setRank(jsonObjEntry.getString("rank"));
								}
								if (jsonObjEntry.has("language")) {
									entryPojo.setLanguage(jsonObjEntry.getString("language"));
								}
								if (jsonObjEntry.has("deleted")) {
									entryPojo.setDeleted(jsonObjEntry.getString("deleted"));
								}
								if (jsonObjEntry.has("totalComments")) {
									entryPojo.setTotalComments(jsonObjEntry.getString("totalComments"));
								}
								if (jsonObjEntry.has("totalviews")) {
									entryPojo.setTotalViews(jsonObjEntry.getString("totalviews"));
								}

								if (jsonObjEntry.has("videoThumb")) {
									entryPojo.setVideoThumb(jsonObjEntry.getString("videoThumb"));
								}

								if (jsonObjEntry.has("tags")) {
									JSONArray jsonArrayTags = jsonObjEntry.getJSONArray("tags");
									for (int j = 0; j < jsonArrayTags.length(); j++) {
										entryPojo.addTags(jsonArrayTags.getString(j));
									}
								}

								if (!jsonObjEntry.has("entryFiles")) {
									//									Log.v(Constant.TAG, "entryFiles not exist in ID " + entryPojo.getID());
								} else {
									JSONArray jsonArrayFiles = jsonObjEntry.getJSONArray("entryFiles");
									for (int j = 0; j < jsonArrayFiles.length(); j++) {
										JSONObject jsonObjFile = jsonArrayFiles.getJSONObject(j);

										if (entryPojo.getType().equalsIgnoreCase("image")) {
											entryPojo.setImageLink(jsonObjFile.getString("filePath"));
											entryPojo.setFiletype(jsonObjFile.getString("fileType"));
										} else if (entryPojo.getType().equalsIgnoreCase("audio")) {
											if (j == 0) {

												entryPojo.setAudioLink(jsonObjFile.getString("filePath"));
												entryPojo.setFiletype(jsonObjFile.getString("fileType"));
											} else if (j == 1) {

												entryPojo.setImageLink(jsonObjFile.getString("filePath"));
												entryPojo.setFiletype(jsonObjFile.getString("fileType"));
											}
										} else if (entryPojo.getType().equalsIgnoreCase("video")) {
											entryPojo.setVideoLink(jsonObjFile.getString("filePath"));
											entryPojo.setFiletype(jsonObjFile.getString("fileType"));
										}
									}

									arrEntryPojosParent.add(entryPojo);
								}
							}
						}


						if (arrEntryPojos != null && arrEntryPojos.size() > 0) {
							mFirstVisibleItem = arrEntryPojos.size() - 1;
							arrEntryPojos.addAll(arrEntryPojosParent);

						} else {
							mFirstVisibleItem = 0;
							arrEntryPojos.clear();
							arrEntryPojos.addAll(arrEntryPojosParent);
						}



					}

					if(jsonObject.has("next")){
						String next=jsonObject.getString("next");
						if(next.length()>0){
							isNextPageAvail=true;
						}
					}
					else{
						isNextPageAvail=false;
					}


					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerEntry.sendEmptyMessage(0);
					} else {
						handlerEntry.sendEmptyMessage(1);
					}

				} catch (Exception exception) {
					// TODO: handle exception
					exception.printStackTrace();
					handlerEntry.sendEmptyMessage(0);
				}
			}

		}
	}

	Handler handlerEntry = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			Utility.HideDialog(mContext);
			mFirstVisibleItem = 0;
			isPageLoaded=false;
			isPagination=false;
			//			isLoaded = true;
			if (msg.what == 1) {
				//				textNoData.setVisibility(View.GONE);
				if(!isDataLoaded){
					isDataLoaded = true;

					textFollowers.setVisibility(View.VISIBLE);
					textFollowers.setText(UserFan+" Followers");

					textUserName.setText(UserName);

					if(UserDisplayName!=null && UserDisplayName.length()>0){
						textUserDisplayName.setText(UserDisplayName);	
					}
					else {
						textUserDisplayName.setText(UserName);
					}

					if(UserTagline!=null && UserTagline.length()>0){
						textTagline.setVisibility(View.VISIBLE);
						textTagline.setText(Utility.unescape_perl_string(UserTagline));	
					}
					else {
						textTagline.setVisibility(View.GONE);
					}

					if (UserPic.equals(""))
					{
						imgUserPic.setImageResource(R.drawable.profile_pic_new);
					}
					else 
					{
						imgUserPic.setImageResource(R.drawable.profile_pic_new);

						Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
						.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUserPic);
					}

					if (UserCoverImage == null || UserCoverImage.equals("")) {
						imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
					} else {
						imgCoverPage.setBackgroundResource(R.drawable.cover_bg);

						Picasso.with(mContext).load(UserCoverImage).fit().centerCrop().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg).into(imgCoverPage);
					}

					if(IsMyStar!=null && !IsMyStar.equalsIgnoreCase("0")){
						imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
						imgFollow.setText(getString(R.string.following));
					}
					else {
						imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
						imgFollow.setText(getString(R.string.follow));
					}

					if(IAmStar!=null && IAmStar.length()>0 && IAmStar.equalsIgnoreCase("1")){
						Picasso.with(mContext).load(R.drawable.msg_act_btn).into(imgMsg);
					}
					else{
						Picasso.with(mContext).load(R.drawable.msg_btn).into(imgMsg);
					}





				}

				if (arrEntryPojos.size() == 0) {
					isDataNull=true;
					//					textNoData.setVisibility(View.VISIBLE);
					//					textNoData.setText(getString(R.string.there_are_no_entries_yet));
				}
				else {
					isDataNull=false;
					//					setListViewHeightBasedOnChildren(listEntry);
				}
				entryListAdapter.notifyDataSetChanged();
			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	class GetEntryCall extends Thread {

		String entryId;

		public GetEntryCall(String entryId) {
			// TODO Auto-generated constructor stub
			this.entryId = entryId;

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_ENTRY + entryId,preferences.getString("token", null));

			//			Log.v(Constant.TAG, "GetEntryCall Update Fragment response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if(jsonObject.has("starredBy")){
						arrFollowers=new ArrayList<StarPojo>();
						JSONArray jsonArrayStarredBy = jsonObject.getJSONArray("starredBy");
						for (int i = 0; i < jsonArrayStarredBy.length(); i++) {
							JSONObject jsonObj = jsonArrayStarredBy.getJSONObject(i);
							StarPojo star=new StarPojo();
							star.setStarID(jsonObj.getString("starId"));
							star.setStarName(jsonObj.getString("starName"));
							star.setStarredDate(jsonObj.getString("starredDate"));
							star.setProfileImage(jsonObj.getString("profileImage"));
							star.setProfileCover(jsonObj.getString("profileCover"));
							arrFollowers.add(star);
						}
					}

					if(jsonObject.has("fans")){
						UserFan=jsonObject.getString("fans");
					}

					if (jsonObject.has("entries")) {

						arrEntryPojosParent.clear();

						JSONArray jsonArrayEntries = jsonObject.getJSONArray("entries");

						for (int i = 0; i < jsonArrayEntries.length(); i++) {

							JSONObject jsonObj = jsonArrayEntries.getJSONObject(i);

							JSONObject jsonObjEntry = jsonObj.getJSONObject("entry");

							EntryPojo entryPojo = new EntryPojo();

							//							Log.d("mobstar","id=>"+jsonObjEntry.isNull("id"));
							if(jsonObjEntry.isNull("id")){
								break;
							}
							else {

								if (jsonObjEntry.has("user")) {
									JSONObject jsonObjUser = jsonObjEntry.getJSONObject("user");
									UserID=jsonObjUser.getString("id");
									UserPic=jsonObjUser.getString("profileImage");
									entryPojo.setProfileImage(UserPic);

									UserCoverImage=jsonObjUser.getString("profileCover");
									entryPojo.setProfileCover(UserCoverImage);

									UserBio=jsonObjUser.getString("bio");
									UserTagline=jsonObjUser.getString("tagLine");
									UserDisplayName=jsonObjUser.getString("displayName");
									UserName=jsonObjUser.getString("userName");
									UserFan=jsonObjUser.getString("fans");
								}
								entryPojo.setID(jsonObjEntry.getString("id"));

								String category=jsonObjEntry.getString("category");
								entryPojo.setCategory(category);
								if(category!=null && category.equalsIgnoreCase("Profile Content") || category.equalsIgnoreCase("MobIT") ){
									//									entryPojo.setIsVotedByYou(jsonObjEntry.getString("isVotedByYou"));
								}
								entryPojo.setType(jsonObjEntry.getString("type"));
								entryPojo.setName(jsonObjEntry.getString("name"));
								entryPojo.setDescription(jsonObjEntry.getString("description"));
								entryPojo.setCreated(jsonObjEntry.getString("created"));
								entryPojo.setModified(jsonObjEntry.getString("modified"));
								if (jsonObjEntry.has("upVotes")) {
									entryPojo.setUpVotesCount(jsonObjEntry.getString("upVotes"));
								}

								if (jsonObjEntry.has("downVotes")) {
									entryPojo.setDownvotesCount(jsonObjEntry.getString("downVotes"));
								}
								if (jsonObjEntry.has("rank")) {
									entryPojo.setRank(jsonObjEntry.getString("rank"));
								}
								if (jsonObjEntry.has("language")) {
									entryPojo.setLanguage(jsonObjEntry.getString("language"));
								}
								if (jsonObjEntry.has("deleted")) {
									entryPojo.setDeleted(jsonObjEntry.getString("deleted"));
								}
								if (jsonObjEntry.has("totalComments")) {
									entryPojo.setTotalComments(jsonObjEntry.getString("totalComments"));
								}
								if (jsonObjEntry.has("totalviews")) {
									entryPojo.setTotalViews(jsonObjEntry.getString("totalviews"));
								}

								if (jsonObjEntry.has("videoThumb")) {
									entryPojo.setVideoThumb(jsonObjEntry.getString("videoThumb"));
								}

								if (jsonObjEntry.has("tags")) {
									JSONArray jsonArrayTags = jsonObjEntry.getJSONArray("tags");
									for (int j = 0; j < jsonArrayTags.length(); j++) {
										entryPojo.addTags(jsonArrayTags.getString(j));
									}
								}

								if (!jsonObjEntry.has("entryFiles")) {
									//									Log.v(Constant.TAG, "entryFiles not exist in ID " + entryPojo.getID());
								} else {
									JSONArray jsonArrayFiles = jsonObjEntry.getJSONArray("entryFiles");
									for (int j = 0; j < jsonArrayFiles.length(); j++) {
										JSONObject jsonObjFile = jsonArrayFiles.getJSONObject(j);

										if (entryPojo.getType().equalsIgnoreCase("image")) {
											entryPojo.setImageLink(jsonObjFile.getString("filePath"));
											entryPojo.setFiletype(jsonObjFile.getString("fileType"));
										} else if (entryPojo.getType().equalsIgnoreCase("audio")) {
											if (j == 0) {

												entryPojo.setAudioLink(jsonObjFile.getString("filePath"));
												entryPojo.setFiletype(jsonObjFile.getString("fileType"));
											} else if (j == 1) {

												entryPojo.setImageLink(jsonObjFile.getString("filePath"));
												entryPojo.setFiletype(jsonObjFile.getString("fileType"));
											}
										} else if (entryPojo.getType().equalsIgnoreCase("video")) {
											entryPojo.setVideoLink(jsonObjFile.getString("filePath"));
											entryPojo.setFiletype(jsonObjFile.getString("fileType"));
										}
									}

									arrEntryPojosParent.add(entryPojo);
								}
							}
						}


						if (arrEntryPojos != null && arrEntryPojos.size() > 0) {
							mFirstVisibleItem = arrEntryPojos.size() - 1;
							arrEntryPojos.addAll(arrEntryPojosParent);

						} else {
							mFirstVisibleItem = 0;
							arrEntryPojos.clear();
							arrEntryPojos.addAll(arrEntryPojosParent);
						}

					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerGetEntry.sendEmptyMessage(0);
					} else {
						handlerGetEntry.sendEmptyMessage(1);
					}

				} catch (Exception exception) {
					// TODO: handle exception
					exception.printStackTrace();
					handlerGetEntry.sendEmptyMessage(0);
				}
			}

		}
	}

	Handler handlerGetEntry = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			isDataLoaded = true;
			Utility.HideDialog(mContext);
			mFirstVisibleItem = 0;
			//			isLoaded = true;
			if (msg.what == 1) {
				//				textNoData.setVisibility(View.GONE);
				textFollowers.setVisibility(View.VISIBLE);
				textFollowers.setText(UserFan+" Followers");
				if (arrEntryPojos.size() == 0) {
					isDataNull=true;
					//					textNoData.setVisibility(View.VISIBLE);
					//					textNoData.setText(getString(R.string.there_are_no_entries_yet));
				}
				else {
					isDataNull=false;
					textUserName.setText(UserName);

					if(UserDisplayName!=null && UserDisplayName.length()>0){
						textUserDisplayName.setText(UserDisplayName);	
					}
					else {
						textUserDisplayName.setText(UserName);
					}

					if(UserTagline!=null && UserTagline.length()>0){
						textTagline.setVisibility(View.VISIBLE);
						textTagline.setText(Utility.unescape_perl_string(UserTagline));	
					}
					else {
						textTagline.setVisibility(View.GONE);
					}

					if (UserPic.equals(""))
					{
						imgUserPic.setImageResource(R.drawable.profile_pic_new);
					}
					else 
					{
						imgUserPic.setImageResource(R.drawable.profile_pic_new);

						Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
						.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUserPic);
					}

					if (UserCoverImage == null || UserCoverImage.equals("")) {
						imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
					} else {
						imgCoverPage.setBackgroundResource(R.drawable.cover_bg);

						Picasso.with(mContext).load(UserCoverImage).fit().centerCrop().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg).into(imgCoverPage);
					}
					//					setListViewHeightBasedOnChildren(listEntry);
				}
				entryListAdapter.notifyDataSetChanged();
			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};

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

	class AddStarCall extends Thread {

		String UserID;

		public AddStarCall(String UserID) {
			this.UserID = UserID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "star" };
			String[] value = { UserID };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.STAR, name, value, preferences.getString("token", null));

			//			Log.v(Constant.TAG, "AddStarCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerAddStar.sendEmptyMessage(0);
					} else {
						for (int i = 0; i < arrEntryPojos.size(); i++) {
							if (arrEntryPojos.get(i).getUserID().equalsIgnoreCase(UserID)) {
								arrEntryPojos.get(i).setIsMyStar("1");
							}

						}
						handlerAddStar.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerAddStar.sendEmptyMessage(0);
				}

			} else {

				handlerAddStar.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerAddStar = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				entryListAdapter.notifyDataSetChanged();
				Intent intent = new Intent("star_added");
				intent.putExtra("UserID", UserID);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

				Intent searchIntent = new Intent("search_star_added");
				searchIntent.putExtra("UserID", UserID);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(searchIntent);
			} else {

			}
		}
	};



	public void onPause() {
		super.onPause();
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}

		isVideoSurfaceReady = false;
		isInPauseState = true;
		indexCurrentPlayAudio = -1;

		//		Log.v(Constant.TAG, "ProfileActivity onPause");
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);

		//		File folder = new File(Environment.getExternalStorageDirectory() + "/.mobstar");

		try {
			if(FILEPATH!=null && FILEPATH.length()>0){
				File folder = new File(FILEPATH);
				if(folder!=null && folder.exists()){
					if (folder.isDirectory()) {
						String[] children = folder.list();

						for (int i = 0; i < listDownloadingFile.size(); i++) {

							new File(folder, listDownloadingFile.get(i)).delete();
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	ActionListener actionListener = new ActionListener() {

		@Override
		public void onComplete() {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (btnEdit.equals(view)) {
			Intent intent = new Intent(mContext, EditProfileActivity.class);
			intent.putExtra("UserID", UserID);
			intent.putExtra("UserName", UserName);
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		//		else if(textUpdates.equals(view)){
		//			if(!isDefault){
		//				isDefault=true;
		//				textUpdates.setBackgroundColor(getResources().getColor(R.color.splash_bg));
		//				textProfile.setBackgroundColor(getResources().getColor(R.color.gray_color));
		//					removeProfileView();
		//				//				UpdatesFragment updatesFragment = new UpdatesFragment();
		//				//				Bundle extras = new Bundle();
		//				//				extras.putString("UserID",UserID);
		//				//				updatesFragment.setArguments(extras);
		//				//				replaceFragment(updatesFragment, "UpdatesFragment");
		//			}
		//		}
		//		else if(textProfile.equals(view)){
		//			if(isDefault){
		//				isDefault=false;
		//				textUpdates.setBackgroundColor(getResources().getColor(R.color.gray_color));
		//				textProfile.setBackgroundColor(getResources().getColor(R.color.splash_bg));
		//
		//				loadProfileTab();
		//				//				ProfileFragment profileFragment = new ProfileFragment();
		//				//				Bundle extras = new Bundle();
		//				//				extras.putString("UserID",UserID);
		//				//				extras.putString("UserBio", UserBio);
		//				//				extras.putString("UserPic",UserPic);
		//				//				profileFragment.setArguments(extras);
		//				//				replaceFragment(profileFragment, "profileFragment");
		//			}
		//
		//		}
		else if(textFollowers.equals(view)){
			Intent i=new Intent(ProfileActivity.this,FansActivity.class);
			i.putExtra("UserId",UserID);
			startActivity(i);
		}

	}


	//	class GetProfileCall extends Thread {
	//
	//		String UserID;
	//
	//		public GetProfileCall(String UserID) {
	//			this.UserID = UserID;
	//		}
	//
	//		@Override
	//		public void run() {
	//			// TODO Auto-generated method stub
	//
	//			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_PROFILE + UserID, preferences.getString("token", null));
	//
	//			Log.v(Constant.TAG, "GetProfile response " + response);
	//
	//			if (response != null) {
	//
	//				String ErrorMessage = "";
	//
	//				try {
	//
	//					JSONObject jsonObject = new JSONObject(response);
	//
	//					if (jsonObject.has("error")) {
	//						ErrorMessage = jsonObject.getString("error");
	//					}
	//
	//					if (jsonObject.has("users")) {
	//
	//						JSONArray jsonArrayUser = jsonObject.getJSONArray("users");
	//
	//						if (jsonArrayUser.length() > 0) {
	//
	//							JSONObject jsonObj = jsonArrayUser.getJSONObject(0);
	//
	//							if (jsonObj.has("user")) {
	//								JSONObject jsonObjUser = jsonObj.getJSONObject("user");
	//
	//								// entryPojo.setUserID(jsonObjUser.getString("id"));
	//								// entryPojo.setUserName(jsonObjUser.getString("userName"));
	//								// entryPojo.setUserDisplayName(jsonObjUser.getString("displayName"));
	//								// entryPojo.setProfileImage(jsonObjUser.getString("profileImage"));
	//								// entryPojo.setProfileCover(jsonObjUser.getString("profileCover"));
	//								// entryPojo.setTagline(jsonObjUser.getString("tagLine"));
	//								//								UserName = jsonObjUser.getString("userName");
	//								//								UserDisplayName = jsonObjUser.getString("displayName");
	//								if(jsonObjUser.has("isMyStar")){
	//									IsMyStar=jsonObjUser.getString("isMyStar");
	//								}
	//								if(UserCoverImage.equalsIgnoreCase("")){
	//									UserCoverImage = jsonObjUser.getString("profileCover");
	//									runOnUiThread(new Runnable() {
	//										public void run() {
	//											if(UserCoverImage.equalsIgnoreCase("")){
	//												imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
	//											}
	//											else {
	//												Picasso.with(mContext).load(UserCoverImage).fit().centerCrop().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg).into(imgCoverPage);	
	//											}
	//										}
	//									});
	//								}
	//
	//								UserTagline = jsonObjUser.getString("tagLine");
	//								//								UserPic = jsonObjUser.getString("profileImage");
	//								UserBio=jsonObjUser.getString("bio");
	//								Log.d("mobstar","BIO"+jsonObjUser.getString("bio"));
	//
	//							}
	//						}
	//					}
	//
	//					if (ErrorMessage != null && !ErrorMessage.equals("")) {
	//						Log.d("mobstar", "errorMessage not null");
	//						handlerProfile.sendEmptyMessage(0);
	//					} else {
	//						Log.d("mobstar", "errorMessage null");
	//						handlerProfile.sendEmptyMessage(1);
	//					}
	//
	//				} catch (Exception exception) {
	//					// TODO: handle exception
	//					exception.printStackTrace();
	//					handlerProfile.sendEmptyMessage(0);
	//				}
	//
	//			} else {
	//				handlerProfile.sendEmptyMessage(0);
	//			}
	//
	//		}
	//	}
	//
	//	Handler handlerProfile = new Handler() {
	//
	//		@Override
	//		public void handleMessage(Message msg) {
	//			// TODO Auto-generated method stub
	//			Log.d("mobstar", "Handle Profile");
	//			Utility.HideDialog(mContext);
	//
	//			if (msg.what == 1) {
	//				//				if (UserCoverImage.equals("")) {
	//				//					imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
	//				//				} else {
	//				//					imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
	//				//
	//				//					Picasso.with(mContext).load(UserCoverImage).fit().centerCrop().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg).into(imgCoverPage);
	//				//				}
	//				//
	//				//				if (UserPic.equals("")) {
	//				//					imgUserPic.setImageResource(R.drawable.profile_pic_new);
	//				//				} else {
	//				//					imgUserPic.setImageResource(R.drawable.profile_pic_new);
	//				//
	//				//					Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
	//				//					.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUserPic);
	//				//				}
	//				//
	//				//				textUserName.setText(UserName);
	//				//				textUserDisplayName.setText(UserDisplayName);
	//				if (UserID.equals(preferences.getString("userid", "0"))) {
	//					btnEdit.setVisibility(View.VISIBLE);
	//					imgFollow.setVisibility(View.INVISIBLE);
	//				} else if (IsMyStar.equalsIgnoreCase("1")) {
	//					btnEdit.setVisibility(View.GONE);
	//					imgFollow.setImageResource(R.drawable.btn_following);
	//					imgFollow.setVisibility(View.VISIBLE);
	//				} else {
	//					btnEdit.setVisibility(View.GONE);
	//					imgFollow.setImageResource(R.drawable.btn_follow_yellow);
	//					imgFollow.setVisibility(View.VISIBLE);
	//				}
	//				if(UserTagline!=null && UserTagline.length()>0){
	//					textTagline.setVisibility(View.VISIBLE);
	//					textTagline.setText(StringEscapeUtils.unescapeJava(UserTagline));
	//				}
	//				else {
	//					textTagline.setVisibility(View.GONE);
	//				}
	//
	//
	//				//				UpdatesFragment updatesFragment = new UpdatesFragment();
	//				//				Bundle extras = new Bundle();
	//				//				extras.putString("UserID",UserID);
	//				//				updatesFragment.setArguments(extras);
	//				//				replaceFragment(updatesFragment, "UpdatesFragment");
	//
	//				//add dynamically layout header height
	//				//				ViewGroup.LayoutParams params = topTransparent.getLayoutParams();
	//				//				params.height = llHeader.getHeight()+150;
	//				//				topTransparent.setLayoutParams(params);
	//				//				topTransparent.requestLayout();
	//
	////				GetData(UserID);
	//
	//			} else {
	//
	//			}
	//		}
	//	};


	void loadProfileTab(){
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}

		isVideoSurfaceReady = false;
		isInPauseState = true;
		indexCurrentPlayAudio = -1;


		//		listEntry.removeAllViews();
		//		entryListAdapter.notifyDataSetChanged();
		//		
		//		
		//		listEntry.addHeaderView(header);
		//		listEntry.setAdapter(null);
		//		listEntry.setVisibility(View.GONE);
		//		if(textNoData.getVisibility()== View.VISIBLE){
		//			textNoData.setVisibility(View.GONE);
		//		}

		//		if(llProfile.getVisibility()== View.GONE){
		//			llProfile.setVisibility(View.VISIBLE);
		//		}
		LayoutInflater inflater = getLayoutInflater();
		bottom = (ViewGroup)inflater.inflate(R.layout.fragment_profile, listEntry, false);
		ImageView imgUser=(ImageView)bottom.findViewById(R.id.imgUser);
		TextView textBio=(TextView)bottom.findViewById(R.id.textBio);
		listEntry.addFooterView(bottom, null, false);

		if (UserPic.equals("")) {
			imgUser.setImageResource(R.drawable.profile_pic_new);
		} else {
			imgUser.setImageResource(R.drawable.profile_pic_new);

			Picasso.with(mContext).load(UserPic).into(imgUser);

			//			Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
			//			.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUser);
		}


		if(UserBio!=null && UserBio.length()>0){
			textBio.setText(Utility.unescape_perl_string(UserBio));
			textBio.setVisibility(View.VISIBLE);
		}
		else {
			textBio.setVisibility(View.GONE);
		}

		entryListAdapter.notifyDataSetChanged();
		listEntry.invalidate();
	}

	//	void removeProfileView(){
	//		//		if(llProfile.getVisibility()== View.VISIBLE){
	//		//			llProfile.setVisibility(View.GONE);
	//		//		}
	//		//		listEntry.setVisibility(View.VISIBLE);
	//
	//		if(bottom!=null){
	//			listEntry.removeFooterView(bottom);
	//		}
	//		listEntry.setAdapter(entryListAdapter);
	//		isInPauseState = false;
	//		// Log.v(Constant.TAG, "VideoListFragment onResume");
	//		if (entryListAdapter != null) {
	//			entryListAdapter.notifyDataSetChanged();
	//		}
	//
	//		if (arrEntryPojos.size() == 0) {
	////			textNoData.setVisibility(View.VISIBLE);
	////			textNoData.setText(getString(R.string.there_are_no_entries_yet));
	//			isDataNull=true;
	//			arrEntryPojos.add(null);
	//		}
	//		else {
	////			textNoData.setVisibility(View.GONE);
	//			isDataNull=false;
	//		}
	//	}


	@Override
	public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
		header.setAlpha(1);

	}


	@Override
	public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {
		if (fadeHeader && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));

		}
	}


	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}




	/*public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null) {
	        return;
	    }
	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0) {
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
	        }
	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}*/



	class DeleteStarCall extends Thread {

		String UserID;

		public DeleteStarCall(String UserID) {
			this.UserID = UserID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "star" };
			String[] value = { UserID };

			String response = JSONParser.deleteRequest(Constant.SERVER_URL + Constant.DELETE_STAR + UserID, name, value, preferences.getString("token", null));

			//			Log.v(Constant.TAG, "DeleteStarCall response " + response + " UserID " + UserID);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerDeleteStar.sendEmptyMessage(0);
					} else {
						handlerDeleteStar.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerDeleteStar.sendEmptyMessage(0);
				}

			} else {

				handlerDeleteStar.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerDeleteStar = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			if (msg.what == 1) {
				Intent intent = new Intent("star_removed");
				intent.putExtra("UserID",UserID);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

				Intent searchIntent = new Intent("search_star_removed");
				searchIntent.putExtra("UserID", UserID);
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(searchIntent);

				IsMyStar="0";
				imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
				imgFollow.setText(getString(R.string.follow));
				imgFollow.setVisibility(View.VISIBLE);


			} else {

			}
		}
	};

	public void LikeDialog(){
		listEntry.setFocusable(false);
		Likedialog.setContentView(R.layout.dialog_like);
		Likedialog.show();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Likedialog.dismiss();
			}
		};
		timer.schedule(task, 1000);
	}

	public void DisLikeDialog(){
		listEntry.setFocusable(false);
		disLikedialog.setContentView(R.layout.dialog_dislike);
		disLikedialog.show();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				disLikedialog.dismiss();
			}
		};
		timer.schedule(task, 1000);
	}

	@Override
	public void onBackPressed() {
		setResult(101);
		finish();
	}




	public class EndlessScrollListener implements OnScrollListener 
	{
		private int visibleThreshold = 1;
		public EndlessScrollListener() {}
		public EndlessScrollListener(int visibleThreshold) 
		{
			this.visibleThreshold = visibleThreshold;
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) 
		{
			if (loading) 
			{
				if (totalItemCount >previousTotal) 
				{
					loading = false;
					previousTotal = totalItemCount;
				}
				else if(previousTotal>totalItemCount)
				{
					visibleThreshold=1;
					previousTotal=0;
					loading=true;
				}
				if(totalItemCount==visibleItemCount)
				{
					loading=true;
				}
			}
			if ((!loading) && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) 
			{
				if (isNextPageAvail) 
				{
					Log.i(" PAGINATION ", "CONTINUE CONTINUE CONTINUE");
					loading = true;
					pageNum++;
					handler.post(new Runnable() 
					{
						@Override
						public void run() 
						{
							if(Utility.isNetworkAvailable(mContext)){
								Log.d("mobstar","api call.. on scroll");
								isPagination=true;
								Utility.ShowProgressDialog(mContext, "Loading ");
								new EntryCall(UserID,pageNum).start();
							}
							else {

								Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
								Utility.HideDialog(mContext);
							}
						}
					});
				}
				else
				{
					Log.i("IS PAGINATION FALSE","" );
				}
			}
		}
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) 
		{
			//final int scrollState=State;
			//			final AbsListView view=v;

			if(view!=null && !isPagination){
				isScrolling = true;
				// Log.i(Constant.TAG, "scrolling..." + isScrolling +
				// " scrollState " + scrollState);
				if (scrollState == 0 && !disLikedialog.isShowing() && !Likedialog.isShowing()) {

					int previousFirstVisibleItem = mFirstVisibleItem;
					//					Log.d("mobstar","priviousvisible"+previousFirstVisibleItem);

					isVideoSurfaceReady = false;

					isScrolling = false;

					//					Log.i(Constant.TAG, "***scrolling stopped..." + isScrolling);

					mFirstVisibleItem = listEntry.getFirstVisiblePosition();

					if (listEntry.getChildAt(0) != null) {
						int height = view.getChildAt(0).getHeight();
						int d=(height/2)+ dpToPx(30);

						//						Log.d("mobstar", "top"+view.getChildAt(0).getTop()+" < "+d);
						////						int d=height/2;
						//						Log.d("mobstar","height"+d);


						if (mFirstVisibleItem != 0) {
							mFirstVisibleItem--;
						}
						//						if (view.getChildAt(0).getTop() < -((height / 2)))
						if (view.getChildAt(0).getTop() < - ((height / 2)) + dpToPx(30)){
							if (mFirstVisibleItem  < arrEntryPojos.size() - 2 )
								mFirstVisibleItem++;

						}
					}

					if (previousFirstVisibleItem != mFirstVisibleItem) {
						indexCurrentPlayAudio = -1;
					}

					//					Log.v(Constant.TAG, "*********mFirstVisibleItem " + mFirstVisibleItem);

					entryListAdapter.notifyDataSetChanged();

				}}


		}
	}

}