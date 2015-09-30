package com.mobstar.home.new_home_screen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.DownloadFileManager;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.EntriesResponse;
import com.mobstar.custom.pull_to_refresh.PullToRefreshBase;
import com.mobstar.custom.pull_to_refresh.PullToRefreshRecyclerView;
import com.mobstar.custom.recycler_view.EndlessRecyclerOnScrollListener;
import com.mobstar.custom.recycler_view.OnEndAnimationListener;
import com.mobstar.custom.recycler_view.RemoveAnimation;
import com.mobstar.player.PlayerManager;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import java.util.HashMap;

/**
 * Created by lipcha on 14.09.15.
 */
public class HomeVideoListBaseFragment extends Fragment implements PullToRefreshBase.OnRefreshListener<RecyclerView>, DownloadFileManager.DownloadCallback, OnEndAnimationListener {

    public static final String IS_SEARCH_API     = "isSearchAPI";
    public static final String SEARCH_TERM       = "SearchTerm";
    public static final String IS_MOBIT_API      = "isMobitAPI";
    public static final String IS_VOTE_API       = "isVoteAPI";
    public static final String VOTE_TYPE         = "VoteType";
    public static final String IS_ENTRY_ID_API   = "isEntryIdAPI";
    public static final String DEEP_LINKED_ID    = "deepLinkedId";
    public static final String LATEST_OR_POPULAR = "LatestORPopular";
    public static final String CATEGORY_ID       = "categoryId";
    public static final String IS_ENTRY_IPI      = "isEntryAPI";
    private static final String LOG_TAG = HomeVideoListBaseFragment.class.getName();

    private boolean isSearchAPI, isMobitAPI, isVoteAPI, isEntryIdAPI, isEntryAPI;
    private String SearchTerm, deeplinkEntryId, LatestORPopular, CategoryId, VoteType;
    protected TextView textNoData;
    private SharedPreferences preferences;

    protected RecyclerViewAdapter entryAdapter;
    protected RecyclerView recyclerView;
    protected PullToRefreshRecyclerView pullToRefreshRecyclerView;
    protected DownloadFileManager downloadFileManager;

    public static HomeVideoListBaseFragment newInstance(final boolean isEntryIdAPI, final String deepLinkedId, final String sLatestPopular, final String categoryId, boolean isEntryAPI) {
        final HomeVideoListBaseFragment baseFragment = new HomeVideoListBaseFragment();
        final Bundle args = new Bundle();
        args.putBoolean(IS_ENTRY_ID_API, isEntryIdAPI);
        args.putBoolean(IS_ENTRY_IPI, isEntryAPI);
        if (deepLinkedId != null)
            args.putString(DEEP_LINKED_ID, deepLinkedId);
        if (sLatestPopular != null)
            args.putString(LATEST_OR_POPULAR, sLatestPopular);
        if (categoryId != null)
            args.putString(CATEGORY_ID, categoryId);
        baseFragment.setArguments(args);
        return baseFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.home_video_list_base_fragment, container, false);
        findViews(inflatedView);
        preferences = getActivity().getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);
        getArgs();
        Utility.ShowProgressDialog(getActivity(), getString(R.string.loading));
        createEntryList();
        getEntryRequest(1);
        return inflatedView;
    }

    private void findViews(final View inflatedView) {
        textNoData = (TextView) inflatedView.findViewById(R.id.textNoData);
        pullToRefreshRecyclerView = (PullToRefreshRecyclerView) inflatedView.findViewById(R.id.pullToRefreshRecyclerView);
    }

    protected void getEntryRequest(final int pageNo) {
        textNoData.setVisibility(View.GONE);
        final HashMap<String, String> params = new HashMap<>();
        String url = Constant.GET_ENTRY;
        if (isEntryIdAPI) {
            if (deeplinkEntryId != null)
                url = url + deeplinkEntryId;
//            Query = Constant.SERVER_URL + Constant.GET_ENTRY  + deeplinkEntryId;
        } else if (isSearchAPI) {
            url = Constant.SEARCH_ENTRY;
            params.put("term", SearchTerm);
            params.put("page", Integer.toString(pageNo));
            params.put("orderBy", LatestORPopular);
//            Query = Constant.SERVER_URL + Constant.SEARCH_ENTRY + "?term=" + SearchTerm;
        } else if (isEntryAPI) {
            if (CategoryId != null && CategoryId.length() > 0) {
                params.put("excludeVotes", "true");
                params.put("orderBy", LatestORPopular);
                params.put("category", CategoryId);
                params.put("page", Integer.toString(pageNo));
//                Query = Constant.SERVER_URL + Constant.ENTRY + "?excludeVotes=true&orderBy=" + LatestORPopular +"&category="+CategoryId+ "&page=" + pageNo;
            } else {
                params.put("excludeVotes", "true");
                params.put("orderBy", LatestORPopular);
                params.put("page", Integer.toString(pageNo));
//                Query = Constant.SERVER_URL + Constant.ENTRY + "?excludeVotes=true&orderBy=" + LatestORPopular + "&page=" + pageNo;
            }

        } else if (isVoteAPI) {
            if (VoteType.equals("all")) {
                url = Constant.VOTE;
                params.put("user", preferences.getString("userid", "0"));
                params.put("page", Integer.toString(pageNo));
//                Query = Constant.SERVER_URL + Constant.VOTE + "?user=" + preferences.getString("userid", "0") + "&page=" + pageNo;
            } else {
                url = Constant.VOTE;
                params.put("type", VoteType);
                params.put("user", preferences.getString("userid", "0"));
                params.put("page", Integer.toString(pageNo));
//                Query = Constant.SERVER_URL + Constant.VOTE + "?type=" + VoteType + "&user=" + preferences.getString("userid", "0") + "&page=" + pageNo;
            }
        }
        getEntry(url, params, pageNo);
    }

    protected void getEntry(final String url, final HashMap<String, String> params, final int pageNo){
        RestClient.getInstance(getActivity()).getRequest(url, params, new ConnectCallback<EntriesResponse>() {

            @Override
            public void onSuccess(EntriesResponse object) {
                if (pageNo == 1) {
                    entryAdapter.setArrEntryes(object.getArrEntry());
                    endlessRecyclerOnScrollListener.reset();
                    downloadFirstFile();
                } else {
                    entryAdapter.addArrEntries(object.getArrEntry());
                }
                if (object.hasNextPage())
                    endlessRecyclerOnScrollListener.existNextPage();
                refreshEntryList();
                Utility.HideDialog(getActivity());
                pullToRefreshRecyclerView.onRefreshComplete();
                setNoEntriesMessage();
            }

            @Override
            public void onFailure(String error) {
                Log.d(LOG_TAG,"http request get:getEntryRequest.onFailure.error="+error);
                endlessRecyclerOnScrollListener.onFailedLoading();
                pullToRefreshRecyclerView.onRefreshComplete();
                Utility.HideDialog(getActivity());
            }
        });
    }

    private void setNoEntriesMessage(){
        if (entryAdapter.getItemCount() != 0)
            return;
        textNoData.setVisibility(View.VISIBLE);
        if (isSearchAPI){
            textNoData.setText(getString(R.string.nothinh_found_for) + " \"" + SearchTerm + "\"");
        }else
            textNoData.setText(getString(R.string.there_are_no_entries_yet));
    }

    private void downloadFirstFile() {
        if (entryAdapter.getItemCount() == 0 || entryAdapter.getEntry(0).getType() == null)
            return;
        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    public void run() {
                        switch (entryAdapter.getEntry(0).getType()) {
                            case "audio":
                                downloadFileManager.downloadFile(entryAdapter.getEntry(0).getAudioLink(), 0);
                                break;
                            case "video":
                                downloadFileManager.downloadFile(entryAdapter.getEntry(0).getVideoLink(), 0);
                                break;
                        }
                    }
                }, 500);
    }

    protected void refreshEntryList() {
        PlayerManager.getInstance().tryToPauseAll();
        entryAdapter.notifyDataSetChanged();
        endlessRecyclerOnScrollListener.onScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE);
        PlayerManager.getInstance().tryToPauseAll();
    }

    protected void createEntryList() {
        pullToRefreshRecyclerView.setOnRefreshListener(this);
        recyclerView = pullToRefreshRecyclerView.getRefreshableView();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new RemoveAnimation(this));
        entryAdapter = new RecyclerViewAdapter((BaseActivity) getActivity());
        recyclerView.setAdapter(entryAdapter);
        downloadFileManager = new DownloadFileManager(getActivity(), this);
        endlessRecyclerOnScrollListener.setLinearLayoutManager((LinearLayoutManager) recyclerView.getLayoutManager());
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);


    }

    protected EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadMore(int currentPage) {
            Utility.ShowProgressDialog(getActivity(), getString(R.string.loading));
            getEntryRequest(currentPage);
        }

        @Override
        public void onLoadNewFile(int currentPosition, int oldPosition) {
            Log.d("entryitem", "onLoadNewFile.pos=" + currentPosition);
//                entryAdapter.getEntryAtPosition(oldPosition).hideProgressBar();
            if (entryAdapter.getEntryAtPosition(currentPosition) != null) {
                final String type = entryAdapter.getEntryAtPosition(currentPosition).getEntryPojo().getType();
                if (type != null && !type.equals("image"))
                    entryAdapter.getEntryAtPosition(currentPosition).showProgressBar();
            }
            PlayerManager.getInstance().standardizePrevious();
            PlayerManager.getInstance().stopPlayer();
            cancelDownloadFile(oldPosition);
            downloadFile(currentPosition);
        }
    };

    @Override
    public void onRemoveItemAnimationEnd() {
        endlessRecyclerOnScrollListener.setDelFlag(true);
        refreshEntryList();
    }

    @Override
    public void onDownload(String filePath, int position) {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final int topVisiblePosition = EndlessRecyclerOnScrollListener.getTopVisiblePosition(recyclerView, linearLayoutManager);
        if (topVisiblePosition == -1)
            return;
        if (topVisiblePosition == position) {
            final EntryItem entryItem = entryAdapter.getEntryAtPosition(position);
            if (entryItem != null) {
                PlayerManager.getInstance().init(getActivity(), entryItem, filePath);
                PlayerManager.getInstance().tryToPlayNew();
            }
        }
    }

    @Override
    public void onFailed() {

    }

    protected void cancelDownloadFile(int cancelPosition) {
        if (cancelPosition == -1 || cancelPosition >= entryAdapter.getItemCount() || entryAdapter.getEntry(cancelPosition).getType() == null)
            return;
        switch (entryAdapter.getEntry(cancelPosition).getType()) {
            case "audio":
                downloadFileManager.cancelFile(entryAdapter.getEntry(cancelPosition).getAudioLink());
                break;
            case "video":
                downloadFileManager.cancelFile(entryAdapter.getEntry(cancelPosition).getVideoLink());
                break;
        }
    }

    protected void downloadFile(int currentPosition) {
        if (entryAdapter.getEntry(currentPosition) == null || entryAdapter.getEntry(currentPosition).getType() == null)
            return;
        switch (entryAdapter.getEntry(currentPosition).getType()) {
            case "audio":
                downloadFileManager.downloadFile(entryAdapter.getEntry(currentPosition).getAudioLink(), currentPosition);
                break;
            case "video":
                downloadFileManager.downloadFile(entryAdapter.getEntry(currentPosition).getVideoLink(), currentPosition);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        getEntryRequest(1);
    }

    protected void getArgs() {
        Bundle extras = getArguments();
        if (extras != null) {
            if (extras.containsKey(IS_SEARCH_API)) {
                isSearchAPI = extras.getBoolean(IS_SEARCH_API);

                if (extras.containsKey(SEARCH_TERM)) {
                    SearchTerm = extras.getString(SEARCH_TERM);
                }

                if (extras.containsKey(LATEST_OR_POPULAR))
                    LatestORPopular = extras.getString(LATEST_OR_POPULAR);
            }
            if (extras.containsKey(IS_MOBIT_API)) {
                isMobitAPI = extras.getBoolean(IS_MOBIT_API);

            }
            if (extras.containsKey(IS_ENTRY_ID_API)) {
                isEntryIdAPI = extras.getBoolean(IS_ENTRY_ID_API);
                deeplinkEntryId = extras.getString(DEEP_LINKED_ID);

            }
            if (extras.containsKey(IS_ENTRY_IPI)) {
                isEntryAPI = extras.getBoolean(IS_ENTRY_IPI);
                if (extras.containsKey(LATEST_OR_POPULAR)) {
                    LatestORPopular = extras.getString(LATEST_OR_POPULAR);
                }

                if (extras.containsKey(CATEGORY_ID)) {
                    CategoryId = extras.getString(CATEGORY_ID);
                }
            }
            if (extras.containsKey(IS_VOTE_API)) {
                isVoteAPI = extras.getBoolean(IS_VOTE_API);

                if (extras.containsKey(VOTE_TYPE)) {
                    VoteType = extras.getString(VOTE_TYPE);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PlayerManager.getInstance().stopPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        endlessRecyclerOnScrollListener.resetCurrentTopItem();
        refreshEntryList();
    }
}
