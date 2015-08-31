package com.mobstar.home;

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
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.mobstar.ProfileActivity;
import com.mobstar.R;
import com.mobstar.custom.PullToRefreshListView;
import com.mobstar.custom.PullToRefreshListView.OnRefreshListener;
import com.mobstar.home.split.SplitActivity;
import com.mobstar.info.report.InformationReportActivity;
import com.mobstar.pojo.EntryPojo;
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

public class VideoListFragment extends Fragment {

	private Context mContext;

	EntryListAdapter entryListAdapter;
	PullToRefreshListView listEntry;

	protected String sErrorMessage;
	ArrayList<EntryPojo> arrEntryPojos = new ArrayList<EntryPojo>();
	ArrayList<EntryPojo> arrEntryPojosParent=new ArrayList<EntryPojo>();

	// int mLastVisibleItem = 0;
	int mFirstVisibleItem = 0;
	boolean isScrolling = false;

	MediaPlayer mediaPlayer;
	int indexCurrentPlayAudio = -1;
	int indexCurrentPauseVideo = -1;

	ArrayList<String> listDownloadingFile = new ArrayList<String>();

	CustomSurfaceTextureListener surfaceTextureListener;

	SharedPreferences preferences;

	float touchX, touchY;

	EntryActionHelper entryActionHelper = new EntryActionHelper();

	boolean isVideoSurfaceReady = false;
	boolean isMoveDone = false;

	boolean isSearchAPI = false;
	String SearchTerm = "";

	boolean isEntryAPI = false;
	String LatestORPopular = "latest";
	String CategoryId="";

	boolean isVoteAPI = false;
	String VoteType = "up";

	boolean isDataLoaded = false;
	Surface tSurface;

	boolean isEntryIdAPI=false;
	String deeplinkEntryId="";

	boolean isMobitAPI=false;

	TextView textNoData;

	boolean isInPauseState = false;

	// pagination
	private boolean isRefresh = false;
	private boolean isWebCall = false;
	private static int currentPage = 1;

	private String FILEPATH;
	private String addedviewImgId="";
	private String unFollowUserId="";
	private boolean isMediaPlayerError=false,isNextPageAvail=false;
	private AdView adView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_video_list, container, false);

		mContext = getActivity();

		FILEPATH = Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + mContext.getPackageName() +"/";

		preferences = getActivity().getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		mediaPlayer = new MediaPlayer();

		entryActionHelper.setActionListener(actionListener);

		Bundle extras = getArguments();
		if (extras != null) {

			if (extras.containsKey("isSearchAPI")) {
				isSearchAPI = extras.getBoolean("isSearchAPI");

				if (extras.containsKey("SearchTerm")) {
					SearchTerm = extras.getString("SearchTerm");
				}
			}

			if (extras.containsKey("isMobitAPI")) {
				isMobitAPI = extras.getBoolean("isMobitAPI");

			}

			if (extras.containsKey("isEntryIdAPI")) {
				isEntryIdAPI = extras.getBoolean("isEntryIdAPI");
				deeplinkEntryId = extras.getString("deepLinkedId");

			}

			if (extras.containsKey("isEntryAPI")) {
				isEntryAPI = extras.getBoolean("isEntryAPI");

				if (extras.containsKey("LatestORPopular")) {
					LatestORPopular = extras.getString("LatestORPopular");
				}

				if(extras.containsKey("categoryId")) {
					CategoryId=extras.getString("categoryId");
				}
			}

			if (extras.containsKey("isVoteAPI")) {
				isVoteAPI = extras.getBoolean("isVoteAPI");

				if (extras.containsKey("VoteType")) {
					VoteType = extras.getString("VoteType");
				}
			}

		}
		isInPauseState = false;
		
		
		

		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("star_added"));
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("star_removed"));
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("entry_deleted"));
		return view;

	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent

			if (intent.getAction().equalsIgnoreCase("star_added")) {

				String tempUserID = intent.getExtras().getString("UserID");

				for (int i = 0; i < arrEntryPojos.size(); i++) {

					if (tempUserID.equals(arrEntryPojos.get(i).getUserID())) {
						arrEntryPojos.get(i).setIsMyStar("1");
					}
				}

			} else if (intent.getAction().equalsIgnoreCase("star_removed")) {

				String tempUserID = intent.getExtras().getString("UserID");

				for (int i = 0; i < arrEntryPojos.size(); i++) {

					if (tempUserID.equals(arrEntryPojos.get(i).getUserID())) {
						arrEntryPojos.get(i).setIsMyStar("0");
					}
				}
			}

			else if(intent.getAction().equalsIgnoreCase("entry_deleted")){

				if(intent.getStringExtra("deletedEntryId")!=null){

					for (int i = 0; i < arrEntryPojos.size(); i++) {

						if(arrEntryPojos.get(i).getID().equalsIgnoreCase(intent.getStringExtra("deletedEntryId"))){
							arrEntryPojos.remove(i);
							break;
						}
					}
				}
				
			}
			
			entryListAdapter.notifyDataSetChanged();
			listEntry.invalidate();

			//			else if(intent.getAction().equalsIgnoreCase("like_dislike")){
			//
			//				if(intent.getStringExtra("likeDislikeEntryId")!=null){
			//
			//					for (int i = 0; i < arrEntryPojos.size(); i++) {
			//
			//						if(arrEntryPojos.get(i).getID().equalsIgnoreCase(intent.getStringExtra("likeDislikeEntryId"))){
			//							arrEntryPojos.remove(i);
			//							break;
			//						}
			//					}
			//				}
			//				entryListAdapter.notifyDataSetChanged();
			//				listEntry.invalidate();
			//			}

		}
	};

	@Override
	public void onResume() {
		isInPauseState = false;
		// TODO Auto-generated method stub
		super.onResume();
		// Log.v(Constant.TAG, "VideoListFragment onResume");
		if (entryListAdapter != null) {
			entryListAdapter.notifyDataSetChanged();
		}
		
		if(adView!=null){
			// Request for Ads
			AdRequest adRequest = new AdRequest.Builder()
	 
			// Add a test device to show Test Ads
//			 .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//			 .addTestDevice("CC5F2C72DF2B356BBF0DA198")
					.build();
	 
			// Load ads into Banner Ads
			adView.loadAd(adRequest);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		isInPauseState = false;

		textNoData = (TextView) view.findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);

		entryListAdapter = new EntryListAdapter();
		listEntry = (PullToRefreshListView) view.findViewById(R.id.listEntries);
		
		adView = (AdView) view.findViewById(R.id.adView);
		 
		

		if (isEntryAPI) {

			listEntry.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub\
					if (Utility.isNetworkAvailable(mContext)) {
						isRefresh = true;
						isWebCall = true;
						currentPage=1;
						new EntryCall(currentPage).start();
					} else {
						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			listEntry.disablePullToRefresh();
		}

		listEntry.setAdapter(entryListAdapter);

		surfaceTextureListener = new CustomSurfaceTextureListener();

		if (!isDataLoaded) {
			GetData();
		} else {
			if (isEntryAPI || isVoteAPI) {
				if (arrEntryPojos.size() == 0) {
					textNoData.setVisibility(View.VISIBLE);
					textNoData.setText(getString(R.string.there_are_no_entries_yet));
				}
			}
		}

		listEntry.setOnScrollListener(new OnScrollListener() {

			private int visibleThreshold = 1;
			private int previousTotal = 0;
			private boolean loading = true;

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

//				if(isEntryAPI){// pagination is currently only in main feed
					if (loading) {
						if (totalItemCount > previousTotal) {
							loading = false;
							previousTotal = totalItemCount;

						}
					}
					if (!loading && !isWebCall && isNextPageAvail && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
						Utility.ShowProgressDialog(mContext, getString(R.string.loading));
						isWebCall = true;
						currentPage++;
						if (Utility.isNetworkAvailable(mContext)) {
							new EntryCall(currentPage).start();
						} else {
							Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}
						loading = true;
					}

//				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				isScrolling = true;
				// Log.i(Constant.TAG, "scrolling..." + isScrolling +
				// " scrollState " + scrollState);
				if (scrollState == 0) {

					int previousFirstVisibleItem = mFirstVisibleItem;

					isVideoSurfaceReady = false;

					isScrolling = false;

					mFirstVisibleItem = listEntry.getFirstVisiblePosition();

					if (listEntry.getChildAt(0) != null) {
						int height = listEntry.getChildAt(0).getHeight();

						if (mFirstVisibleItem != 0) {
							mFirstVisibleItem--;
						}

						if (listEntry.getChildAt(0).getTop() < -((height / 2) + dpToPx(30)))
							mFirstVisibleItem++;
					}

					if (previousFirstVisibleItem != mFirstVisibleItem) {
						indexCurrentPlayAudio = -1;
					}

					entryListAdapter.notifyDataSetChanged();

				}
			}
		});

		// listEntry.setOnScrollListener(new EndlessScrollListener());

	}

	public class CustomSurfaceTextureListener implements SurfaceTextureListener {

		int position;

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int arg1, int arg2) {
			// TODO Auto-generated method stub
			Surface surface = new Surface(surfaceTexture);
			tSurface = surface;

			if (!isInPauseState) {
				PlayVideo(position);
			}
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture texture) {
			// TODO Auto-generated method stub
		}

	}

	public int dpToPx(int dp) {

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int px = Math.round(dp * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;

	}

	void GetData() {

		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
		}
		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {
			isWebCall = true;
			currentPage=1;
			new EntryCall(currentPage).start();

		} else {
			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}


	}

	class EntryCall extends Thread {

		private int pageNo;

		public EntryCall(int pageNo) {
			this.pageNo = pageNo;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String Query = "";
			if (isEntryIdAPI) {
				Query = Constant.SERVER_URL + Constant.GET_ENTRY  + deeplinkEntryId;
			}else if (isSearchAPI) {
				Query = Constant.SERVER_URL + Constant.SEARCH_ENTRY + "?term=" + SearchTerm;
			} else if (isEntryAPI) {
				if(CategoryId!=null && CategoryId.length()>0){
					Query = Constant.SERVER_URL + Constant.ENTRY + "?excludeVotes=true&orderBy=" + LatestORPopular +"&category="+CategoryId+ "&page=" + pageNo;
				}
				else {
					Query = Constant.SERVER_URL + Constant.ENTRY + "?excludeVotes=true&orderBy=" + LatestORPopular + "&page=" + pageNo;
				}

			} else if (isVoteAPI) {
				if (VoteType.equals("all")) {
					Query = Constant.SERVER_URL + Constant.VOTE + "?user=" + preferences.getString("userid", "0")+ "&page=" + pageNo;
				} else {
					Query = Constant.SERVER_URL + Constant.VOTE + "?type=" + VoteType + "&user=" + preferences.getString("userid", "0")+ "&page=" + pageNo;
				}
			}

//			Log.v(Constant.TAG, "Query in videoLIstFragment-> " + Query);

			String response = JSONParser.getRequest(Query, preferences.getString("token", null));

			// Log.v(Constant.TAG, "EntryCall response " + response);
//			Log.d("videoList fragment Response is=>", response);

			try {
				if (response != null) {

					sErrorMessage = "";

					if (response.trim().equals("[]")) {
						sErrorMessage = getString(R.string.no_entries_found);
					}

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("entries") || jsonObject.has("votes")) {

						// arrEntryPojos.clear();
						arrEntryPojosParent.clear();

						JSONArray jsonArrayEntries;
						if (isVoteAPI) {
							jsonArrayEntries = jsonObject.getJSONArray("votes");
						} else {
							jsonArrayEntries = jsonObject.getJSONArray("entries");
						}

						for (int i = 0; i < jsonArrayEntries.length(); i++) {

							JSONObject jsonObj;

							JSONObject jsonObjEntry = null;

							if (isSearchAPI) {
								//jsonObjEntry = jsonArrayEntries.getJSONObject(i);
								//added by khyati and change api search2
								jsonObj = jsonArrayEntries.getJSONObject(i);
								jsonObjEntry = jsonObj.getJSONObject("entry");
							} else if (isEntryAPI) {
								jsonObj = jsonArrayEntries.getJSONObject(i);
								jsonObjEntry = jsonObj.getJSONObject("entry");
							}
							else if (isEntryIdAPI) {
								jsonObj = jsonArrayEntries.getJSONObject(i);
								jsonObjEntry = jsonObj.getJSONObject("entry");
							}else if (isVoteAPI) {
								jsonObj = jsonArrayEntries.getJSONObject(i);

								JSONObject jsonObjVote = jsonObj.getJSONObject("vote");

								jsonObjEntry = jsonObjVote.getJSONObject("entry");
							}

							EntryPojo entryPojo = new EntryPojo();

							if (jsonObjEntry.has("user")) {
								JSONObject jsonObjUser = jsonObjEntry.getJSONObject("user");
								entryPojo.setUserID(jsonObjUser.getString("id"));
								entryPojo.setUserName(jsonObjUser.getString("userName"));
								entryPojo.setUserDisplayName(jsonObjUser.getString("displayName"));
								entryPojo.setProfileImage(jsonObjUser.getString("profileImage"));
								entryPojo.setProfileCover(jsonObjUser.getString("profileCover"));
								entryPojo.setTagline(jsonObjUser.getString("tagLine"));
								if (jsonObjUser.has("isMyStar")) {
									entryPojo.setIsMyStar(jsonObjUser.getString("isMyStar"));
								}
								if(jsonObjUser.has("iAmStar")){
									entryPojo.setIAmStar(jsonObjUser.getString("iAmStar"));
								}

							}

							entryPojo.setID(jsonObjEntry.getString("id"));

							if(jsonObjEntry.has("subcategory")){
								entryPojo.setSubCategry(jsonObjEntry.getString("subcategory"));
							}

							if(jsonObjEntry.has("age")){
								entryPojo.setAge(jsonObjEntry.getString("age"));
							}

							if(jsonObjEntry.has("height")){
								entryPojo.setHeight(jsonObjEntry.getString("height"));
							}
							entryPojo.setCategory(jsonObjEntry.getString("category"));
							entryPojo.setType(jsonObjEntry.getString("type"));
							entryPojo.setName(jsonObjEntry.getString("name"));
							entryPojo.setDescription(jsonObjEntry.getString("description"));
							entryPojo.setCreated(jsonObjEntry.getString("created"));
							entryPojo.setModified(jsonObjEntry.getString("modified"));
							entryPojo.setUpVotesCount(jsonObjEntry.getString("upVotes"));
							entryPojo.setDownvotesCount(jsonObjEntry.getString("downVotes"));
							entryPojo.setRank(jsonObjEntry.getString("rank"));
							entryPojo.setLanguage(jsonObjEntry.getString("language"));
							entryPojo.setDeleted(jsonObjEntry.getString("deleted"));
							entryPojo.setTotalComments(jsonObjEntry.getString("totalComments"));

							if(jsonObjEntry.has("totalviews")){
								entryPojo.setTotalViews(jsonObjEntry.getString("totalviews"));
							}


							if (jsonObjEntry.has("videoThumb")) {
								entryPojo.setVideoThumb(jsonObjEntry.getString("videoThumb"));
							}

							JSONArray jsonArrayTags = jsonObjEntry.getJSONArray("tags");

							for (int j = 0; j < jsonArrayTags.length(); j++) {
								entryPojo.addTags(jsonArrayTags.getString(j));
							}

							if (!jsonObjEntry.has("entryFiles")) {
								// Log.v(Constant.TAG,
								// "entryFiles not exist in ID " +
								// entryPojo.getID());
							} else {
								JSONArray jsonArrayFiles = jsonObjEntry.getJSONArray("entryFiles");
								for (int j = 0; j < jsonArrayFiles.length(); j++) {
									JSONObject jsonObjFile = jsonArrayFiles.getJSONObject(j);

									if (entryPojo.getType().equalsIgnoreCase("image")) {
										entryPojo.setImageLink(jsonObjFile.getString("filePath"));
										entryPojo.setFiletype(jsonObjFile.getString("fileType"));

										// Log.v(Constant.TAG,
										// "Image "+jsonObjFile.getString("filePath"));
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

								// arrEntryPojos.add(entryPojo);
								arrEntryPojosParent.add(entryPojo);
							}

						}

						if (isRefresh) {
							mFirstVisibleItem = 0;
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									listEntry.onRefreshComplete();
								}
							});

							isRefresh = false;
							arrEntryPojos.clear();
							arrEntryPojos.addAll(arrEntryPojosParent);

						} else {
							if (arrEntryPojos != null && arrEntryPojos.size() > 0) {
								mFirstVisibleItem = arrEntryPojos.size();
								arrEntryPojos.addAll(arrEntryPojosParent);

							} else {
								mFirstVisibleItem = 0;
								arrEntryPojos.clear();
								arrEntryPojos.addAll(arrEntryPojosParent);
							}
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
				}

				if (sErrorMessage != null && !sErrorMessage.equals("")) {
					isRefresh = false;
					handlerEntry.sendEmptyMessage(0);
				} else {
					handlerEntry.sendEmptyMessage(1);
				}

			} catch (Exception exception) {
				isWebCall = false;
				exception.printStackTrace();
				handlerEntry.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerEntry = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			
			listEntry.onRefreshComplete();

			// mFirstVisibleItem = 0;

			if (mediaPlayer != null) {
				if(mediaPlayer.isPlaying())
					mediaPlayer.pause();
				mediaPlayer.reset();
			}
			// if(mediaPlayer!=null) {
			// if(!mediaPlayer.isPlaying()){
			// mediaPlayer.reset();
			// }
			// }

			indexCurrentPlayAudio = -1;
			indexCurrentPauseVideo = -1;

			isDataLoaded = true;
			isWebCall = false;
			if (msg.what == 1) {
				textNoData.setVisibility(View.GONE);
				if(isEntryIdAPI) {
					isEntryIdAPI=false;
					deeplinkEntryId="";
					//					isEntryAPI=true;

				}
				else if (isEntryAPI || isVoteAPI) {
					if (arrEntryPojos.size() == 0) {
						textNoData.setVisibility(View.VISIBLE);
						textNoData.setText(getString(R.string.there_are_no_entries_yet));
					}
				}
				entryListAdapter.notifyDataSetChanged();

			} else {
				if (isSearchAPI) {
					textNoData.setVisibility(View.VISIBLE);
					textNoData.setText(getString(R.string.nothinh_found_for) + " \"" + SearchTerm + "\"");

					if (getActivity() != null && getActivity().getCurrentFocus() != null) {
						InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
					}

				} else {
					OkayAlertDialog(sErrorMessage);
				}
			}
		}
	};

	void OkayAlertDialog(final String msg) {

		if (getActivity() != null && !getActivity().isFinishing()) {
			getActivity().runOnUiThread(new Runnable() {

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

	void PlayAudio(int position) {

		// Log.v(Constant.TAG, "indexCurrentPlayAudio " +
		// indexCurrentPlayAudio);

		if (indexCurrentPlayAudio == position) {

			if (!mediaPlayer.isPlaying()) {
				Log.d("mobstar","audio play");
				mediaPlayer.start();
			}

		} else {

			indexCurrentPlayAudio = position;

			if (Utility.isNetworkAvailable(mContext)) {
				Log.d("mobstar","Audio update view counter call ---First time--");
				new UpdateViewCountCall(arrEntryPojos.get(position).getID()).start();
			} else {
				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
			}

			new Thread() {

				public void run() {
					try {
						if (mediaPlayer != null) {
							if(mediaPlayer.isPlaying())
								mediaPlayer.pause();
							
							mediaPlayer.reset();
						}

						final String sFileName = Utility.GetFileNameFromURl(arrEntryPojos.get(indexCurrentPlayAudio).getAudioLink());

						// Log.v(Constant.TAG, "sFileName " +
						// Environment.getExternalStorageDirectory() +
						// "/.mobstar/" + sFileName);

						//						File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);

						File file = new File(FILEPATH);

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
							//														mediaPlayer.setLooping(true);
							//Added by Khyati
							mediaPlayer.setLooping(false);
							mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									if(indexCurrentPlayAudio>=0 && !isMediaPlayerError){
										if (Utility.isNetworkAvailable(mContext)) {
											new UpdateViewCountCall(arrEntryPojos.get(indexCurrentPlayAudio).getID()).start();
										} else {
											Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
											//											Utility.HideDialog(mContext);
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


	public class EntryListAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;

		public EntryListAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			return arrEntryPojos.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			final int pos=position;

			final ViewHolder viewHolder;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_item_entry, null);

				viewHolder = new ViewHolder();
                findViews(viewHolder, convertView);


				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

            setupViews(viewHolder, position);
			setEnableSplitButton(viewHolder, position, false);


			viewHolder.btnFollow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (arrEntryPojos.get(pos).getIsMyStar()!=null && !arrEntryPojos.get(pos).getIsMyStar().equalsIgnoreCase("0")) {
						//unfollow
						Utility.ShowProgressDialog(mContext, getString(R.string.loading));

						if (Utility.isNetworkAvailable(mContext)) {
							unFollowUserId=arrEntryPojos.get(pos).getUserID();
							new DeleteStarCall(arrEntryPojos.get(pos).getUserID()).start();
						} else {
							Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}
					} else {
						Utility.ShowProgressDialog(mContext, getString(R.string.loading));

						if (Utility.isNetworkAvailable(mContext)) {

							new AddStarCall(arrEntryPojos.get(pos).getUserID()).start();

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

			if (arrEntryPojos.get(position).getIAmStar()!=null && arrEntryPojos.get(position).getIAmStar().equalsIgnoreCase("1")) {
//				viewHolder.imgMsg.setImageDrawable(drawable)
				Picasso.with(mContext).load(R.drawable.msg_act_btn).into(viewHolder.imgMsg);
			}
			else{
				Picasso.with(mContext).load(R.drawable.msg_btn).into(viewHolder.imgMsg);
			}

			viewHolder.imgMsg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (arrEntryPojos.get(position).getIAmStar() != null && arrEntryPojos.get(pos).getIAmStar().equalsIgnoreCase("1")) {
						//following
 						Intent intent=new Intent(mContext,MessageActivity.class);
						intent.putExtra("recipent",arrEntryPojos.get(pos).getUserID());
						intent.putExtra("isDisableCompose",true);
						startActivity(intent);
					}
				}
			});

			viewHolder.textUserName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, ProfileActivity.class);
					intent.putExtra("UserID", arrEntryPojos.get(pos).getUserID());
					intent.putExtra("UserName", arrEntryPojos.get(pos).getUserName());
					intent.putExtra("UserDisplayName", arrEntryPojos.get(pos).getUserDisplayName());
					intent.putExtra("UserPic", arrEntryPojos.get(pos).getProfileImage());
					intent.putExtra("UserCoverImage", arrEntryPojos.get(pos).getProfileCover());
					intent.putExtra("IsMyStar", arrEntryPojos.get(pos).getIsMyStar());
					intent.putExtra("UserTagline", arrEntryPojos.get(pos).getTagline());
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, ProfileActivity.class);
					intent.putExtra("UserCoverImage", arrEntryPojos.get(pos).getProfileCover());
					intent.putExtra("UserID", arrEntryPojos.get(pos).getUserID());
					intent.putExtra("UserName", arrEntryPojos.get(pos).getUserName());
					intent.putExtra("UserDisplayName", arrEntryPojos.get(pos).getUserDisplayName());
					intent.putExtra("UserPic", arrEntryPojos.get(pos).getProfileImage());
					intent.putExtra("IsMyStar", arrEntryPojos.get(pos).getIsMyStar());
					intent.putExtra("UserTagline", arrEntryPojos.get(pos).getTagline());
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			viewHolder.btnShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, ShareActivity.class);
					intent.putExtra("entry", arrEntryPojos.get(pos));
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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

			viewHolder.layoutStatastics.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, StatisticsActivity.class);
					intent.putExtra("entry", arrEntryPojos.get(pos));
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			viewHolder.btnInfo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (isDataLoaded) {

					}
					Intent intent = new Intent(mContext, InformationReportActivity.class);
					intent.putExtra("entry", arrEntryPojos.get(pos));
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			viewHolder.textCommentCount.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, CommentActivity.class);
					intent.putExtra("entry_id", arrEntryPojos.get(pos).getID());
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
			viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);

			if (arrEntryPojos.get(position).getType().equals("image")) {

				Picasso.with(mContext).load(R.drawable.indicator_image).into(viewHolder.ivIndicator);
				// Log.v(Constant.TAG, "image position " + position);

				viewHolder.ivAudioIcon.setVisibility(View.GONE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.image_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);

//				change width 332 to 360
//				Picasso.with(mContext).load(arrEntryPojos.get(position).getImageLink()).resize(Utility.dpToPx(mContext, 332), Utility.dpToPx(mContext, 360)).centerCrop()
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

				Picasso.with(mContext).load(R.drawable.indicator_audio).into(viewHolder.ivIndicator);

				viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
				viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);

				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.audio_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);

//				Picasso.with(mContext).load(arrEntryPojos.get(position).getImageLink()).resize(Utility.dpToPx(mContext, 332), Utility.dpToPx(mContext, 360)).centerCrop()
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
									if (Utility.isNetworkAvailable(mContext)) {
										AsyncHttpClient client = new AsyncHttpClient();
										final int DEFAULT_TIMEOUT = 60 * 1000;
										client.setTimeout(DEFAULT_TIMEOUT);

										client.get(arrEntryPojos.get(pos).getAudioLink(), new FileAsyncHttpResponseHandler(file) {

											@Override
											public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
//												Log.d("mobstar","Download fail=>"+arrEntryPojos.get(position).getAudioLink());
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

				Picasso.with(mContext).load(R.drawable.indicator_video).into(viewHolder.ivIndicator);
				viewHolder.ivAudioIcon.setVisibility(View.GONE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);

				setEnableSplitButton(viewHolder, position, false);
				//				viewHolder.progressWheel.setVisibility(View.VISIBLE);

				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.video_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);

				Picasso.with(mContext).load(arrEntryPojos.get(position).getVideoThumb())
				.placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder).into(viewHolder.imageFrame, new Callback() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						setEnableSplitButton(viewHolder, position, true);
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

							if (Utility.isNetworkAvailable(mContext)) {
								AsyncHttpClient client = new AsyncHttpClient();
								final int DEFAULT_TIMEOUT = 60 * 1000;

								client.setTimeout(DEFAULT_TIMEOUT);
								client.get(arrEntryPojos.get(position).getVideoLink(), new FileAsyncHttpResponseHandler(file) {

									@Override
									public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
//										Log.d("mobstar","Download fail video=>"+arrEntryPojos.get(position).getVideoLink());

									}

									@Override
									public void onSuccess(int arg0, Header[] arg1, File file) {
										// TODO Auto-generated method stub
										// Log.v(Constant.TAG,
										// "onSuccess Video File  downloaded");
										setEnableSplitButton(viewHolder, position, true);
										viewHolder.progressbar.setVisibility(View.GONE);
										viewHolder.textureView.setVisibility(View.GONE);

										listDownloadingFile.remove(file.getName());

										notifyDataSetChanged();

									}

									//								@Override
									//								public void onProgress(int bytesWritten, int totalSize) {
									//									super.onProgress(bytesWritten, totalSize);
									//									final int totProgress = (int) (((float) bytesWritten * 100) / totalSize);
									//										getActivity().runOnUiThread(new Runnable() {
									//
									//											@Override
									//											public void run() {
									//												viewHolder.progressbar.setProgress(totProgress);
									////												viewHolder.progressbar.setProgress(totProgress);
									//												Log.i("Progress::::", "" + totProgress);
									//											}
									//											});
									//
									//									notifyDataSetChanged();
									//									}

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
							setEnableSplitButton(viewHolder, position, true);
							viewHolder.progressbar.setVisibility(View.GONE);
							viewHolder.textureView.setVisibility(View.GONE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else {
					setEnableSplitButton(viewHolder, position, false);
					viewHolder.progressbar.setVisibility(View.VISIBLE);
				}

			}

			viewHolder.imageFrame.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View view, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						touchX = event.getX();
						touchY = event.getY();

						isMoveDone = false;

						break;

					case MotionEvent.ACTION_UP:

						final float yDistance = Math.abs(touchY - event.getY());

						if (yDistance < Utility.dpToPx(mContext, 5)) {
							if (arrEntryPojos.get(pos).getType().equals("audio") && !listDownloadingFile.contains(sFileName) && !isMoveDone) {
								//will not fire other feed click // khyati
								Log.d("mobstar","pos is"+indexCurrentPlayAudio +"--"+pos);
								if(indexCurrentPlayAudio == pos){
									if (mediaPlayer != null) {
										if (mediaPlayer.isPlaying()) {
											Log.d("mobstar","audio pause");
											mediaPlayer.pause();

											viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
											viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);
											indexCurrentPauseVideo = pos;
										} else {
											Log.d("mobstar","go for play1");
											PlayAudio(pos);
											viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
											viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);
											indexCurrentPauseVideo = -1;
										}
									} else {
										Log.d("mobstar","go for play2");
										PlayAudio(pos);
										viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
										viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);
										indexCurrentPauseVideo = -1;

									}
								}

							} else if (arrEntryPojos.get(pos).getType().equals("video") && !listDownloadingFile.contains(sFileName) && !isMoveDone) {
								//will not fire other feed click // khyati
								Log.d("mobstar","position is===>"+pos);
								if(indexCurrentPlayAudio == pos || indexCurrentPauseVideo == pos){
									if (mediaPlayer != null) {
										if (mediaPlayer.isPlaying()) {
											mediaPlayer.pause();
											indexCurrentPauseVideo = pos;
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

						break;

					case MotionEvent.ACTION_MOVE:

						if (isVoteAPI) {
							break;
						}

						final float yDistance1 = Math.abs(touchY - event.getY());

						if (yDistance1 < Utility.dpToPx(mContext, 50) && !isMoveDone) {

							if (touchX > event.getX() + Utility.dpToPx(mContext, 100)) {

								isMoveDone = true;

								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
									String[] name = { "entry", "type" };
									String[] value = { arrEntryPojos.get(mFirstVisibleItem).getID(), "down" };
									entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
									Utility.DisLikeDialog(getActivity());


									arrEntryPojos.remove(mFirstVisibleItem);
									mFirstVisibleItem = 0;
									if (mediaPlayer != null) {
										if(mediaPlayer.isPlaying())
											mediaPlayer.pause();

//										Log.d("mobstar","on imgframe1 going to reset");
										mediaPlayer.reset();
									}
									indexCurrentPlayAudio = -1;
									entryListAdapter.notifyDataSetChanged();

									if (arrEntryPojos.size() == 0) {
										textNoData.setVisibility(View.VISIBLE);
										textNoData.setText(getString(R.string.there_are_no_entries_yet));
									}
								}

							} else if (touchX < event.getX() - Utility.dpToPx(mContext, 100)) {

								isMoveDone = true;

								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
									String[] name = { "entry", "type" };
									String[] value = { arrEntryPojos.get(mFirstVisibleItem).getID(), "up" };
									entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
									Utility.LikeDialog(getActivity());

									arrEntryPojos.remove(mFirstVisibleItem);
									mFirstVisibleItem = 0;
									if (mediaPlayer != null) {
										if(mediaPlayer.isPlaying())
											mediaPlayer.pause();

//										Log.d("mobstar","on imgframe2 going to reset");
										mediaPlayer.reset();
									}
									indexCurrentPlayAudio = -1;

									entryListAdapter.notifyDataSetChanged();

									if (arrEntryPojos.size() == 0) {
										textNoData.setVisibility(View.VISIBLE);
										textNoData.setText(getString(R.string.there_are_no_entries_yet));
									}
								}
							}
						}

						break;
					default:
						break;
					}
					return true;
				}
			});

			viewHolder.textureView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View view, MotionEvent event) {
					// TODO Auto-generated method stub
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						touchX = event.getX();
						touchY = event.getY();

						isMoveDone = false;

						break;

					case MotionEvent.ACTION_UP:

						final float yDistance = Math.abs(touchY - event.getY());

						if (yDistance < Utility.dpToPx(mContext, 5)) {
							if (arrEntryPojos.get(pos).getType().equals("video") && !listDownloadingFile.contains(sFileName) && !isMoveDone) {
								if (mediaPlayer != null) {
									if (mediaPlayer.isPlaying()) {
										mediaPlayer.pause();

										indexCurrentPauseVideo = pos;

										viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
										viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);

									} else {

										indexCurrentPauseVideo = -1;

										isVideoSurfaceReady = true;
										entryListAdapter.notifyDataSetChanged();

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
							else if (arrEntryPojos.get(pos).getType().equals("audio") && !listDownloadingFile.contains(sFileName) && !isMoveDone) {
								if (mediaPlayer != null) {
									if (mediaPlayer.isPlaying()) {
										mediaPlayer.pause();

										indexCurrentPauseVideo = pos;
										Log.d("mobstar","audio pause 2");
										viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
										viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);

									} else {
										Log.d("mobstar","go for play3");
										PlayAudio(pos);
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

						break;

					case MotionEvent.ACTION_MOVE:

						if (isVoteAPI) {
							break;
						}

						final float yDistance1 = Math.abs(touchY - event.getY());

						if (yDistance1 < Utility.dpToPx(mContext, 50) && !isMoveDone) {

							if (touchX > event.getX() + Utility.dpToPx(mContext, 100)) {

								isMoveDone = true;

								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
									String[] name = { "entry", "type" };
									String[] value = { arrEntryPojos.get(mFirstVisibleItem).getID(), "down" };
									entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
									Utility.DisLikeDialog(getActivity());

									arrEntryPojos.remove(mFirstVisibleItem);
									mFirstVisibleItem = 0;
									if (mediaPlayer != null) {
										if(mediaPlayer.isPlaying())
											mediaPlayer.pause();

//										Log.d("mobstar","on texureview1 going to reset");
										mediaPlayer.reset();
									}
									indexCurrentPlayAudio = -1;

									entryListAdapter.notifyDataSetChanged();

									if (arrEntryPojos.size() == 0) {
										textNoData.setVisibility(View.VISIBLE);
										textNoData.setText(getString(R.string.there_are_no_entries_yet));
									}
								}

							} else if (touchX < event.getX() - Utility.dpToPx(mContext, 100)) {

								isMoveDone = true;

								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
									String[] name = { "entry", "type" };
									String[] value = { arrEntryPojos.get(mFirstVisibleItem).getID(), "up" };
									entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
									Utility.LikeDialog(getActivity());

									arrEntryPojos.remove(mFirstVisibleItem);
									mFirstVisibleItem = 0;
									if (mediaPlayer != null) {
										if(mediaPlayer.isPlaying())
											mediaPlayer.pause();

//										Log.d("mobstar","on texureview2 going to reset");
										mediaPlayer.reset();
									}
									indexCurrentPlayAudio = -1;

									entryListAdapter.notifyDataSetChanged();

									if (arrEntryPojos.size() == 0) {
										textNoData.setVisibility(View.VISIBLE);
										textNoData.setText(getString(R.string.there_are_no_entries_yet));
									}
								}
							}
						}

						break;
					default:
						break;
					}
					return true;
				}
			});

			if (mFirstVisibleItem == position && !isScrolling) {

				if (!listDownloadingFile.contains(sFileName)) {

					if (arrEntryPojos.get(position).getType().equals("audio")) {

						// if condition added by khyati
						if(indexCurrentPauseVideo == position){
							viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
							viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);
						}
						else{
							viewHolder.textureView.setVisibility(View.GONE);
							// viewHolder.textBgGray.setVisibility(View.GONE);
							viewHolder.imageFrame.setVisibility(View.VISIBLE);
							Log.d("mobstar","go for play4");
							viewHolder.ivAudioIcon.setVisibility(View.INVISIBLE);
							PlayAudio(position);

						}




					} else if (arrEntryPojos.get(position).getType().equals("video")) {

						// Log.v(Constant.TAG, "Play Video " + position +
						// " isVideoSurfaceReady " + isVideoSurfaceReady +
						// " indexCurrentPlayAudio " + indexCurrentPlayAudio);


						if (indexCurrentPauseVideo == position) {
							viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
							viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);

						} else {


							indexCurrentPauseVideo = -1;

							viewHolder.textureView.setVisibility(View.VISIBLE);

							if (mediaPlayer != null && mediaPlayer.isPlaying() && indexCurrentPlayAudio == position) {
								setEnableSplitButton(viewHolder, position, true);
								viewHolder.flPlaceHolder.setVisibility(View.GONE);
								viewHolder.progressbar.setVisibility(View.GONE);
								// viewHolder.textBgGray.setVisibility(View.GONE);
							} else if (isVideoSurfaceReady) {
								// Log.v(Constant.TAG,
								// "isVideoSurfaceReady Play Video " +
								// position);
								setEnableSplitButton(viewHolder, position, true);
								viewHolder.flPlaceHolder.setVisibility(View.GONE);
								viewHolder.progressbar.setVisibility(View.GONE);
								// viewHolder.textBgGray.setVisibility(View.GONE);
							} else {
								setEnableSplitButton(viewHolder, position, false);
								viewHolder.flPlaceHolder.setVisibility(View.VISIBLE);
								viewHolder.progressbar.setVisibility(View.VISIBLE);
								// viewHolder.textBgGray.setVisibility(View.VISIBLE);
							}

							surfaceTextureListener.position = position;
							viewHolder.textureView.setSurfaceTextureListener(surfaceTextureListener);

							if (viewHolder.textureView.isAvailable()) {
								// Log.v(Constant.TAG,
								// "textureView isAvailable position " +
								// position);


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
//									Log.d("mobstar","2 Add View for image");
									addedviewImgId=arrEntryPojos.get(position).getID();
									new UpdateViewCountCall(arrEntryPojos.get(position).getID()).start();
								} else {
									Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
									//										Utility.HideDialog(mContext);
								}
							}
							else{
//								Log.d("mobstar","same cell is displaying");
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

        private void findViews(ViewHolder viewHolder, View convertView){
            viewHolder.textVideoSplit = (TextView) convertView.findViewById(R.id.splitVideo);
            viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
            viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
            viewHolder.textViews = (TextView) convertView.findViewById(R.id.textViews);
            viewHolder.textDescription = (TextView) convertView.findViewById(R.id.textDescription);
            viewHolder.imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);
            viewHolder.progressbar = (ProgressBar) convertView.findViewById(R.id.progressbar);
            viewHolder.textureView = (TextureView) convertView.findViewById(R.id.textureView);
            viewHolder.btnShare = (FrameLayout) convertView.findViewById(R.id.btnShare);
            viewHolder.btnFollow = (TextView) convertView.findViewById(R.id.btnFollow);
            viewHolder.btnInfo = (FrameLayout) convertView.findViewById(R.id.btnInfo);
            viewHolder.layoutStatastics = (FrameLayout) convertView.findViewById(R.id.layoutStatastic);
            viewHolder.textStatasticCount = (TextView) convertView.findViewById(R.id.textStatasticCount);
            viewHolder.ivAudioIcon = (ImageView) convertView.findViewById(R.id.ivAudioIcon);
            viewHolder.textCommentCount = (TextView) convertView.findViewById(R.id.textCommentCount);
            viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
            viewHolder.imgPlaceHolder = (ImageView) convertView.findViewById(R.id.imgPlaceHolder);
            viewHolder.flPlaceHolder = (FrameLayout) convertView.findViewById(R.id.flPlaceHolder);
            viewHolder.imgMsg=(ImageView) convertView.findViewById(R.id.imgMsg);
            viewHolder.ivIndicator=(ImageView) convertView.findViewById(R.id.ivIndicator);

        }

        private void setupViews(ViewHolder viewHolder, int position){
            viewHolder.textCommentCount.setText(arrEntryPojos.get(position).getTotalComments());
            viewHolder.textUserName.setText(arrEntryPojos.get(position).getName());
            viewHolder.textDescription.setText(Utility.unescape_perl_string(arrEntryPojos.get(position).getDescription()));

            viewHolder.textTime.setText(arrEntryPojos.get(position).getCreated());
            viewHolder.textViews.setText(arrEntryPojos.get(position).getTotalViews());

            viewHolder.textStatasticCount.setText(arrEntryPojos.get(position).getUpVotesCount());

            // Added by khyati for follow/following btn

            if(preferences.getString("userid", "0").equalsIgnoreCase(arrEntryPojos.get(position).getUserID())){
                viewHolder.btnFollow.setVisibility(View.GONE);
            }
            else {
                viewHolder.btnFollow.setVisibility(View.VISIBLE);
                if (arrEntryPojos.get(position).getIsMyStar() != null) {
                    if (!arrEntryPojos.get(position).getIsMyStar().equalsIgnoreCase("0")) {
						viewHolder.btnFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
						viewHolder.btnFollow.setText(getString(R.string.following));
                    } else {
						viewHolder.btnFollow.setBackground(getResources().getDrawable(R.drawable.selector_oval_button));
						viewHolder.btnFollow.setText(getString(R.string.follow));
                    }
                }
            }
        }

		private void setEnableSplitButton(final ViewHolder viewHolder, final int position, boolean enable){
			try {
				if (!enable) {
					viewHolder.textVideoSplit.setEnabled(false);
					viewHolder.textVideoSplit.setTextColor(getResources().getColor(R.color.comment_color_state_disable));
				} else {
					viewHolder.textVideoSplit.setEnabled(true);
					viewHolder.textVideoSplit.setTextColor(getResources().getColor(R.color.comment_color));
					viewHolder.textVideoSplit.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (arrEntryPojos.get(position).getVideoLink() == null)
								return;
							Intent intent = new Intent(getActivity(), SplitActivity.class);
							intent.putExtra(Constant.ENTRY, arrEntryPojos.get(position));
							getActivity().startActivity(intent);
							getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
						}
					});
				}
			}
			catch (IllegalStateException e){
				e.printStackTrace();
			}
		}

		class ViewHolder {
			TextView textUserName, textDescription, textTime,textViews, textVideoSplit;
			ImageView imageFrame;
			ProgressBar progressbar;
			TextureView textureView;
			FrameLayout btnShare;
			TextView btnFollow;
			FrameLayout btnInfo;
			// ImageView btnStatistics;
			ImageView ivAudioIcon;
			ImageView imgUserPic;
			TextView textCommentCount;
			ImageView imgPlaceHolder;
			FrameLayout flPlaceHolder;
			FrameLayout layoutStatastics;
			TextView textStatasticCount;
			ImageView imgMsg,ivIndicator;
		}

	}

	void PlayVideo(int position) {


		if (indexCurrentPlayAudio == position) {

			//			Log.v(Constant.TAG, "Current video is playing");

			if (!mediaPlayer.isPlaying()) {
				// Log.v(Constant.TAG, "mediaPlayer.isPlaying() " +
				// mediaPlayer.isPlaying() + " Set Surface agian");
				mediaPlayer.start();

				entryListAdapter.notifyDataSetChanged();
			}
			if (!isVideoSurfaceReady) {
				isVideoSurfaceReady = true;

				entryListAdapter.notifyDataSetChanged();
				// Log.v(Constant.TAG, "PlayVideo isVideoSurfaceReady");
			}

		} else {

			// Log.v(Constant.TAG, "Current video is not playing");
			//khyati do webcall for view
			
			if (Utility.isNetworkAvailable(mContext)) {
				Log.d("Mobstar","updateViewCall first time view....");
				new UpdateViewCountCall(arrEntryPojos.get(position).getID()).start();
			} else {
				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
			}

			indexCurrentPlayAudio = position;

			new Thread() {

				public void run() {

					try {
						if (mediaPlayer != null) {
							if(mediaPlayer.isPlaying())
								mediaPlayer.pause();
							
//							Log.d("mobstar","on play video reset");
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

											if (getActivity() != null) {
												getActivity().runOnUiThread(new Runnable() {

													@Override
													public void run() {
														// TODO
														// Auto-generated
														// method
														// stub
														isVideoSurfaceReady = true;

														entryListAdapter.notifyDataSetChanged();

														// Log.v(Constant.TAG,
														// "Video setOnPreparedListener");
													}
												});
											}
										}
									};
									timer.schedule(task, 500);
								}
							});
							//														mediaPlayer.setLooping(true);
							//Added by khyati
							
							mediaPlayer.setLooping(false);
							
							mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {

									if(indexCurrentPlayAudio>=0 && !isMediaPlayerError){
										
										if (Utility.isNetworkAvailable(mContext)) {
											new UpdateViewCountCall(arrEntryPojos.get(indexCurrentPlayAudio).getID()).start();
										} else {
											Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
											//												Utility.HideDialog(mContext);
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
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	// added by khyati
	class AddStarCall extends Thread {

		String userID;

		AddStarCall(String userID) {
			this.userID = userID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "star" };
			String[] value = { userID };

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
							if (arrEntryPojos.get(i).getUserID().equalsIgnoreCase(userID)) {
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
			} else {

			}
		}
	};

	//khyati 

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
			//				Utility.HideDialog(mContext);

			if (msg.what == 1) {

			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	public void onPause() {
		super.onPause();

		if (mediaPlayer != null) {
			if(mediaPlayer.isPlaying())
				mediaPlayer.pause();
			mediaPlayer.reset();
		}

		isVideoSurfaceReady = false;
		isInPauseState = true;
		indexCurrentPlayAudio = -1;


	};

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
				if(unFollowUserId!=""){
					Intent intent = new Intent("star_removed");
					intent.putExtra("UserID",unFollowUserId);
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
				}
				unFollowUserId="";

			} else {

			}
		}
	};


	ActionListener actionListener = new ActionListener() {
		@Override
		public void onComplete() {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (mediaPlayer != null) {
			if(mediaPlayer.isPlaying())
				mediaPlayer.pause();
			mediaPlayer.reset();
		}

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
}
