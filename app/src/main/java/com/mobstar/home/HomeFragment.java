package com.mobstar.home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.adapters.CategoriesAdapter;
import com.mobstar.adapters.ContinentsAdapter;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.new_api_call.ProfileCall;
import com.mobstar.api.new_api_model.Category;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.api.new_api_model.Settings;
import com.mobstar.api.new_api_model.response.*;
import com.mobstar.api.new_api_model.response.CategoryResponse;
import com.mobstar.api.responce.Error;
import com.mobstar.gcm.GcmIntentService;
import com.mobstar.gcm.NewEntryPush;
import com.mobstar.home.dialogs.CategoryDialog;
import com.mobstar.home.dialogs.LatestOrPopularDialog;
import com.mobstar.home.new_home_screen.VideoListBaseFragment;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnClickListener, LatestOrPopularDialog.OnSelectLatestOrPopularListener, CategoryDialog.OnChangeCategoryListener {

    private static final String LOG_TAG = HomeFragment.class.getName();
    public static final String NEW_ENTY_ACTION = "new entry";
	private TextView textLatestPopular;
	private TextView textAllEntries;
	private boolean isLatest = true;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private boolean isDataLoaded = false;
	private String deepLinkedId="";
	private ArrayList<Category> arrCategoryPojos = new ArrayList<Category>();
    private ImageView vCategoryButton;
    private Settings userSettings;
    private TextView vNewEntry;
    private int mUserId;


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle extras = getArguments();
		if(extras != null) {
			if(extras.containsKey("deepLinkedId")){
				deepLinkedId = extras.getString("deepLinkedId");
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.fragment_home, container, false);
        findViews(view);
        setListeners();
		mFragmentManager = getChildFragmentManager();
        mUserId = Integer.parseInt(UserPreference.getUserField(getActivity(), UserPreference.USER_ID));
        if (!isDataLoaded) {
            getData("latest");
        }

        if (isLatest) {
            textLatestPopular.setText(getString(R.string.latest));
        } else {
            textLatestPopular.setText(getString(R.string.popular));
        }
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter("upload_successful"));
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
                if (userSettings.getContinentFilter().contains(newEntryPush.getContinent()) || userSettings.getContinentFilter().isEmpty())
                    if (userSettings.getCategoryFilter().contains(newEntryPush.getCategory()) || userSettings.getCategoryFilter().isEmpty()) {
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
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_top);
            animation.setDuration(1000);
            vNewEntry.startAnimation(animation);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
            getData("latest");
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
	}

    private void findViews(final View view){
        textAllEntries     = (TextView) view.findViewById(R.id.textAllEntries);
        textLatestPopular  = (TextView) view.findViewById(R.id.textLatestPopular);
        vCategoryButton    = (ImageView) view.findViewById(R.id.btn_continents_home);
        vNewEntry          = (TextView) view.findViewById(R.id.new_entry_field);
    }

    private void setListeners(){
        textAllEntries.setOnClickListener(this);
        textLatestPopular.setOnClickListener(this);
        vCategoryButton.setOnClickListener(this);
        vNewEntry.setOnClickListener(this);
    }

	void getData(String sLatestPopular) {
        getCategories();
        getUserSettings();

		if(deepLinkedId != null && deepLinkedId.length()>0) {
			VideoListBaseFragment videoListFragment = VideoListBaseFragment.newInstance(true, deepLinkedId, sLatestPopular, null, false);
			replaceFragment(videoListFragment, "VideoListFragment");
            deepLinkedId = null;
		}
		else {
			VideoListBaseFragment videoListFragment = VideoListBaseFragment.newInstance(false, null, sLatestPopular, null, true);
            replaceFragment(videoListFragment, "VideoListFragment");
        }
		isDataLoaded = true;
	}

    private void replaceFragment(Fragment mFragment, String fragmentName) {

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.childFragmentContent, mFragment, fragmentName);
		mFragmentTransaction.commitAllowingStateLoss();
	}

	private void startLatestPopularDialog() {
        final LatestOrPopularDialog dialog = new LatestOrPopularDialog(getActivity(), this);
        dialog.show();
	}

    @Override
    public void onSelectLatest() {
        getData("latest");
        textLatestPopular.setText(getString(R.string.latest));
        isLatest = true;
        tryHideNewEntry();
    }

    @Override
    public void onSelectPopular() {
        getData("popular");
        isLatest = false;
        textLatestPopular.setText(getString(R.string.popular));
        tryHideNewEntry();
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "onClick");
        switch (v.getId()) {
            case R.id.textLatestPopular:
                startLatestPopularDialog();
                break;
            case R.id.textAllEntries:
                startSelectCategoryDialog(new CategoriesAdapter(getActivity(),arrCategoryPojos, userSettings.getCategoryFilter()));
                break;

            case R.id.btn_continents_home:
                startSelectCategoryDialog(new ContinentsAdapter(getActivity(), userSettings.getContinentFilter()));
                break;
            case R.id.new_entry_field:
                getData("latest");
                tryHideNewEntry();
                break;
        }
    }

    private void startSelectCategoryDialog(final BaseAdapter adapter){
        final CategoryDialog categoryDialog = new CategoryDialog(getActivity(), adapter, this);
        categoryDialog.show();

    }

    @Override
    public void onChangeCategory(Dialog dialog) {
        postFilters(userSettings.getContinentFilter(), userSettings.getCategoryFilter(), dialog);
    }

    private void getUserSettings(){
        ProfileCall.getUserSettings(getActivity(), new ConnectCallback<UserSettingsResponse>() {
            @Override
            public void onSuccess(UserSettingsResponse object) {
                userSettings = object.getSettings();
            }
        });
    }

    private void postFilters(final List<Integer> continentFilter, final List<Integer> categoryFilter, final Dialog dialog){
        ProfileCall.setUserFilters(getActivity(), continentFilter, categoryFilter, new ConnectCallback<SuccessResponse>() {

            @Override
            public void onSuccess(SuccessResponse object) {
                dialog.dismiss();
                onBeginVideoFragment();
            }

        });
    }

    private void getCategories(){
        ProfileCall.getCategories(getActivity(), new ConnectCallback<com.mobstar.api.new_api_model.response.CategoryResponse>() {
            @Override
            public void onSuccess(CategoryResponse object) {
                arrCategoryPojos = object.getCategories();
            }
        });
    }

    private void onBeginVideoFragment() {
		VideoListBaseFragment videoListFragment = VideoListBaseFragment.newInstance(false, null, "latest", null, true);
        replaceFragment(videoListFragment, "VideoListFragment");
    }


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
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top);
            animation.setDuration(1000);
            vNewEntry.startAnimation(animation);
            vNewEntry.setVisibility(View.GONE);
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
