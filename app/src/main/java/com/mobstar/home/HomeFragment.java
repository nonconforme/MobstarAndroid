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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.adapters.ContinentsAdapter;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.ContinentFilterResponse;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.pojo.CategoryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


import api.responce.ContinentFilterResponse;

public class HomeFragment extends Fragment implements OnClickListener {

    private static final String LOG_TAG = HomeFragment.class.getName();
    private Context mContext;

	SharedPreferences preferences;

	TextView textLatestPopular;
	TextView textAllEntries;

	boolean isLatest = true;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	private boolean isDataLoaded = false;
	private String deepLinkedId="";
	private String sErrorMessage="";
	private ArrayList<CategoryPojo> arrCategoryPojos = new ArrayList<CategoryPojo>();
	private CategoryAdapter categoryAdapter;
	private Dialog categoryDialog;
    private ImageView vCategoryButton;
    private int[] choosenContinents = {1,3,4};
    private ProgressDialog progressDialog;
    private ArrayList<Integer> listChoosen;


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

		categoryAdapter=new CategoryAdapter();

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
		textAllEntries.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CategoryDialog();
			}
		});

		textLatestPopular = (TextView) view.findViewById(R.id.textLatestPopular);
		textLatestPopular.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LatestPopularDialog();
			}
		});

        vCategoryButton = (ImageView) view.findViewById(R.id.btn_category_home);
        vCategoryButton.setOnClickListener(this);


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
			new CategoryCall().start();
		} else {
			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			//			Utility.HideDialog(mContext);
		}

		//check for deeplink EntryId
		if(deepLinkedId!=null && deepLinkedId.length()>0) {
			Log.d("mobstar","Sending deepLinkedId"+deepLinkedId);
			VideoListFragment videoListFragment = new VideoListFragment();
			Bundle extras = new Bundle();
			extras.putBoolean("isEntryIdAPI", true);
			extras.putString("deepLinkedId",deepLinkedId);
			extras.putString("LatestORPopular", sLatestPopular);
			videoListFragment.setArguments(extras);
			replaceFragment(videoListFragment, "VideoListFragment");
		}
		else {
			VideoListFragment videoListFragment = new VideoListFragment();
			Bundle extras = new Bundle();
			extras.putBoolean("isEntryAPI", true);
			extras.putString("LatestORPopular", sLatestPopular);
			videoListFragment.setArguments(extras);
			replaceFragment(videoListFragment, "VideoListFragment");	
		}
		isDataLoaded = true;
	}

    private void getContinentsFilters() {
        RestClient.getInstance(mContext).getRequest(Constant.USER_CONTINENT_FILTERS, null, new ConnectCallback<ContinentFilterResponse>() {
            @Override
            public void onSuccess(ContinentFilterResponse filterResponse) {
                Log.d(LOG_TAG, "ContinentFilterResponse=" + filterResponse.getChoosenContinents().size());
                if (!filterResponse.hasError()) {
                    listChoosen = filterResponse.getChoosenContinents();
                } else {
                    Log.d(LOG_TAG, "getContinentsFilters.onFailure.error=" + filterResponse.getError());
                    listChoosen = new ArrayList<Integer>();
//                    okayAlertDialog(filterResponse.getError());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(LOG_TAG, "getContinentsFilters.onFailure=" + error);

//                okayAlertDialog(error);

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

		final Dialog dialog = new Dialog(getActivity(), R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_latest_popular);
		dialog.setCancelable(true);
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
                        }
                    });
                }
                dialog.dismiss();
            }
        });
		dialog.show();
	}

	void CategoryDialog() {

		ListView listCategory;

		categoryDialog = new Dialog(getActivity(), R.style.DialogTheme);
		categoryDialog.setContentView(R.layout.dialog_category);
		listCategory = (ListView) categoryDialog.findViewById(R.id.listCategory);
		listCategory.setAdapter(categoryAdapter);

		categoryDialog.show();
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
            case R.id.btn_category_home:
                //mock
//                for (int i=0;i<choosenContinents.length;i++){
//                    listChoosen.add(choosenContinents[i]);
//                }
                ListView vListConinents;

                final Dialog continentDialog = new Dialog(getActivity(), R.style.DialogTheme);
                continentDialog.setContentView(R.layout.dialog_continets);
                vListConinents = (ListView) continentDialog.findViewById(R.id.listContinents);
                ((ImageButton) continentDialog.findViewById(R.id.btn_close_continents_filters)).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listChoosen.size() == 6)
                            listChoosen.clear();
                        final HashMap<String, String> params = new HashMap<>();
                        JSONArray jsonArray = new JSONArray(listChoosen);
                        Log.d(LOG_TAG, "listChoosen.jsonArray=" + jsonArray.toString());
                        params.put("continentFilter", jsonArray.toString());
                        showProgress();
                        RestClient.getInstance(mContext).postRequest(Constant.USER_CONTINENT_FILTERS, params, new ConnectCallback<ContinentFilterResponse>() {

                            @Override
                            public void onSuccess(ContinentFilterResponse filterResponse) {
                                Log.d(LOG_TAG, "ContinentFilterResponse=" + filterResponse.getChoosenContinents().size());
                                hideProgress();
                                if (filterResponse.hasError()){
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
                        });

                    }
                });


                vListConinents.setAdapter(new ContinentsAdapter(continentDialog, listChoosen));

                continentDialog.show();
                break;
        }
    }

    private void onBeginVideoFragment() {
        VideoListFragment videoListFragment = new VideoListFragment();
        Bundle extras = new Bundle();
        extras.putBoolean("isEntryAPI", true);
        extras.putString("LatestORPopular", "latest");
        videoListFragment.setArguments(extras);
        replaceFragment(videoListFragment, "VideoListFragment");
    }

    public class CategoryAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;

		public CategoryAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrCategoryPojos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder {
			CustomTextviewBold btnAll;
			TextView textCategoryName;
			ImageView imageIcon;
			LinearLayout llCategory;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ViewHolder viewHolder;
			final CategoryPojo categoryObj=arrCategoryPojos.get(position);
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_dialog_category, null);

				viewHolder = new ViewHolder();

				viewHolder.btnAll=(CustomTextviewBold)convertView.findViewById(R.id.btnAll);
				viewHolder.textCategoryName = (TextView) convertView.findViewById(R.id.textCategoryName);
				viewHolder.imageIcon=(ImageView)convertView.findViewById(R.id.image_icon);
				viewHolder.llCategory=(LinearLayout)convertView.findViewById(R.id.llCategory);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if(position==0){
				viewHolder.btnAll.setVisibility(View.VISIBLE);
			}
			else {
				viewHolder.btnAll.setVisibility(View.GONE);
			}

			if(categoryObj.getCategoryName()!=null && categoryObj.getCategoryName().length()>0){
				viewHolder.textCategoryName.setText(categoryObj.getCategoryName());
			}

			if(categoryObj.getCategoryDescription()!=null && categoryObj.getCategoryDescription().length()>0) {
				Picasso.with(mContext).load(categoryObj.getCategoryDescription()).placeholder(R.drawable.ic_pic_small).into(viewHolder.imageIcon);
			}
			else {
				Picasso.with(mContext).load(R.drawable.ic_pic_small).into(viewHolder.imageIcon);
			}

			//set background
			if(categoryObj.getCategoryActive()){
				viewHolder.llCategory.setBackground(getResources().getDrawable(R.drawable.oval_yellow_button_background));
			}
			else {
				viewHolder.llCategory.setBackground(getResources().getDrawable(R.drawable.btn_coming_soon));
			}

			viewHolder.btnAll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(categoryDialog!=null) {
						categoryDialog.dismiss();
					}
					onBeginVideoFragment();
				}
			});

			viewHolder.llCategory.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(categoryDialog!=null) {
						categoryDialog.dismiss();
					}
					if(categoryObj.getCategoryActive()){
						VideoListFragment videoListFragment = new VideoListFragment();
						Bundle extras = new Bundle();
						extras.putBoolean("isEntryAPI", true);
						extras.putString("categoryId",categoryObj.getID());
						extras.putString("LatestORPopular","latest");
						videoListFragment.setArguments(extras);
						replaceFragment(videoListFragment, "VideoListFragment");
					}
				}
			});

			return convertView;

		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

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
				categoryAdapter.notifyDataSetChanged();

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

	

}
