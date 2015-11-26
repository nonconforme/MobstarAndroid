package com.mobstar.home;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.mobstar.R;
import com.mobstar.custom.PullToRefreshListView;
import com.mobstar.custom.PullToRefreshListView.OnRefreshListener;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.info.report.InformationReportActivity;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.EntryActionHelper;
import com.mobstar.utils.EntryActionHelper.ActionListener;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import cz.msebera.android.httpclient.Header;

public class MobItVideoListFragment extends Fragment {

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

	boolean isVoteAPI = false;
	String VoteType = "up";

	boolean isDataLoaded = false;
	Surface tSurface;

	boolean isMobitAPI=false;

	TextView textNoData;

	boolean isInPauseState = false;

	// pagination
	private boolean isRefresh = false;
	private boolean isWebCall = false;
	private static int currentPage = 1;
	
	private String FILEPATH;

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

//		Log.d("Mobstar","onCreatedView... isDataLoaded"+isDataLoaded);


		Bundle extras = getArguments();
		if (extras != null) {

			if (extras.containsKey("isSearchAPI")) {
				isSearchAPI = extras.getBoolean("isSearchAPI");

				if (extras.containsKey("SearchTerm")) {
					SearchTerm = extras.getString("SearchTerm");
				}
			}

			if (extras.containsKey("isMobitAPI")) {
//				Log.d("mobstar","mobit api..");
				isMobitAPI = extras.getBoolean("isMobitAPI");

			}



			if (extras.containsKey("isEntryAPI")) {
//				Log.d("mobstar","entry api..");
				isEntryAPI = extras.getBoolean("isEntryAPI");

				if (extras.containsKey("LatestORPopular")) {
					LatestORPopular = extras.getString("LatestORPopular");
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
		listEntry.setBackgroundColor(getResources().getColor(R.color.mobit_yellow_color));

//		Log.d("Mobstar","onViewCreated 11... isDataLoaded"+isDataLoaded);

		if (isEntryAPI) {

			listEntry.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub\
					if (Utility.isNetworkAvailable(mContext)) {
						isRefresh = true;
						isWebCall = true;
						currentPage=1;
//						Log.d("mobstar","Call from pull to refresh");
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
			if (isEntryAPI) {
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

				if(isEntryAPI){// pagination is currently only in main feed
					if (loading) {
						if (totalItemCount > previousTotal) {
							loading = false;
							previousTotal = totalItemCount;

						}
					}
					if (!loading && !isWebCall && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
						Utility.ShowProgressDialog(mContext, getString(R.string.loading));
						isWebCall = true;
						currentPage++;
						if (Utility.isNetworkAvailable(mContext)) {
//							Log.d("mobstar","Load from scroll");
							new EntryCall(currentPage).start();
						} else {
							Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}
						loading = true;
					}

				}
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

//					Log.i(Constant.TAG, "scrolling stopped..." + isScrolling);

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

					//	Log.v(Constant.TAG, "mFirstVisibleItem " + mFirstVisibleItem);
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
			if (isSearchAPI) {
				Query = Constant.SERVER_URL + Constant.SEARCH_ENTRY + "?term=" + SearchTerm;
			}
			else if (isEntryAPI) {
				Query = Constant.SERVER_URL + Constant.ENTRY + "?excludeVotes=true&orderBy=" + LatestORPopular +"&category=8"+ "&page=" + pageNo;

			} else if (isVoteAPI) {

				if (VoteType.equals("all")) {
					Query = Constant.SERVER_URL + Constant.VOTE + "?user=" + preferences.getString("userid", "0");
				} else {
					Query = Constant.SERVER_URL + Constant.VOTE + "?type=" + VoteType + "&user=" + preferences.getString("userid", "0");
				}
			}

			Log.v(Constant.TAG, "Query of mobitVideo" + Query);

			String response = JSONParser.getRequest(Query, preferences.getString("token", null));

			// Log.v(Constant.TAG, "EntryCall response " + response);

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

							}

							entryPojo.setID(jsonObjEntry.getString("id"));

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
				if (isEntryAPI) {
					if (arrEntryPojos.size() == 0) {
						textNoData.setVisibility(View.VISIBLE);
						textNoData.setText(getString(R.string.there_are_no_entries_yet));
					}

				}
				entryListAdapter.notifyDataSetChanged();

			} else {
				if (isSearchAPI) {
					textNoData.setVisibility(View.VISIBLE);
					textNoData.setText("NOTHING FOUND FOR \"" + SearchTerm + "\"");

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
				mediaPlayer.start();
			}

		} else {

			indexCurrentPlayAudio = position;

			if (Utility.isNetworkAvailable(mContext)) {
				new UpdateViewCountCall(arrEntryPojos.get(position).getID()).start();
			} else {
				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
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
									if(indexCurrentPlayAudio>0){
										if (Utility.isNetworkAvailable(mContext)) {
											new UpdateViewCountCall(arrEntryPojos.get(indexCurrentPlayAudio).getID()).start();
										} else {
											Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
											Utility.HideDialog(mContext);
										}
										mediaPlayer.seekTo(0);
										mediaPlayer.start();
									}
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

	// public class EndlessScrollListener implements OnScrollListener {
	//
	// private int visibleThreshold = 5;
	// private int currentPage = 0;
	// private int previousTotal = 0;
	// private boolean loading = true;
	//
	// public EndlessScrollListener() {
	// }
	//
	// public EndlessScrollListener(int visibleThreshold) {
	// this.visibleThreshold = visibleThreshold;
	// }
	//
	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	//
	// Log.d("Mobstar", "ToatalItem" + totalItemCount);
	//
	// if (loading) {
	// if (totalItemCount > previousTotal) {
	// Log.d("Mobestar", "call from endless loading false"
	// + currentPage);
	// loading = false;
	// previousTotal = totalItemCount;
	// currentPage++;
	// }
	// }
	// int val = totalItemCount - visibleItemCount;
	// int val1 = firstVisibleItem + visibleThreshold;
	// Log.d(Constant.TAG, "displaying value is=>" + val + "<=" + val1);
	// if (!loading
	// && !isWebCall
	// && (totalItemCount - visibleItemCount) <= (firstVisibleItem +
	// visibleThreshold)) {
	//
	// Log.d("Mobstar", "if "+ totalItemCount + " visible "+ visibleItemCount );
	//
	// Utility.ShowProgressDialog(mContext, "Loading");
	//
	// if (Utility.isNetworkAvailable(mContext)) {
	// Log.d("Mobestar",
	// "call from endless scrollview position is "
	// + currentPage);
	// isWebCall=true;
	// new EntryCall(currentPage).start();
	//
	// } else {
	//
	// Toast.makeText(mContext, getString(R.string.no_internet_access),
	// Toast.LENGTH_SHORT).show();
	// Utility.HideDialog(mContext);
	// }
	// loading = true;
	// }
	//
	// else
	// {
	// Log.d("Mobstar", "else" );
	//
	// }
	// }
	//
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	// }
	// }

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

			final ViewHolder viewHolder;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_item_mobit, null);

				viewHolder = new ViewHolder();

				viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
				viewHolder.textViews = (TextView) convertView.findViewById(R.id.textViews);
				viewHolder.textDescription = (TextView) convertView.findViewById(R.id.textDescription);
				viewHolder.imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);
				viewHolder.progressbar = (ProgressBar) convertView.findViewById(R.id.progressbar);
				viewHolder.textureView = (TextureView) convertView.findViewById(R.id.textureView);
				viewHolder.btnShare = (FrameLayout) convertView.findViewById(R.id.btnShare);
				viewHolder.btnInfo = (FrameLayout) convertView.findViewById(R.id.btnInfo);
				viewHolder.textLikeCount = (TextView) convertView.findViewById(R.id.textLikeCount);
				viewHolder.ivAudioIcon = (ImageView) convertView.findViewById(R.id.ivAudioIcon);
				viewHolder.layoutComment = (FrameLayout) convertView.findViewById(R.id.layoutComment);
//				viewHolder.layoutLike = (FrameLayout) convertView.findViewById(R.id.layoutLike);
				viewHolder.textCommentCount = (TextView) convertView.findViewById(R.id.textCommentCount);
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.imgPlaceHolder = (ImageView) convertView.findViewById(R.id.imgPlaceHolder);
				viewHolder.flPlaceHolder = (FrameLayout) convertView.findViewById(R.id.flPlaceHolder);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.textLikeCount.setText(arrEntryPojos.get(position).getUpVotesCount());
			viewHolder.textCommentCount.setText(arrEntryPojos.get(position).getTotalComments());
			viewHolder.textUserName.setText(arrEntryPojos.get(position).getName());
			viewHolder.textDescription.setText(arrEntryPojos.get(position).getDescription());
			viewHolder.textTime.setText(arrEntryPojos.get(position).getCreated());
			viewHolder.textViews.setText(arrEntryPojos.get(position).getTotalViews());



			viewHolder.textUserName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startProfileActivity(position);
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
					startProfileActivity(position);
				}
			});

			viewHolder.btnShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, ShareActivity.class);
					intent.putExtra("entry", arrEntryPojos.get(position));
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


			viewHolder.btnInfo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (isDataLoaded) {

					}

					Intent intent = new Intent(mContext, InformationReportActivity.class);
					intent.putExtra("entry", arrEntryPojos.get(position));
					getActivity().startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			viewHolder.layoutComment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, CommentActivity.class);
					intent.putExtra("entry_id", arrEntryPojos.get(position).getID());
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

			if (arrEntryPojos.get(position).getType().equals("image")) {

				// Log.v(Constant.TAG, "image position " + position);

				viewHolder.ivAudioIcon.setVisibility(View.GONE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.image_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);

				Picasso.with(mContext).load(arrEntryPojos.get(position).getImageLink()).resize(Utility.dpToPx(mContext, 332), Utility.dpToPx(mContext, 360)).centerCrop()
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
				viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
				viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);

				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.audio_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);

				Picasso.with(mContext).load(arrEntryPojos.get(position).getImageLink()).resize(Utility.dpToPx(mContext, 332), Utility.dpToPx(mContext, 360)).centerCrop()
				.into(viewHolder.imageFrame, new Callback() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						viewHolder.progressbar.setVisibility(View.GONE);
						viewHolder.imageFrame.setVisibility(View.VISIBLE);

						if (!listDownloadingFile.contains(sFileName)) {
							
//							File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);
							
							File file = new File(FILEPATH + sFileName);

							if (!file.exists()) {

								listDownloadingFile.add(sFileName);

								AsyncHttpClient client = new AsyncHttpClient();
								final int DEFAULT_TIMEOUT = 60 * 1000;
								client.setTimeout(DEFAULT_TIMEOUT);

								client.get(arrEntryPojos.get(position).getAudioLink(), new FileAsyncHttpResponseHandler(file) {

									@Override
									public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
										// TODO Auto-generated
										// method
										// stub

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
				viewHolder.ivAudioIcon.setVisibility(View.GONE);
				viewHolder.progressbar.setVisibility(View.VISIBLE);
				//				viewHolder.progressWheel.setVisibility(View.VISIBLE);

				viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);
				viewHolder.imgPlaceHolder.setImageResource(R.drawable.video_placeholder);
				viewHolder.imageFrame.setVisibility(View.GONE);

				Picasso.with(mContext).load(arrEntryPojos.get(position).getVideoThumb()).resize(Utility.dpToPx(mContext, 332), Utility.dpToPx(mContext, 360)).centerCrop()
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
					
					File file = new File(FILEPATH + sFileName);

					if (!file.exists()) {

						listDownloadingFile.add(sFileName);

						// Log.v(Constant.TAG, "Download video " +
						// arrEntryPojos.get(position).getVideoLink());
						//						commented by khyati
						AsyncHttpClient client = new AsyncHttpClient();
						final int DEFAULT_TIMEOUT = 60 * 1000;

						client.setTimeout(DEFAULT_TIMEOUT);
						client.get(arrEntryPojos.get(position).getVideoLink(), new FileAsyncHttpResponseHandler(file) {


							@Override
							public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
								// TODO Auto-generated method stub

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
				}

				else {
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
							if (arrEntryPojos.get(position).getType().equals("audio") && !listDownloadingFile.contains(sFileName) && !isMoveDone) {
								if (mediaPlayer != null) {
									if (mediaPlayer.isPlaying()) {
										mediaPlayer.pause();
										viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_video_pause);
										viewHolder.ivAudioIcon.setVisibility(View.VISIBLE);
									} else {
										PlayAudio(position);
										viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);
									}
								} else {
									PlayAudio(position);
									viewHolder.ivAudioIcon.setImageResource(R.drawable.ic_audio_volume);

								}
							} else if (arrEntryPojos.get(position).getType().equals("video") && !listDownloadingFile.contains(sFileName) && !isMoveDone) {
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

						break;

					case MotionEvent.ACTION_MOVE:

						if (isVoteAPI) {
							break;
						}

						final float yDistance1 = Math.abs(touchY - event.getY());

						if (yDistance1 < Utility.dpToPx(mContext, 50) && !isMoveDone) {

							if (touchX > event.getX() + Utility.dpToPx(mContext, 100)) {

								isMoveDone = true;

								//								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
								//									String[] name = { "entry", "type" };
								//									String[] value = { arrEntryPojos.get(mFirstVisibleItem).getID(), "down" };
								//									entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
								//									Utility.DisLikeDialog(getActivity());
								//
								//
								//									arrEntryPojos.remove(mFirstVisibleItem);
								//									mFirstVisibleItem = 0;
								//									if (mediaPlayer != null) {
								//										mediaPlayer.reset();
								//									}
								//									indexCurrentPlayAudio = -1;
								//									entryListAdapter.notifyDataSetChanged();
								//
								//									if (arrEntryPojos.size() == 0) {
								//										textNoData.setVisibility(View.VISIBLE);
								//										textNoData.setText(getString(R.string.there_are_no_entries_yet));
								//									}
								//								}

							} else if (touchX < event.getX() - Utility.dpToPx(mContext, 100)) {

								isMoveDone = true;

								//								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
								//									String[] name = { "entry", "type" };
								//									String[] value = { arrEntryPojos.get(mFirstVisibleItem).getID(), "up" };
								//									entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
								//									Utility.LikeDialog(getActivity());
								//
								//
								//									arrEntryPojos.remove(mFirstVisibleItem);
								//									mFirstVisibleItem = 0;
								//									if (mediaPlayer != null) {
								//										mediaPlayer.reset();
								//									}
								//									indexCurrentPlayAudio = -1;
								//
								//									entryListAdapter.notifyDataSetChanged();
								//
								//									if (arrEntryPojos.size() == 0) {
								//										textNoData.setVisibility(View.VISIBLE);
								//										textNoData.setText(getString(R.string.there_are_no_entries_yet));
								//									}
								//								}
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
							if (arrEntryPojos.get(position).getType().equals("video") && !listDownloadingFile.contains(sFileName) && !isMoveDone) {
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

						}

						break;

					case MotionEvent.ACTION_MOVE:

						if (isVoteAPI) {
							break;
						}

						//						final float yDistance1 = Math.abs(touchY - event.getY());
						//
						//						if (yDistance1 < Utility.dpToPx(mContext, 50) && !isMoveDone) {
						//
						//							if (touchX > event.getX() + Utility.dpToPx(mContext, 100)) {
						//
						//								isMoveDone = true;

						//								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
						//									String[] name = { "entry", "type" };
						//									String[] value = { arrEntryPojos.get(mFirstVisibleItem).getID(), "down" };
						//									entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
						//									Utility.DisLikeDialog(getActivity());
						//
						//									arrEntryPojos.remove(mFirstVisibleItem);
						//									mFirstVisibleItem = 0;
						//									if (mediaPlayer != null) {
						//										mediaPlayer.reset();
						//									}
						//									indexCurrentPlayAudio = -1;
						//
						//									entryListAdapter.notifyDataSetChanged();
						//
						//									if (arrEntryPojos.size() == 0) {
						//										textNoData.setVisibility(View.VISIBLE);
						//										textNoData.setText(getString(R.string.there_are_no_entries_yet));
						//									}
						//								}

						//							} else if (touchX < event.getX() - Utility.dpToPx(mContext, 100)) {
						//
						//								isMoveDone = true;
						//
						//								//								if (arrEntryPojos.size() > 0 && mFirstVisibleItem >= 0) {
						//								//									String[] name = { "entry", "type" };
						//								//									String[] value = { arrEntryPojos.get(mFirstVisibleItem).getID(), "up" };
						//								//									entryActionHelper.LikeDislikeEntry(name, value, preferences.getString("token", null));
						//								//									Utility.LikeDialog(getActivity());
						//								//
						//								//									arrEntryPojos.remove(mFirstVisibleItem);
						//								//									mFirstVisibleItem = 0;
						//								//									if (mediaPlayer != null) {
						//								//										mediaPlayer.reset();
						//								//									}
						//								//									indexCurrentPlayAudio = -1;
						//								//
						//								//									entryListAdapter.notifyDataSetChanged();
						//								//
						//								//									if (arrEntryPojos.size() == 0) {
						//								//										textNoData.setVisibility(View.VISIBLE);
						//								//										textNoData.setText(getString(R.string.there_are_no_entries_yet));
						//								//									}
						//								//								}
						//							}
						//						}

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


						viewHolder.textureView.setVisibility(View.GONE);
						// viewHolder.textBgGray.setVisibility(View.GONE);
						viewHolder.imageFrame.setVisibility(View.VISIBLE);

						PlayAudio(position);


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
			TextView textUserName, textDescription, textTime,textViews;
			ImageView imageFrame;
			ProgressBar progressbar;
			TextureView textureView;
			FrameLayout btnShare;
			FrameLayout btnInfo;
			ImageView ivAudioIcon;
			FrameLayout layoutComment/*layoutLike*/;
			ImageView imgUserPic;
			TextView textCommentCount,textLikeCount;
			ImageView imgPlaceHolder;
			FrameLayout flPlaceHolder;

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
//			Log.d("Mobstar","updateViewCall....");
			if (Utility.isNetworkAvailable(mContext)) {
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
							//							mediaPlayer.setLooping(true);
							//Added by khyati
							mediaPlayer.setLooping(false);
							mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									if(indexCurrentPlayAudio>0){
//										Log.d("Mobstar","onComplete....");
//										Log.d("Mobstar","updateViewCall...."+indexCurrentPlayAudio);
										if (Utility.isNetworkAvailable(mContext)) {
											new UpdateViewCountCall(arrEntryPojos.get(indexCurrentPlayAudio).getID()).start();
										} else {
											Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
											Utility.HideDialog(mContext);
										}
										mediaPlayer.seekTo(0);
										mediaPlayer.start();
									}
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
				OkayAlertDialog(sErrorMessage);
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

//		Log.v(Constant.TAG, "MobItVideoListFragment onPause");

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

		LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);

//		File folder = new File(Environment.getExternalStorageDirectory() + "/.mobstar");
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
		
	}

	private void startProfileActivity(int position){
		final Intent intent = new Intent(mContext, NewProfileActivity.class);
		final UserProfile userProfile = UserProfile.newBuilder()
				.setUserCoverImage(arrEntryPojos.get(position).getProfileCover())
				.setUserId(arrEntryPojos.get(position).getUserID())
				.setUserName(arrEntryPojos.get(position).getUserName())
				.setUserDisplayName(arrEntryPojos.get(position).getUserDisplayName())
				.setUserPic(arrEntryPojos.get(position).getProfileImage())
				.setIsMyStar(arrEntryPojos.get(position).getIsMyStar() == "1")
				.setUserTagline(arrEntryPojos.get(position).getTagline())
				.build();
		intent.putExtra(NewProfileActivity.USER, userProfile);
//					intent.putExtra("UserID", arrEntryPojos.get(position).getUserID());
//					intent.putExtra("UserName", arrEntryPojos.get(position).getUserName());
//					intent.putExtra("UserDisplayName", arrEntryPojos.get(position).getUserDisplayName());
//					intent.putExtra("UserPic", arrEntryPojos.get(position).getProfileImage());
//					intent.putExtra("UserCoverImage", arrEntryPojos.get(position).getProfileCover());
//					intent.putExtra("IsMyStar", arrEntryPojos.get(position).getIsMyStar());
//					intent.putExtra("UserTagline", arrEntryPojos.get(position).getTagline());
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}



}
