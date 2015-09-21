package com.mobstar.home;

import java.net.URLEncoder;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.home.new_home_screen.HomeVideoListBaseFragment;
import com.mobstar.utils.Utility;

public class SearchFragment extends Fragment {

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	SearchView searchView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_search, container, false);

		mFragmentManager = getChildFragmentManager();

		Utility.SendDataToGA("Search Screen", getActivity());

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		searchView = (SearchView) view.findViewById(R.id.searchView);
		searchView.setQueryHint("search");
		searchView.onActionViewExpanded();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String SearchText) {
				// TODO Auto-generated method stub
//				Log.v(Constant.TAG, "SearchText " + SearchText);
				searchView.clearFocus();

				Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
				if (!(f instanceof HomeFragment)) {
//					SearchListFragment videoListFragment = new SearchListFragment();
					HomeVideoListBaseFragment videoListFragment = new HomeVideoListBaseFragment();
					Bundle extras = new Bundle();
					extras.putBoolean("isSearchAPI", true);
					extras.putString("SearchTerm", SearchText);
					videoListFragment.setArguments(extras);
					replaceFragment(videoListFragment, "SearchListFragment");
					
//					VideoListFragment videoListFragment = new VideoListFragment();
//					Bundle extras = new Bundle();
//					extras.putBoolean("isSearchAPI", true);
//					extras.putString("SearchTerm", SearchTerm);
//					videoListFragment.setArguments(extras);
//					replaceFragment(videoListFragment, "VideoListFragment");
				}

				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
		View searchPlate = searchView.findViewById(searchPlateId);
		if (searchPlate != null) {
			searchPlate.setBackgroundColor(Color.WHITE);
			int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
			TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
			if (searchText != null) {
				searchText.setTextColor(Color.BLACK);
				searchText.setHintTextColor(Color.DKGRAY);
			}
		}

		TextView textsearch = (TextView) view.findViewById(R.id.textSearch);
		textsearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard();
				getActivity().onBackPressed();
			}
		});

		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}

	private void replaceFragment(Fragment mFragment, String fragmentName) {

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.childFragmentContent, mFragment, fragmentName);
		mFragmentTransaction.commitAllowingStateLoss();
	}
	private void hideKeyboard() {   
	    // Check if no view has focus:
	    View view = getActivity().getCurrentFocus();
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
}
