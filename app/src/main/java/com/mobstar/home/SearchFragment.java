package com.mobstar.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.mobstar.home.new_home_screen.VideoListBaseFragment;
import com.mobstar.utils.Utility;

import java.util.List;

public class SearchFragment extends Fragment implements OnClickListener, OnQueryTextListener {

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private TextView textSearch;
	private SearchView searchView;
	private Handler handler;
	private String searchText = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_search, container, false);
		mFragmentManager = getChildFragmentManager();
		Utility.SendDataToGA("Search Screen", getActivity());
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViews(view);
		setupSearch();
		setListeners();
		setupInputMode();
		handler = new Handler();
	}

	private void findViews(final View convertView){
		searchView = (SearchView) convertView.findViewById(R.id.searchView);
		textSearch = (TextView) convertView.findViewById(R.id.textSearch);
	}

	private void setListeners(){
		textSearch.setOnClickListener(this);
		searchView.setOnQueryTextListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.textSearch:
				hideKeyboard();
				getActivity().onBackPressed();
				break;
		}
	}

	private void setupSearch(){
		searchView.setQueryHint("search");
		searchView.onActionViewExpanded();
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
	}

	private void setupInputMode(){
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}

	private void replaceFragment(Fragment mFragment, String fragmentName) {

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.childFragmentContent, mFragment, fragmentName);
		mFragmentTransaction.commitAllowingStateLoss();
	}

	private void hideKeyboard() {   
	    final View view = getActivity().getCurrentFocus();
	    if (view != null) {
	        final InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		searchView.clearFocus();

		Fragment f = mFragmentManager.findFragmentById(R.id.frag_content);
		if (!(f instanceof HomeFragment)) {
			final VideoListBaseFragment videoListFragment = new VideoListBaseFragment();
			final Bundle extras = new Bundle();
			extras.putBoolean(VideoListBaseFragment.IS_SEARCH_API, true);
			extras.putString(VideoListBaseFragment.SEARCH_TERM, query);
			extras.putString(VideoListBaseFragment.LATEST_OR_POPULAR, "latest");
			videoListFragment.setArguments(extras);
			replaceFragment(videoListFragment, "SearchListFragment");
		}
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		searchText = newText.trim();
		if (searchText.equalsIgnoreCase("")) {
			removeSearchFragment();
			return false;
		}
		replaceBeginSearchFragmentPostDelay();
		return false;
	}

	private void replaceBeginSearchFragmentPostDelay() {
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 600);
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (searchText.equalsIgnoreCase(""))
				removeSearchFragment();
			else
				replaceBeginSearchFragment();
		}
	};

	private void replaceBeginSearchFragment(){
		final VideoListBaseFragment videoListFragment = new VideoListBaseFragment();
		final Bundle extras = new Bundle();
		extras.putBoolean(VideoListBaseFragment.IS_SEARCH_BEGIN_API, true);
		extras.putString(VideoListBaseFragment.SEARCH_TERM, searchText);
		extras.putString(VideoListBaseFragment.LATEST_OR_POPULAR, "latest");
		videoListFragment.setArguments(extras);
		replaceFragment(videoListFragment, "SearchListFragment");
	}

	public void removeSearchFragment(){
		List<Fragment> al = mFragmentManager.getFragments();
		if (al == null) {
			return;
		}

		for (Fragment frag : al) {
			if (frag == null)
				continue;
			mFragmentManager.beginTransaction().remove(frag).commit();
		}
	}
}
