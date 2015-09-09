package com.mobstar.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mobstar.ProfileActivity;
import com.mobstar.R;
import com.mobstar.blog.BlogFragment;
import com.mobstar.fanconnect.FanConnectHomeFragment;
import com.mobstar.help.HelpFragment;
import com.mobstar.inbox.InboxFragment;
import com.mobstar.login.LoginSocialActivity;
import com.mobstar.service.NotificationService;
import com.mobstar.settings.SettingsFragment;
import com.mobstar.talentconnect.TalentConnectHomeFragment;
import com.mobstar.upload.SelectUploadTypeActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;

public class HomeActivity extends ActionBarActivity implements OnClickListener, DrawerListener {

	ActionBar mActionBar;
	Context mContext;

	ScrollView scrollLeftDrawer;
	private DrawerLayout mDrawerLayout;
	ImageView btnMenu, btnUpload, btnNotification, btnSearch;
	ImageView imgHeaderLogo;

	boolean isMenuOpen = false;

	SharedPreferences preferences;
	ImageView imgUserPic,imgBlogDivider;
	TextView textUserDisplayName, textUserFullName;
	String sDisplayName, sFullName, sUserId;
	String UserProfileImage;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	HomeFragment homeFragment;

	ImageView imgIamTalent, imgIamFan, imgMentorsDivider;

	LinearLayout llMentors, llSettings, llHelp, llTalentConnect, llFanConnect, llPoints, llLogout, llInbox,llBlog;

	int NotificationCount = 0,MessageCount = 0;

	private TextView textNotificationCount,textInboxCount;
	private String deepLinkedId="";
	private Intent intentNotificationService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mContext = HomeActivity.this;

		Bundle b=getIntent().getExtras();
		if(b!=null) {
			if(b.containsKey("deepLinkedId")){
				deepLinkedId=b.getString("deepLinkedId");
			}
		}
		
		
		preferences = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
		
		SharedPreferences prefs = mContext.getSharedPreferences("rate_app", 0);
		SharedPreferences.Editor editor = prefs.edit();
		//Add to launch Counter
		long launch_count = prefs.getLong("launch_count", 0) +1;
		editor.putLong("launch_count", launch_count).commit();
		
		sDisplayName = preferences.getString("displayName", "");
		sFullName = preferences.getString("fullName", "");
		sUserId = preferences.getString("userid", "");
		UserProfileImage = preferences.getString("profile_image", "");

		//		Log.v(Constant.TAG, "Token " + preferences.getString("token", null));

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		final View customView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
		mActionBar.setCustomView(customView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayShowCustomEnabled(true);
		Toolbar parent =(Toolbar) customView.getParent();
		parent.setContentInsetsAbsolute(0, 0);


		mFragmentManager = getSupportFragmentManager();

		// Ion.getDefault(mContext).configure().setLogging("Ion", Log.DEBUG);

		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + mContext.getPackageName() +"/";

		//		File folder = new File(Environment.getExternalStorageDirectory() + "/.mobstar");

		File folder = new File(path);

		if (!folder.exists()) {
			folder.mkdir();
		}

		InitControls();

		if(deepLinkedId!=null && deepLinkedId.length()>0){
			homeFragment = new HomeFragment();
			Bundle extras = new Bundle();
			extras.putString("deepLinkedId", deepLinkedId);
			homeFragment.setArguments(extras);
			replaceFragment(homeFragment, "Home Fragment");
		}
		else {
			homeFragment = new HomeFragment();
			replaceFragment(homeFragment, "Home Fragment");
		}

		if (Utility.isNetworkAvailable(mContext)) {
			new NotificationCountCall().start();
		}

		intentNotificationService = new Intent(this, NotificationService.class);
		startService(intentNotificationService);
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("profile_image_changed"));

		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("notification_count_changed"));

		registerReceiver(mReceiver, new IntentFilter("notification_count_update_from_service"));
		startHowToVoteActivity();
	}

	private void startHowToVoteActivity(){
		final SharedPreferences preferences = getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);
		final boolean isShowingHowToVote = preferences.getBoolean(HowToVoteActivity.HOW_TO_VOTE, true);
		if (isShowingHowToVote){
			final Intent intent = new Intent(this, HowToVoteActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent

			if(intent.getAction().equalsIgnoreCase("notification_count_update_from_service")){
				Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
				if (!(f instanceof NotificationsFragment)) {
					int count = intent.getExtras().getInt("notification_count");
					if (count > 0) {
						textNotificationCount.setText(count+"");
						textNotificationCount.setVisibility(View.VISIBLE);
					} else {
						textNotificationCount.setVisibility(View.GONE);
					}
				}

			}
			else if (intent.getAction().equalsIgnoreCase("notification_count_changed")) {
				closeMenu();
			} else {
				UserProfileImage = preferences.getString("profile_image", "");

				if (UserProfileImage.equals("")) {
					imgUserPic.setImageResource(R.drawable.ic_pic_small);
				} else {
					imgUserPic.setImageResource(R.drawable.ic_pic_small);
					Picasso.with(mContext).load(UserProfileImage).resize(Utility.dpToPx(mContext, 60), Utility.dpToPx(mContext, 60)).centerCrop()
					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(imgUserPic);
				}

				sDisplayName = preferences.getString("displayName", "");
				textUserDisplayName.setText(sDisplayName);

				sFullName = preferences.getString("fullName", "");
				textUserFullName.setText(sFullName);
			}

		}
	};

	void InitControls() {

		imgUserPic = (ImageView) findViewById(R.id.imgUserPic);
		imgUserPic.setOnClickListener(this);

		if (UserProfileImage.equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso.with(mContext).load(UserProfileImage).resize(Utility.dpToPx(mContext, 60), Utility.dpToPx(mContext, 60)).centerCrop().placeholder(R.drawable.ic_pic_small)
			.error(R.drawable.ic_pic_small).into(imgUserPic);

			// Ion.with(mContext).load(UserProfileImage).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small)
			// .resize(Utility.dpToPx(mContext, 60), Utility.dpToPx(mContext,
			// 60)).centerCrop().asBitmap().setCallback(new
			// FutureCallback<Bitmap>() {
			//
			// @Override
			// public void onCompleted(Exception exception, Bitmap bitmap) {
			// // TODO Auto-generated method stub
			// if (exception == null) {
			// imgUserPic.setImageBitmap(bitmap);
			// }
			// }
			// });
		}

		textUserDisplayName = (TextView) findViewById(R.id.textUserDisplayName);
		textUserDisplayName.setText(sDisplayName);
		textUserDisplayName.setOnClickListener(this);

		textUserFullName = (TextView) findViewById(R.id.textUserFullName);
		textUserFullName.setText(sFullName);
		textUserFullName.setOnClickListener(this);

		btnMenu = (ImageView) mActionBar.getCustomView().findViewById(R.id.btnMenu);
		btnMenu.setOnClickListener(this);

		imgHeaderLogo = (ImageView) mActionBar.getCustomView().findViewById(R.id.imgHeaderLogo);
		imgHeaderLogo.setOnClickListener(this);

		btnNotification = (ImageView) mActionBar.getCustomView().findViewById(R.id.btnNotification);
		btnNotification.setOnClickListener(this);

		textNotificationCount = (TextView) mActionBar.getCustomView().findViewById(R.id.textNotificationCount);
		textNotificationCount.setVisibility(View.GONE);

		btnUpload = (ImageView) mActionBar.getCustomView().findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(this);

		btnSearch = (ImageView) mActionBar.getCustomView().findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerListener(this);

		scrollLeftDrawer = (ScrollView) findViewById(R.id.scrollLeftDrawer);
		scrollLeftDrawer.setOnClickListener(this);

		llTalentConnect = (LinearLayout) findViewById(R.id.llTalentConnect);
		llTalentConnect.setOnClickListener(this);

		llPoints = (LinearLayout) findViewById(R.id.llPoints);
		llPoints.setOnClickListener(this);

		llLogout = (LinearLayout) findViewById(R.id.llLogout);
		llLogout.setOnClickListener(this);

		llInbox = (LinearLayout) findViewById(R.id.llInbox);
		llInbox.setOnClickListener(this);
		textInboxCount=(TextView)findViewById(R.id.textInboxCount);
		textInboxCount.setVisibility(View.GONE);

		llFanConnect = (LinearLayout) findViewById(R.id.llFanConnect);
		llFanConnect.setOnClickListener(this);

		llMentors = (LinearLayout) findViewById(R.id.llMentors);

		imgIamFan = (ImageView) findViewById(R.id.imgIamFan);
		imgIamFan.setImageResource(R.drawable.side_iam_fan_act);
		imgIamFan.setOnClickListener(this);

		imgIamTalent = (ImageView) findViewById(R.id.imgIamTalent);
		imgIamTalent.setImageResource(R.drawable.side_iam_talent);
		imgIamTalent.setOnClickListener(this);

		llMentors = (LinearLayout) findViewById(R.id.llMentors);
		llMentors.setOnClickListener(this);

		imgMentorsDivider = (ImageView) findViewById(R.id.imgMentorsDivider);

		llBlog= (LinearLayout) findViewById(R.id.llBlog);
		imgBlogDivider=(ImageView)findViewById(R.id.imgBlogDivider);
		llBlog.setOnClickListener(this);

		llSettings = (LinearLayout) findViewById(R.id.llSettings);
		llSettings.setOnClickListener(this);

		llHelp = (LinearLayout) findViewById(R.id.llHelp);
		llHelp.setOnClickListener(this);

	}

	private void replaceFragment(Fragment mFragment, String fragmentName) {

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.frag_content, mFragment, fragmentName);
		mFragmentTransaction.commitAllowingStateLoss();
	}

	private void replaceFragment(Fragment mFragment, String fragmentName, String tag) {
		if (!isFinishing()) {
			mFragmentTransaction = mFragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
			mFragmentTransaction.replace(R.id.frag_content, mFragment, fragmentName);
			mFragmentTransaction.addToBackStack(tag);
			mFragmentTransaction.commitAllowingStateLoss();
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		if (view.equals(btnMenu)) {
			if (isMenuOpen) {
				closeMenu();
			} else {
				openMenu();
			}

		} else if (btnUpload.equals(view)) {
			Intent intent = new Intent(mContext, SelectUploadTypeActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			closeMenu();

		} else if (view.equals(btnSearch)) {
			Log.d("mobstar","open search screen");
			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof SearchFragment)) {
				SearchFragment searchFragment = new SearchFragment();
				replaceFragment(searchFragment, "SearchFragment", "SearchFragment");
			}
			closeMenu();

		} else if (imgHeaderLogo.equals(view)) {

			homeFragment = new HomeFragment();
			replaceFragment(homeFragment, "Home Fragment");

			closeMenu();

		} else if (btnNotification.equals(view)) {
			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof NotificationsFragment)) {
				NotificationsFragment notificationsFragment = new NotificationsFragment();
				replaceFragment(notificationsFragment, "NotificationsFragment", "NotificationsFragment");
			}
			closeMenu();

		} else if (llMentors.equals(view)) {
			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof MentorsFragment)) {

				MentorsFragment mentorsFragment = new MentorsFragment();
				replaceFragment(mentorsFragment, "MentorsFragment", "MentorsFragment");
			}
			closeMenu();
		}
		// else if (llEntriesFeed.equals(view)) {
		//
		// if (!sActiveFragment.equals("HomeFrgament")) {
		//
		// sActiveFragment = "HomeFrgament";
		//
		// homeFragment = new HomeFragment();
		// replaceFragment(homeFragment, "Home Fragment");
		// }
		// closeMenu();
		// }
		else if (llSettings.equals(view)) {

			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof SettingsFragment)) {
				SettingsFragment settingsFragment = new SettingsFragment();
				replaceFragment(settingsFragment, "SettingsFragment", "SettingsFragment");
			}
			closeMenu();

		} else if (imgIamFan.equals(view)) {
			imgIamFan.setImageResource(R.drawable.side_iam_fan_act);
			imgIamFan.setBackgroundColor(getResources().getColor(R.color.side_act_bg));
			llFanConnect.setVisibility(View.GONE);
			llTalentConnect.setVisibility(View.VISIBLE);
			imgIamTalent.setBackgroundColor(getResources().getColor(R.color.splash_bg));
			imgIamTalent.setImageResource(R.drawable.side_iam_talent);
			llMentors.setVisibility(View.GONE);
			imgMentorsDivider.setVisibility(View.GONE);
			llBlog.setVisibility(View.GONE);
			imgBlogDivider.setVisibility(View.GONE);
		} else if (imgIamTalent.equals(view)) {
			imgIamTalent.setImageResource(R.drawable.side_iam_talent_act);
			imgIamTalent.setBackgroundColor(getResources().getColor(R.color.side_act_bg));
			llTalentConnect.setVisibility(View.GONE);
			llFanConnect.setVisibility(View.VISIBLE);
			llMentors.setVisibility(View.VISIBLE);
			imgMentorsDivider.setVisibility(View.VISIBLE);
			imgIamFan.setBackgroundColor(getResources().getColor(R.color.splash_bg));
			imgIamFan.setImageResource(R.drawable.side_iam_fan);
			llBlog.setVisibility(View.VISIBLE);
			imgBlogDivider.setVisibility(View.VISIBLE);
		}

		// else if (llWinners.equals(view)) {
		// if (!sActiveFragment.equals("WinnerFragment")) {
		//
		// sActiveFragment = "WinnerFragment";
		//
		// WinnerFragment winnerFragment = new WinnerFragment();
		// replaceFragment(winnerFragment, "WinnerFragment", "WinnerFragment");
		// }
		// mDrawerLayout.closeDrawer(layoutLeftDrawer);
		// isMenuOpen = false;
		// }

		else if (llHelp.equals(view)) {
			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof HelpFragment)) {
				HelpFragment helpFragment = new HelpFragment();
				replaceFragment(helpFragment, "HelpFragment", "HelpFragment");
			}
			closeMenu();
		} else if (llTalentConnect.equals(view)) {
			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof TalentConnectHomeFragment)) {
				TalentConnectHomeFragment talentConnectHomeFragment = new TalentConnectHomeFragment();
				replaceFragment(talentConnectHomeFragment, "TalentConnectFragment", "TalentConnectFragment");
			}
			closeMenu();
		} else if (llFanConnect.equals(view)) {
			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof FanConnectHomeFragment)) {

				FanConnectHomeFragment fanConnectHomeFragment = new FanConnectHomeFragment();
				replaceFragment(fanConnectHomeFragment, "FanConnectHomeFragment", "FanConnectHomeFragment");
			}
			closeMenu();
		}else if(llBlog.equals(view)){
			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof BlogFragment)) {

				BlogFragment blogFragment = new BlogFragment();
				replaceFragment(blogFragment, "BlogFragment", "BlogFragment");
			}
			closeMenu();


		}else if (llPoints.equals(view)) {
			// Fragment f =
			// mFragmentManager.findFragmentById(R.id.frag_content);
			// if (!(f instanceof PointsFragment)) {
			//
			// PointsFragment pointsFragment = new PointsFragment();
			// replaceFragment(pointsFragment, "PointsFragment",
			// "PointsFragment");
			// }
			// closeMenu();

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

			// set title
			alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

			// set dialog message
			alertDialogBuilder.setMessage(getString(R.string.coming_soon)).setCancelable(false).setNeutralButton("OK", null);

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

		} else if (llLogout.equals(view)) {
			if (Utility.isNetworkAvailable(mContext)) {
				new LogoutCall(Utility.getRegistrationId(mContext)).start();
			}
			
			SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
			pref.edit().putBoolean("isLogin", false).commit();
			Intent intent = new Intent(mContext, LoginSocialActivity.class);
			startActivity(intent);
			finish();
		} else if (llInbox.equals(view)) {

			Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
			if (!(f instanceof InboxFragment)) {
				InboxFragment talentConnectHomeFragment = new InboxFragment();
				replaceFragment(talentConnectHomeFragment, "InboxFragment", "InboxFragment");
			}
			closeMenu();
			//			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
			//
			//			// set title
			//			alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
			//
			//			// set dialog message
			//			alertDialogBuilder.setMessage(getString(R.string.coming_soon)).setCancelable(false).setNeutralButton("OK", null);
			//
			//			// create alert dialog
			//			AlertDialog alertDialog = alertDialogBuilder.create();
			//
			//			// show it
			//			alertDialog.show();
		}
		// else if (llMessages.equals(view)) {
		// if (!sActiveFragment.equals("MessagesFragment")) {
		//
		// sActiveFragment = "MessagesFragment";
		//
		// MessagesFragment messagesFragment = new MessagesFragment();
		// replaceFragment(messagesFragment, "MessagesFragment",
		// "MessagesFragment");
		// }
		// mDrawerLayout.closeDrawer(layoutLeftDrawer);
		// isMenuOpen = false;
		// }
		else if (imgUserPic.equals(view)) {
			Intent intent = new Intent(mContext, ProfileActivity.class);
			intent.putExtra("UserID", preferences.getString("userid", ""));
			intent.putExtra("UserName", preferences.getString("username", ""));
			intent.putExtra("UserPic", preferences.getString("profile_image", ""));
			intent.putExtra("UserDisplayName", preferences.getString("displayName", ""));
			intent.putExtra("UserCoverImage", preferences.getString("cover_image", ""));
			intent.putExtra("UserTagline", preferences.getString("tagline", ""));
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else if (textUserDisplayName.equals(view)) {
			Intent intent = new Intent(mContext, ProfileActivity.class);
			intent.putExtra("UserID", preferences.getString("userid", ""));
			intent.putExtra("UserName", preferences.getString("username", ""));
			intent.putExtra("UserDisplayName", preferences.getString("displayName", ""));
			intent.putExtra("UserCoverImage", preferences.getString("cover_image", ""));
			intent.putExtra("UserPic", preferences.getString("profile_image", ""));
			intent.putExtra("UserTagline", preferences.getString("tagline", ""));
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else if (textUserFullName.equals(view)) {
			Intent intent = new Intent(mContext, ProfileActivity.class);
			intent.putExtra("UserID", preferences.getString("userid", ""));
			intent.putExtra("UserName", preferences.getString("username", ""));
			intent.putExtra("UserDisplayName", preferences.getString("displayName", ""));
			intent.putExtra("UserPic", preferences.getString("profile_image", ""));
			intent.putExtra("UserCoverImage", preferences.getString("cover_image", ""));
			intent.putExtra("UserTagline", preferences.getString("tagline", ""));
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}

	private void closeMenu() {

		mDrawerLayout.closeDrawer(scrollLeftDrawer);
		isMenuOpen = false;
		btnMenu.setImageResource(R.drawable.icon_menu);
		//		btnMenu.setBackgroundColor(getResources().getColor(R.color.side_menu_bg_normal));
		btnMenu.setBackgroundColor(0);
		if (Utility.isNetworkAvailable(mContext)) {
			new NotificationCountCall().start();
		}
	}

	private void openMenu() {
		mDrawerLayout.openDrawer(scrollLeftDrawer);
		isMenuOpen = true;
		btnMenu.setImageResource(R.drawable.icon_menu_act);
		btnMenu.setBackgroundColor(getResources().getColor(R.color.splash_bg));

		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}


	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LocalBroadcastManager.getInstance(HomeActivity.this).unregisterReceiver(mReceiver);
		unregisterReceiver(mReceiver);
		stopService(intentNotificationService); 
	}

	@Override
	public void onDrawerClosed(View arg0) {
		isMenuOpen = false;
		btnMenu.setImageResource(R.drawable.icon_menu);
		//		btnMenu.setBackgroundColor(getResources().getColor(R.color.side_menu_bg_normal));
		btnMenu.setBackgroundColor(0);
	}

	@Override
	public void onDrawerOpened(View arg0) {
		isMenuOpen = true;
		btnMenu.setImageResource(R.drawable.icon_menu_act);
		btnMenu.setBackgroundColor(getResources().getColor(R.color.splash_bg));
		if (Utility.isNetworkAvailable(mContext)) {
			new InboxMessageCountCall().start();
		}
	}

	@Override
	public void onDrawerSlide(View arg0, float arg1) {

	}

	@Override
	public void onDrawerStateChanged(int arg0) {

	}

	class NotificationCountCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_NOTIFICATION_COUNT, preferences.getString("token", null));

			//			Log.v(Constant.TAG, "GET_NOTIFICATION_COUNT response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					NotificationCount = jsonObject.getInt("notifications");

					handlerNotification.sendEmptyMessage(1);

				} catch (Exception e) {
					e.printStackTrace();
					handlerNotification.sendEmptyMessage(0);
				}
			} else {

				handlerNotification.sendEmptyMessage(0);
			}
		}
	}

	Handler handlerNotification = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (NotificationCount > 0) {
				textNotificationCount.setText(NotificationCount + "");
				textNotificationCount.setVisibility(View.VISIBLE);
			} else {
				textNotificationCount.setVisibility(View.GONE);
			}
		}
	};

	class InboxMessageCountCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			//			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_NOTIFICATION_COUNT, preferences.getString("token", null));

			String response=JSONParser.postRequest(Constant.SERVER_URL+ Constant.MESSAGE_COUNT,null,null,preferences.getString("token", null));
			Log.v(Constant.TAG, "GET_INBOX_COUNT response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					MessageCount = jsonObject.getInt("notifications");

					handlerInboxMessage.sendEmptyMessage(1);

				} catch (Exception e) {
					e.printStackTrace();
					handlerInboxMessage.sendEmptyMessage(0);
				}
			} else {

				handlerInboxMessage.sendEmptyMessage(0);
			}
		}
	}

	Handler handlerInboxMessage = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (MessageCount > 0) {
				textInboxCount.setText(MessageCount + "");
				textInboxCount.setVisibility(View.VISIBLE);
			} else {
				textInboxCount.setVisibility(View.GONE);
			}
		}
	};
	
	class LogoutCall extends Thread {
		
		String deviceId;
		
		public LogoutCall(String device_id){
			this.deviceId=device_id;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			//			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_NOTIFICATION_COUNT, preferences.getString("token", null));
			
			String[] name = {"deviceToken","device"};
			String[] value = {deviceId,"google"};
			
			String response=JSONParser.postRequest(Constant.SERVER_URL+ Constant.LOGOUT,name,value,preferences.getString("token", null));
			Log.v(Constant.TAG, "LOGOUT response " + response);

			if (response != null) {

				try {

//					JSONObject jsonObject = new JSONObject(response);


					handlerLogout.sendEmptyMessage(1);

				} catch (Exception e) {
					e.printStackTrace();
					handlerLogout.sendEmptyMessage(0);
				}
			} else {

				handlerLogout.sendEmptyMessage(0);
			}
		}
	}

	Handler handlerLogout = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
		}
	};


	@Override
	public void onResume() {
		super.onResume();	
		Log.d("mobstar","OnResume Called");

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("mobstar","OnPause Called");
	}





}
