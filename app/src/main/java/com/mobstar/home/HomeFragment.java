package com.mobstar.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.adapters.CategoriesAdapter;
import com.mobstar.adapters.ContinentsAdapter;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.api.responce.*;
import com.mobstar.api.responce.Error;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.gcm.GcmIntentService;
import com.mobstar.gcm.NewEntryPush;
import com.mobstar.home.new_home_screen.VideoListBaseFragment;
import com.mobstar.pojo.CategoryPojo;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.TimeUtility;
import com.mobstar.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import com.mobstar.home.new_home_screen.HomeVideoListBaseFragment;




public class HomeFragment extends Fragment implements OnClickListener {

    private static final String LOG_TAG = HomeFragment.class.getName();
    public static final String NEW_ENTY_ACTION = "new entry";
    private Context mContext;
	private SharedPreferences preferences;
	private TextView textLatestPopular;
	private TextView textAllEntries;
	private boolean isLatest = true;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private boolean isDataLoaded = false;
	private String deepLinkedId="";
	private String sErrorMessage="";
	private ArrayList<CategoryPojo> arrCategoryPojos = new ArrayList<CategoryPojo>();
    private ImageView vCategoryButton;
    private ProgressDialog progressDialog;
    private ArrayList<Integer> listChoosenContinents;
    private ArrayList<Integer> listChoosenCategories;
    private TextView vNewEntry;
    private int mUserId;


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getArguments();
		if(extras!=null) {
			if(extras.containsKey("deepLinkedId")){
//				Log.d("mobstar","get deep linked in Homefreagment");
				deepLinkedId=extras.getString("deepLinkedId");
			}

		}


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_home, container, false);

		mContext = getActivity();

		mFragmentManager = getChildFragmentManager();

		preferences = getActivity().getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);
        mUserId = Integer.parseInt(preferences.getString("userid", "0"));

		// Ion.getDefault(mContext).configure().setLogging("Ion", Log.DEBUG);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter("upload_successful"));

		//		if (!preferences.getBoolean("welcome_is_checked", false)) {
		//			Intent intent = new Intent(mContext, WelcomeVideoActivity.class);
		//			startActivity(intent);
		//			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		//		}

		Utility.SendDataToGA("Home Screen", getActivity());
		return view;
	}

    private BroadcastReceiver mNewEntryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "mNewEntryReceiver");
            ArrayList<NewEntryPush> newEntryPushs = (ArrayList<NewEntryPush>) intent.getSerializableExtra(GcmIntentService.NEW_ENTRY_PUSH);
            showNewEntryButton(newEntryPushs);

        }
    };

    private void showNewEntryButton(ArrayList<NewEntryPush> newEntryPushs) {
        if (!isLatest)
            return;
        boolean canShow = false;
        for (NewEntryPush newEntryPush : newEntryPushs) {
            if (newEntryPush.getUserId() != mUserId)
                if (listChoosenContinents.contains(newEntryPush.getContinent()) || listChoosenContinents.isEmpty())
                    if (listChoosenCategories.contains(newEntryPush.getCategory()) || listChoosenCategories.isEmpty()) {
                        VideoListBaseFragment videoListBaseFragment = (VideoListBaseFragment) mFragmentManager.findFragmentById(R.id.childFragmentContent);
                        if (videoListBaseFragment != null) {
                            ArrayList<EntryP> entryPojos = videoListBaseFragment.getEntryAdapter().getArrEntries();
                            if (!entryPojos.isEmpty()) {
                                long timeExistEntry = entryPojos.get(0).getEntry().getCreated();
                                long timeNewEntry = newEntryPush.getTimeUpload();
                                int idExistEntry = Integer.parseInt(entryPojos.get(0).getEntry().getId());
                                int idNewEntry = newEntryPush.getId();
                                if ((timeNewEntry > timeExistEntry) && (idNewEntry > idExistEntry)) {
                                    canShow = true;
                                    break;
                                }
                            } else {
                                canShow = true;
                                break;
                            }
                        }

                        newEntryPush.getTimeUpload();
                    }


        }
        if (vNewEntry.getVisibility() == View.GONE && isLatest && canShow) {
            vNewEntry.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_top);
            animation.setDuration(1000);
            vNewEntry.startAnimation(animation);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent

//			Log.v(Constant.TAG, "upload_successful mReceiver");
            GetData("latest");
		}
	};

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		textAllEntries = (TextView) view.findViewById(R.id.textAllEntries);
		textAllEntries.setOnClickListener(this);

		textLatestPopular = (TextView) view.findViewById(R.id.textLatestPopular);
		textLatestPopular.setOnClickListener(this);

        vCategoryButton = (ImageView) view.findViewById(R.id.btn_continents_home);
        vCategoryButton.setOnClickListener(this);

        vNewEntry = (TextView) view.findViewById(R.id.new_entry_field);
        vNewEntry.setOnClickListener(this);

        if (!isDataLoaded) {
			GetData("latest");
		}

		if (isLatest) {
			textLatestPopular.setText(getString(R.string.latest));
		} else {
			textLatestPopular.setText(getString(R.string.popular));
		}
	}

	void GetData(String sLatestPopular) {
		if (Utility.isNetworkAvailable(mContext)) {
            getContinentsFilters();
            getCategoriesFilters();
			new CategoryCall().start();
		} else {
			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			//			Utility.HideDialog(mContext);
		}

		//check for deeplink EntryId
		if(deepLinkedId!=null && deepLinkedId.length()>0) {
			Log.d("mobstar","Sending deepLinkedId"+deepLinkedId);
//			VideoListFragment videoListFragment = new VideoListFragment();
//			Bundle extras = new Bundle();
//			extras.putBoolean("isEntryIdAPI", true);
//			extras.putString("deepLinkedId",deepLinkedId);
//			extras.putString("LatestORPopular", sLatestPopular);
//			videoListFragment.setArguments(extras);
			VideoListBaseFragment videoListFragment = VideoListBaseFragment.newInstance(true, deepLinkedId, sLatestPopular, null, false);
			replaceFragment(videoListFragment, "VideoListFragment");
            deepLinkedId = null;
		}
		else {
//			VideoListFragment videoListFragment = new VideoListFragment();
//			Bundle extras = new Bundle();
//			extras.putBoolean("isEntryAPI", true);
//			extras.putString("LatestORPopular", sLatestPopular);
//			videoListFragment.setArguments(extras);
			VideoListBaseFragment videoListFragment = VideoListBaseFragment.newInstance(false, null, sLatestPopular, null, true);
            replaceFragment(videoListFragment, "VideoListFragment");
        }
		isDataLoaded = true;
	}

    private void getCategoriesFilters() {
        RestClient.getInstance(mContext).getRequest(Constant.USER_CATEGORIES_FILTERS, null, new ConnectCallback<CategoriesFilterResponse>() {
            @Override
            public void onSuccess(CategoriesFilterResponse filterResponse) {
                Log.d(LOG_TAG, "CategoriesFilterResponse=" + filterResponse.getChoosenCategories().size());
                if (!filterResponse.hasError()) {
                    listChoosenCategories = filterResponse.getChoosenCategories();
                } else {
                    Log.d(LOG_TAG, "getCategoriesFilters.onFailure.error=" + filterResponse.getError());
                    listChoosenCategories = new ArrayList<Integer>();
//                    okayAlertDialog(filterResponse.getError());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(LOG_TAG, "getCategoriesFilters.onFailure=" + error);

//                okayAlertDialog(error);

            }

            @Override
            public void onServerError(com.mobstar.api.responce.Error error) {

            }
        });
    }

    private void getContinentsFilters() {
        RestClient.getInstance(mContext).getRequest(Constant.USER_CONTINENT_FILTERS, null, new ConnectCallback<ContinentFilterResponse>() {
            @Override
            public void onSuccess(ContinentFilterResponse filterResponse) {
                Log.d(LOG_TAG, "ContinentFilterResponse=" + filterResponse.getChoosenContinents().size());
                if (!filterResponse.hasError()) {
                    listChoosenContinents = filterResponse.getChoosenContinents();
                } else {
                    Log.d(LOG_TAG, "getContinentsFilters.onFailure.error=" + filterResponse.getError());
                    listChoosenContinents = new ArrayList<Integer>();
//                    okayAlertDialog(filterResponse.getError());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(LOG_TAG, "getContinentsFilters.onFailure=" + error);

//                okayAlertDialog(error);

            }

            @Override
            public void onServerError(Error error) {

            }
        });

    }


    private void replaceFragment(Fragment mFragment, String fragmentName) {

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.childFragmentContent, mFragment, fragmentName);
		mFragmentTransaction.commitAllowingStateLoss();
	}

	void LatestPopularDialog() {

		CustomTextviewBold btnLatest, btnPopular;
        final ImageButton btnClose;

		final Dialog dialog = new Dialog(getActivity(), R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_latest_popular);
		dialog.setCancelable(true);
        btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnLatest = (CustomTextviewBold) dialog.findViewById(R.id.btnLatest);
		btnLatest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            GetData("latest");
                            textLatestPopular.setText(getString(R.string.latest));
                            isLatest = true;
                            tryHideNewEntry();
                        }
                    });
                }
                dialog.dismiss();
            }
        });
		btnPopular = (CustomTextviewBold) dialog.findViewById(R.id.btnPopular);
		btnPopular.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            GetData("popular");
                            isLatest = false;
                            textLatestPopular.setText(getString(R.string.popular));
                            tryHideNewEntry();
                        }
                    });
                }
                dialog.dismiss();
            }
        });
		dialog.show();
	}


    private void showProgress(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

    private void hideProgress(){
        if (progressDialog != null)
            progressDialog.hide();
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "onClick");
        switch (v.getId()) {
            case R.id.textLatestPopular:
                LatestPopularDialog();
                break;
            case R.id.textAllEntries:
                ListView listCategory;

                final Dialog categoryDialog = new Dialog(getActivity(), R.style.DialogTheme);
                categoryDialog.setContentView(R.layout.dialog_chooser_filter);
                listCategory = (ListView) categoryDialog.findViewById(R.id.list_choose_dialog);
                ((ImageButton) categoryDialog.findViewById(R.id.btn_close_dialog_filters)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       sendNewCategoryFilter(categoryDialog);


                    }
                });
                listCategory.setAdapter(new CategoriesAdapter(getActivity(),arrCategoryPojos,listChoosenCategories));

                categoryDialog.show();
                break;

            case R.id.btn_continents_home:
                //mock
//                for (int i=0;i<choosenContinents.length;i++){
//                    listChoosenContinents.add(choosenContinents[i]);
//                }
                ListView vListConinents;

                final Dialog continentDialog = new Dialog(getActivity(), R.style.DialogTheme);
                continentDialog.setContentView(R.layout.dialog_chooser_filter);
                vListConinents = (ListView) continentDialog.findViewById(R.id.list_choose_dialog);
                ((ImageButton) continentDialog.findViewById(R.id.btn_close_dialog_filters))
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendNewContinentFilter(continentDialog);
                            }
                        });


                vListConinents.setAdapter(new ContinentsAdapter(continentDialog, listChoosenContinents));

                continentDialog.show();
                break;
            case R.id.new_entry_field:

                GetData("latest");
                tryHideNewEntry();

                break;
        }
    }

    private void sendNewCategoryFilter(final Dialog categoryDialog) {
//        if (listChoosenCategories.size() == 6)
//            listChoosenContinents.clear();
        final HashMap<String, String> params = new HashMap<>();
        JSONArray jsonArray = new JSONArray(listChoosenCategories);
        Log.d(LOG_TAG, "listChoosenCategories.jsonArray=" + jsonArray.toString());
        params.put(CategoriesFilterResponse.KEY_CATEGORIES_FILTER, jsonArray.toString());
        showProgress();
        RestClient.getInstance(mContext).postRequest(Constant.USER_CATEGORIES_FILTERS, params, new ConnectCallback<CategoriesFilterResponse>() {

            @Override
            public void onSuccess(CategoriesFilterResponse filterResponse) {
                Log.d(LOG_TAG, "CategoriesFilterResponse=" + filterResponse.getChoosenCategories().size());
                hideProgress();
                if (filterResponse.hasError()) {
                    Log.d(LOG_TAG, "categoryDialog.onSuccess.error=" + filterResponse.getError());
//                                    okayAlertDialog(object.getError());
                } else {
                    categoryDialog.dismiss();
                    onBeginVideoFragment();
                }

            }

            @Override
            public void onFailure(String error) {
                Log.d(LOG_TAG, "categoryDialog.onFailure.error=" + error);
                hideProgress();
//                                okayAlertDialog(error);
//                                showToastNotification(error);
            }

            @Override
            public void onServerError(Error error) {

            }
        });
    }

    private void sendNewContinentFilter(final Dialog continentDialog) {
        if (listChoosenContinents.size() == 6)
            listChoosenContinents.clear();
        final HashMap<String, String> params = new HashMap<>();
        JSONArray jsonArray = new JSONArray(listChoosenContinents);
        Log.d(LOG_TAG, "listChoosenContinents.jsonArray=" + jsonArray.toString());
        params.put(ContinentFilterResponse.KEY_CONTINENT_FILTER, jsonArray.toString());
        showProgress();
        RestClient.getInstance(mContext).postRequest(Constant.USER_CONTINENT_FILTERS, params, new ConnectCallback<ContinentFilterResponse>() {

            @Override
            public void onSuccess(ContinentFilterResponse filterResponse) {
                Log.d(LOG_TAG, "ContinentFilterResponse=" + filterResponse.getChoosenContinents().size());
                hideProgress();
                if (filterResponse.hasError()) {
                    Log.d(LOG_TAG, "continentDialog.onSuccess.error=" + filterResponse.getError());
//                                    okayAlertDialog(object.getError());
                } else {
                    continentDialog.dismiss();
                    onBeginVideoFragment();
                }

            }

            @Override
            public void onFailure(String error) {
                Log.d(LOG_TAG, "continentDialog.onFailure.error=" + error);
                hideProgress();
//                                okayAlertDialog(error);
//                                showToastNotification(error);
            }

            @Override
            public void onServerError(Error error) {

            }
        });
    }

    private void onBeginVideoFragment() {
//        VideoListFragment videoListFragment = new VideoListFragment();
//        Bundle extras = new Bundle();
//        extras.putBoolean("isEntryAPI", true);
//        extras.putString("LatestORPopular", "latest");
//        videoListFragment.setArguments(extras);
		VideoListBaseFragment videoListFragment = VideoListBaseFragment.newInstance(false, null, "latest", null, true);
        replaceFragment(videoListFragment, "VideoListFragment");
    }

//

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNewEntryReceiver, new IntentFilter(NEW_ENTY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mNewEntryReceiver);
    }

    public void tryHideNewEntry() {
        if (vNewEntry.getVisibility()==View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_top);
            animation.setDuration(1000);
            vNewEntry.startAnimation(animation);
            vNewEntry.setVisibility(View.GONE);
        }
    }

    class CategoryCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String Query = Constant.SERVER_URL + Constant.GET_CATEGORY;


//			Log.v(Constant.TAG, "Query " + Query);

			String response = JSONParser.getRequest(Query, preferences.getString("token", null));

			// Log.v(Constant.TAG, "EntryCall response " + response);
//			Log.d("Response is=>", response);

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

					if (jsonObject.has("categories")) {

						arrCategoryPojos.clear();

						JSONArray jsonArrayCategories;
						jsonArrayCategories = jsonObject.getJSONArray("categories");


						for (int i = 0; i < jsonArrayCategories.length(); i++) {

							JSONObject jsonObj = jsonArrayCategories.getJSONObject(i);

							if (jsonObj.has("category")) {
								JSONObject jsonObjCategory = jsonObj.getJSONObject("category");
								CategoryPojo categoryPojo = new CategoryPojo();
								categoryPojo.setID(jsonObjCategory.getString("id"));
								categoryPojo.setCategoryActive(jsonObjCategory.getBoolean("categoryActive"));
								categoryPojo.setCategoryName(jsonObjCategory.getString("categoryName"));
								categoryPojo.setCategoryDescription(jsonObjCategory.getString("categoryDescription"));
								arrCategoryPojos.add(categoryPojo);
							}
						}

					}
				}

				if (sErrorMessage != null && !sErrorMessage.equals("")) {
					handlerEntry.sendEmptyMessage(0);
				} else {
					handlerEntry.sendEmptyMessage(1);
				}

			} catch (Exception exception) {
				exception.printStackTrace();
				handlerEntry.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerEntry = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//			Utility.HideDialog(mContext);
			isDataLoaded = true;

			if (msg.what == 1) {
//				categoryAdapter.notifyDataSetChanged();

			} else {
				okayAlertDialog(sErrorMessage);

			}
		}
	};

	void okayAlertDialog(final String msg) {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

	

}
