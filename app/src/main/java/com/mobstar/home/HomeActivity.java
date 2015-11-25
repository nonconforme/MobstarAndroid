package com.mobstar.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBar;
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

import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.new_api_call.AuthCall;
import com.mobstar.api.new_api_call.NotificationCall;
import com.mobstar.api.new_api_model.Profile;
import com.mobstar.api.new_api_model.response.NotificationCountResponse;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.api.responce.*;
import com.mobstar.api.responce.Error;
import com.mobstar.blog.BlogFragment;
import com.mobstar.fanconnect.FanConnectHomeFragment;
import com.mobstar.help.HelpFragment;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.home.notification.NotificationsFragment;
import com.mobstar.home.search.SearchFragment;
import com.mobstar.inbox.InboxFragment;
import com.mobstar.login.LoginSocialActivity;
import com.mobstar.login.facebook.FacebookManager;
import com.mobstar.service.NotificationService;
import com.mobstar.settings.SettingsFragment;
import com.mobstar.talentconnect.TalentConnectHomeFragment;
import com.mobstar.upload.SelectUploadTypeActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class HomeActivity extends BaseActivity implements OnClickListener, DrawerListener {

	private ActionBar mActionBar;
	private ScrollView scrollLeftDrawer;
	private DrawerLayout mDrawerLayout;
	private ImageView btnMenu, btnUpload, btnNotification, btnSearch;
	private ImageView imgHeaderLogo;
	private boolean isMenuOpen = false;
	private ImageView imgUserPic,imgBlogDivider;
	private TextView textUserDisplayName, textUserFullName;
	private Profile userProfile;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private HomeFragment homeFragment;
	private LinearLayout llIamFan, llIamTalent;
	private ImageView imgIamTalent, imgIamFan, imgMentorsDivider;
	private LinearLayout llMentors, llSettings, llHelp, llTalentConnect, llFanConnect, llPoints, llLogout, llInbox,llBlog;
	private TextView textNotificationCount,textInboxCount;
	private String deepLinkedId = "";
	private Intent intentNotificationService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		getBundleExtra();
		userProfile = UserPreference.getUserProfile(this);
		setupActionBar();
		mFragmentManager = getSupportFragmentManager();
		findViews();
		setListeners();
		initControls();

		replaceStartFragment();
		getNotificationCount();
		registerReceiver();
		startHowToVoteActivity();
	}

	private void replaceStartFragment(){
		if(deepLinkedId != null && deepLinkedId.length() > 0){
			homeFragment = new HomeFragment();
			final Bundle extras = new Bundle();
			extras.putString("deepLinkedId", deepLinkedId);
			homeFragment.setArguments(extras);
			replaceFragment(homeFragment, "Home Fragment");
		}
		else {
			homeFragment = new HomeFragment();
			replaceFragment(homeFragment, "Home Fragment");
		}
	}

	private void registerReceiver(){
		intentNotificationService = new Intent(this, NotificationService.class);
		startService(intentNotificationService);
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("profile_image_changed"));
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("notification_count_changed"));
		registerReceiver(mReceiver, new IntentFilter("notification_count_update_from_service"));
	}

	private void setupActionBar(){
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		final View customView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
		mActionBar.setCustomView(customView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayShowCustomEnabled(true);
		final Toolbar parent =(Toolbar) customView.getParent();
		parent.setContentInsetsAbsolute(0, 0);
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

	private void findViews(){
		imgUserPic              = (ImageView) findViewById(R.id.imgUserPic);
		textUserDisplayName     = (TextView) findViewById(R.id.textUserDisplayName);
		btnMenu                 = (ImageView) mActionBar.getCustomView().findViewById(R.id.btnMenu);
		textUserFullName        = (TextView) findViewById(R.id.textUserFullName);
		imgHeaderLogo           = (ImageView) mActionBar.getCustomView().findViewById(R.id.imgHeaderLogo);
		btnNotification         = (ImageView) mActionBar.getCustomView().findViewById(R.id.btnNotification);
		textNotificationCount   = (TextView) mActionBar.getCustomView().findViewById(R.id.textNotificationCount);
		btnUpload               = (ImageView) mActionBar.getCustomView().findViewById(R.id.btnUpload);
		btnSearch               = (ImageView) mActionBar.getCustomView().findViewById(R.id.btnSearch);
		mDrawerLayout           = (DrawerLayout) findViewById(R.id.drawer_layout);
		scrollLeftDrawer        = (ScrollView) findViewById(R.id.scrollLeftDrawer);
		llTalentConnect         = (LinearLayout) findViewById(R.id.llTalentConnect);
		llPoints                = (LinearLayout) findViewById(R.id.llPoints);
		llLogout                = (LinearLayout) findViewById(R.id.llLogout);
		llInbox                 = (LinearLayout) findViewById(R.id.llInbox);
		textInboxCount          = (TextView)findViewById(R.id.textInboxCount);
		llFanConnect            = (LinearLayout) findViewById(R.id.llFanConnect);
		llMentors               = (LinearLayout) findViewById(R.id.llMentors);
		llIamFan                = (LinearLayout) findViewById(R.id.llIamFan);
		imgIamFan               = (ImageView) findViewById(R.id.imgIamFan);
		llIamTalent             = (LinearLayout) findViewById(R.id.llIamTalent);
		imgIamTalent            = (ImageView) findViewById(R.id.imgIamTalent);
		imgMentorsDivider       = (ImageView) findViewById(R.id.imgMentorsDivider);
		llSettings              = (LinearLayout) findViewById(R.id.llSettings);
		llMentors               = (LinearLayout) findViewById(R.id.llMentors);
		llBlog                  = (LinearLayout) findViewById(R.id.llBlog);
		imgBlogDivider          = (ImageView)findViewById(R.id.imgBlogDivider);
		llHelp                  = (LinearLayout) findViewById(R.id.llHelp);
	}

	private void setListeners(){
		imgUserPic.setOnClickListener(this);
		textUserDisplayName.setOnClickListener(this);
		textUserFullName.setOnClickListener(this);
		btnMenu.setOnClickListener(this);
		imgHeaderLogo.setOnClickListener(this);
		btnNotification.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		scrollLeftDrawer.setOnClickListener(this);
		llTalentConnect.setOnClickListener(this);
		llPoints.setOnClickListener(this);
		llLogout.setOnClickListener(this);
		llInbox.setOnClickListener(this);
		llIamTalent.setOnClickListener(this);
		llMentors.setOnClickListener(this);
		llFanConnect.setOnClickListener(this);
		llBlog.setOnClickListener(this);
		llSettings.setOnClickListener(this);
		llIamFan.setOnClickListener(this);
		llHelp.setOnClickListener(this);

		mDrawerLayout.setDrawerListener(this);

	}

	private void initControls() {
		if (userProfile.getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso.with(this)
					.load(userProfile.getProfileImage())
					.resize(Utility.dpToPx(this, 60), Utility.dpToPx(this, 60))
					.centerCrop()
					.placeholder(R.drawable.ic_pic_small)
					.error(R.drawable.ic_pic_small)
					.into(imgUserPic);
		}

		textUserDisplayName.setText(userProfile.getDisplayName());
		textUserFullName.setText(userProfile.getFullName());
		textNotificationCount.setVisibility(View.GONE);
		textInboxCount.setVisibility(View.GONE);
		imgIamFan.setImageResource(R.drawable.side_iam_fan_act);
		imgIamTalent.setImageResource(R.drawable.side_iam_talent);

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

		switch (view.getId()){
			case R.id.btnMenu:
				invertMainMenu();
				break;
			case R.id.btnUpload:
				startSelectUploadTypeActivity();
				break;
			case R.id.btnSearch:
				replaceSearchFragment();
				break;
			case R.id.imgHeaderLogo:
				replaceHomeFragment();
				break;
			case R.id.btnNotification:
				replaceNotificationsragment();
				break;
			case R.id.llMentors:
				replaceMentorsFragment();
				break;
			case R.id.llSettings:
				replaceSettingsFragment();
				break;
			case R.id.llIamFan:
				switchToIamFan();
				break;
			case R.id.llIamTalent:
				switchToIamTalent();
				break;
			case R.id.llHelp:
				replaceHelpFragment();
				break;
			case R.id.llTalentConnect:
				replaceTalentConnectFragment();
				break;
			case R.id.llFanConnect:
				replaceFanConnectFragment();
				break;
			case R.id.llBlog:
				replaceBlogFragment();
				break;
			case R.id.llPoints:
				startComingSoonDialog();
				break;
			case R.id.llLogout:
				logOut();
				break;
			case R.id.llInbox:
				replaceInboxFragment();
				break;
			case R.id.imgUserPic:
				startProfileActivity();
				break;
			case R.id.textUserDisplayName:
				startProfileActivity();
				break;
			case R.id.textUserFullName:
				startProfileActivity();
				break;
		}
	}

	private void invertMainMenu(){
		if (isMenuOpen) {
			closeMenu();
		} else {
			openMenu();
		}
	}

	private void startSelectUploadTypeActivity(){
		final Intent intent = new Intent(this, SelectUploadTypeActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		closeMenu();
	}

	private void replaceSearchFragment(){
		final Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof SearchFragment)) {
			SearchFragment searchFragment = new SearchFragment();
			replaceFragment(searchFragment, "SearchFragment", "SearchFragment");
		}
		closeMenu();
	}

	private void replaceHomeFragment(){
		homeFragment = new HomeFragment();
		replaceFragment(homeFragment, "Home Fragment");
		closeMenu();
	}

	private void replaceNotificationsragment(){
		final Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof NotificationsFragment)) {
			NotificationsFragment notificationsFragment = new NotificationsFragment();
			replaceFragment(notificationsFragment, "NotificationsFragment", "NotificationsFragment");
		}
		closeMenu();
	}

	private void replaceMentorsFragment(){
		final Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof MentorsFragment)) {

			MentorsFragment mentorsFragment = new MentorsFragment();
			replaceFragment(mentorsFragment, "MentorsFragment", "MentorsFragment");
		}
		closeMenu();
	}

	private void replaceSettingsFragment(){
		final Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof SettingsFragment)) {
			SettingsFragment settingsFragment = new SettingsFragment();
			replaceFragment(settingsFragment, "SettingsFragment", "SettingsFragment");
		}
		closeMenu();
	}

	private void switchToIamFan(){
		imgIamFan.setImageResource(R.drawable.side_iam_fan_act);
		llIamFan.setBackgroundColor(getResources().getColor(R.color.side_act_bg));
		llFanConnect.setVisibility(View.GONE);
		llTalentConnect.setVisibility(View.VISIBLE);
		llIamTalent.setBackgroundColor(getResources().getColor(R.color.splash_bg));
		imgIamTalent.setImageResource(R.drawable.side_iam_talent);
		llMentors.setVisibility(View.GONE);
		imgMentorsDivider.setVisibility(View.GONE);
		llBlog.setVisibility(View.GONE);
		imgBlogDivider.setVisibility(View.GONE);
	}

	private void switchToIamTalent(){
		imgIamTalent.setImageResource(R.drawable.side_iam_talent_act);
		llIamTalent.setBackgroundColor(getResources().getColor(R.color.side_act_bg));
		llTalentConnect.setVisibility(View.GONE);
		llFanConnect.setVisibility(View.VISIBLE);
		llMentors.setVisibility(View.VISIBLE);
		imgMentorsDivider.setVisibility(View.VISIBLE);
		llIamFan.setBackgroundColor(getResources().getColor(R.color.splash_bg));
		imgIamFan.setImageResource(R.drawable.side_iam_fan);
		llBlog.setVisibility(View.VISIBLE);
		imgBlogDivider.setVisibility(View.VISIBLE);
	}

	private void replaceHelpFragment(){
		final Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof HelpFragment)) {
			HelpFragment helpFragment = new HelpFragment();
			replaceFragment(helpFragment, "HelpFragment", "HelpFragment");
		}
		closeMenu();
	}

	private void replaceTalentConnectFragment(){
		final Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof TalentConnectHomeFragment)) {
			TalentConnectHomeFragment talentConnectHomeFragment = new TalentConnectHomeFragment();
			replaceFragment(talentConnectHomeFragment, "TalentConnectFragment", "TalentConnectFragment");
		}
		closeMenu();
	}

	private void replaceFanConnectFragment(){
		final Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof FanConnectHomeFragment)) {

			FanConnectHomeFragment fanConnectHomeFragment = new FanConnectHomeFragment();
			replaceFragment(fanConnectHomeFragment, "FanConnectHomeFragment", "FanConnectHomeFragment");
		}
		closeMenu();
	}

	private void replaceBlogFragment(){
		final Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof BlogFragment)) {

			BlogFragment blogFragment = new BlogFragment();
			replaceFragment(blogFragment, "BlogFragment", "BlogFragment");
		}
		closeMenu();

	}

	private void startComingSoonDialog(){
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
		alertDialogBuilder.setMessage(getString(R.string.coming_soon)).setCancelable(false).setNeutralButton("OK", null);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void logOut(){
		logOutRequest();
		FacebookManager.facebookLogOut(HomeActivity.this);
		UserPreference.logOut(this);
		startLoginSocialActivity();
	}

	private void logOutRequest(){
		AuthCall.signOut(this, new ConnectCallback<SuccessResponse>() {
			@Override
			public void onSuccess(SuccessResponse object) {

			}

			@Override
			public void onFailure(String error) {

			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {

			}
		});
	}

	private void replaceInboxFragment(){
		Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof InboxFragment)) {
			InboxFragment talentConnectHomeFragment = new InboxFragment();
			replaceFragment(talentConnectHomeFragment, "InboxFragment", "InboxFragment");
		}
		closeMenu();
	}

	private void startLoginSocialActivity() {
		final Intent intent = new Intent(this, LoginSocialActivity.class);
		startActivity(intent);
		finish();
	}


	private void startProfileActivity(){
		final Intent intent = new Intent(this, NewProfileActivity.class);
		final UserProfile userProfile =  UserProfile.newBuilder()
//				.setUserId(preferences.getString("userid", ""))
//				.setUserName(preferences.getString("username", ""))
//				.setUserPic(preferences.getString("profile_image", ""))
//				.setUserDisplayName(preferences.getString("displayName", ""))
//				.setUserCoverImage(preferences.getString("cover_image", ""))
//				.setUserTagline(preferences.getString("tagline", ""))
				.build();
		intent.putExtra(NewProfileActivity.USER, userProfile);

		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void closeMenu() {

		mDrawerLayout.closeDrawer(scrollLeftDrawer);
		isMenuOpen = false;
		btnMenu.setImageResource(R.drawable.icon_menu);
		//		btnMenu.setBackgroundColor(getResources().getColor(R.color.side_menu_bg_normal));
		btnMenu.setBackgroundColor(0);
		getNotificationCount();
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
		super.onBackPressed();

	}

	@Override
	public void onDestroy() {
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
		if (Utility.isNetworkAvailable(this)) {
			new InboxMessageCountCall().start();
		}
	}

	@Override
	public void onDrawerSlide(View arg0, float arg1) {

	}

	@Override
	public void onDrawerStateChanged(int arg0) {

	}

	private void getNotificationCount(){
		NotificationCall.getNotificationCount(this, new ConnectCallback<NotificationCountResponse>() {
			@Override
			public void onSuccess(NotificationCountResponse object) {
				setNotificationCount(object.getNotificationsCount());
			}

			@Override
			public void onFailure(String error) {

			}

			@Override
			public void onServerError(Error error) {

			}
		});
	}

	private void setNotificationCount(final int notificationCount){
		if (notificationCount > 0) {
			textNotificationCount.setText(notificationCount + "");
			textNotificationCount.setVisibility(View.VISIBLE);
			Utility.setBadge(HomeActivity.this, notificationCount);
		} else {
			textNotificationCount.setVisibility(View.GONE);
			Utility.clearBadge(HomeActivity.this);
		}
	}

	private void setMessageCount(int messageCount){
		if (messageCount > 0) {
			textInboxCount.setText(messageCount + "");
			textInboxCount.setVisibility(View.VISIBLE);
		} else {
			textInboxCount.setVisibility(View.GONE);
		}
	}

	class InboxMessageCountCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			//			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_NOTIFICATION_COUNT, preferences.getString("token", null));

//			String response=JSONParser.postRequest(Constant.SERVER_URL+ Constant.MESSAGE_COUNT,null,null,preferences.getString("token", null));
//			Log.v(Constant.TAG, "GET_INBOX_COUNT response " + response);
//
//			if (response != null) {
//
//				try {
//
//					JSONObject jsonObject = new JSONObject(response);
//
//					MessageCount = jsonObject.getInt("notifications");
//
//					handlerInboxMessage.sendEmptyMessage(1);
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					handlerInboxMessage.sendEmptyMessage(0);
//				}
//			} else {
//
//				handlerInboxMessage.sendEmptyMessage(0);
//			}
		}
	}

	Handler handlerInboxMessage = new Handler() {

		@Override
		public void handleMessage(Message msg) {
//			if (MessageCount > 0) {
//				textInboxCount.setText(MessageCount + "");
//				textInboxCount.setVisibility(View.VISIBLE);
//			} else {
//				textInboxCount.setVisibility(View.GONE);
//			}
		}
	};

	private void getBundleExtra(){
		final Bundle b = getIntent().getExtras();
		if(b != null) {
			if(b.containsKey("deepLinkedId")){
				deepLinkedId=b.getString("deepLinkedId");
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();	
		Log.d("mobstar","OnResume Called");

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("mobstar", "OnPause Called");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (homeFragment != null)
			homeFragment.onActivityResult(requestCode, resultCode, data);
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
						Utility.setBadge(HomeActivity.this, count);
					} else {
						textNotificationCount.setVisibility(View.GONE);
						Utility.clearBadge(HomeActivity.this);
					}
				}

			}
			else if (intent.getAction().equalsIgnoreCase("notification_count_changed")) {
				closeMenu();
			} else {
				final String userProfileImage = UserPreference.getUserField(HomeActivity.this, UserPreference.PROFILE_IMAGE);

				if (userProfileImage.equals("")) {
					imgUserPic.setImageResource(R.drawable.ic_pic_small);
				} else {
					imgUserPic.setImageResource(R.drawable.ic_pic_small);
					Picasso
							.with(HomeActivity.this)
							.load(userProfileImage)
							.resize(Utility.dpToPx(HomeActivity.this, 60), Utility.dpToPx(HomeActivity.this, 60))
							.centerCrop()
							.placeholder(R.drawable.ic_pic_small)
							.error(R.drawable.ic_pic_small)
							.into(imgUserPic);
				}

				textUserDisplayName.setText(UserPreference.getUserField(HomeActivity.this, UserPreference.DISPLAY_NAME));

				textUserFullName.setText(UserPreference.getUserField(HomeActivity.this, UserPreference.FULL_NAME));
			}

		}
	};

}
