package com.mobstar.home.new_home_screen;

import android.app.Activity;
import android.content.Intent;
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
import com.mobstar.home.HomeFragment;
import com.mobstar.home.notification.SingleEntryActivity;
import com.mobstar.home.youtube.YouTubePlayerActivity;
import com.mobstar.player.PlayerManager;
import com.mobstar.player.YouTubePlayerManager;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lipcha on 14.09.15.
 */
public class VideoListBaseFragment extends Fragment implements PullToRefreshBase.OnRefreshListener<RecyclerView>, DownloadFileManager.DownloadCallback, OnEndAnimationListener, SwipeRefreshAction {

    public static final String ID                = "id";
    public static final String IS_SEARCH_API     = "isSearchAPI";
    public static final String SEARCH_TERM       = "searchTerm";
    public static final String IS_MOBIT_API      = "isMobitAPI";
    public static final String IS_VOTE_API       = "isVoteAPI";
    public static final String VOTE_TYPE         = "voteType";
    public static final String IS_ENTRY_ID_API   = "isEntryIdAPI";
    public static final String DEEP_LINKED_ID    = "deepLinkedId";
    public static final String LATEST_OR_POPULAR = "latestORPopular";
    public static final String CATEGORY_ID       = "categoryId";
    public static final String IS_ENTRY_IPI      = "isEntryAPI";
    public static final String IS_SEARCH_BEGIN_API = "is_search_begin_api";
    private static final String LOG_TAG = VideoListBaseFragment.class.getName();

    private boolean isSearchAPI, isMobitAPI, isVoteAPI, isEntryIdAPI, isEntryAPI, isSearchBeginApi;
    private String searchTerm, deeplinkEntryId, latestORPopular, categoryId, voteType;
    protected TextView textNoData;
    private SharedPreferences preferences;

    protected RecyclerViewAdapter entryAdapter;
    protected RecyclerView recyclerView;
    protected PullToRefreshRecyclerView pullToRefreshRecyclerView;
    protected DownloadFileManager downloadFileManager;

    public static VideoListBaseFragment newInstance(final boolean isEntryIdAPI, final String deepLinkedId, final String sLatestPopular, final String categoryId, boolean isEntryAPI) {
        final VideoListBaseFragment baseFragment = new VideoListBaseFragment();
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
        pullToRefreshRecyclerView.setEnablePullToRefresh(getEnablePulToRefreshAction());
        preferences = getActivity().getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);
        getArgs();
        Utility.ShowProgressDialog(getActivity(), getString(R.string.loading));
        createEntryList();
        entryAdapter.setEnableSwipeAction(getEnableSwipeCardAction());
        getEntryRequest(1);
        return inflatedView;
    }

    private void findViews(final View inflatedView) {
        textNoData = (TextView) inflatedView.findViewById(R.id.textNoData);
        pullToRefreshRecyclerView = (PullToRefreshRecyclerView) inflatedView.findViewById(R.id.pullToRefreshRecyclerView);
    }

    // override this method for enable/disable swipe action
    public boolean getEnableSwipeCardAction(){
        return true;
    }

    // override this method for enable/disable pull to refresh action
    public boolean getEnablePulToRefreshAction(){
        return true;
    }

    protected void getEntryRequest(final int pageNo) {
        textNoData.setVisibility(View.GONE);
        final HashMap<String, String> params = new HashMap<>();
        String url = Constant.GET_ENTRY;
        if (isEntryIdAPI) {
            startSingleViewActivity();
            isEntryIdAPI = false;
            isEntryAPI = true;
            params.put("excludeVotes", "true");
            params.put("orderBy", "latest");
            params.put("page", Integer.toString(pageNo));
        } else if (isSearchAPI) {
            url = Constant.SEARCH_ENTRY;
            if (searchTerm.equalsIgnoreCase("")){
                pullToRefreshRecyclerView.onRefreshComplete();
                setNoEntriesMessage();
                return;
            }
            params.put("term", searchTerm);
            params.put("page", Integer.toString(pageNo));
//            params.put("orderBy", latestORPopular);
        } else if (isEntryAPI) {
            if (categoryId != null && categoryId.length() > 0) {
                params.put("excludeVotes", "true");
                params.put("orderBy", latestORPopular);
                params.put("category", categoryId);
                params.put("page", Integer.toString(pageNo));
            } else {
                params.put("excludeVotes", "true");
                params.put("orderBy", latestORPopular);
                params.put("page", Integer.toString(pageNo));
            }

        } else if (isVoteAPI) {
            if (voteType.equals("all")) {
                url = Constant.VOTE;
                params.put("user", preferences.getString("userid", "0"));
                params.put("page", Integer.toString(pageNo));
            } else {
                url = Constant.VOTE;
                params.put("type", voteType);
                params.put("user", preferences.getString("userid", "0"));
                params.put("page", Integer.toString(pageNo));
            }
        }else if (isSearchBeginApi){
            if (searchTerm.equalsIgnoreCase("")){
                pullToRefreshRecyclerView.onRefreshComplete();
                setNoEntriesMessage();
                return;
            }
            url = Constant.SEARCH_BEGIN + searchTerm;
            params.put("page", Integer.toString(pageNo));
        }
        getEntry(url, params, pageNo);
    }

    protected void getEntry(final String url, final HashMap<String, String> params, final int pageNo){
        RestClient.getInstance(getActivity()).getRequest(url, params, new ConnectCallback<EntriesResponse>() {

            @Override
            public void onSuccess(EntriesResponse object) {
                if (getActivity() == null)
                    return;
                ArrayList<EntryPojo> arrEntry = object.getArrEntry();
                if (pageNo == 1) {
                    if (!(!arrEntry.isEmpty() &&
                            (arrEntry.get(0).getID().isEmpty() || arrEntry.get(0).getID().equals("null")))) {
                        entryAdapter.setArrEntryes(arrEntry);
                        endlessRecyclerOnScrollListener.reset();
                        downloadFirstFile();
                    }
                } else {
                    entryAdapter.addArrEntries(arrEntry);
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
                if (getActivity() == null)
                    return;
                Log.d(LOG_TAG, "http request get:getEntryRequest.onFailure.error=" + error);
                endlessRecyclerOnScrollListener.onFailedLoading();
                pullToRefreshRecyclerView.onRefreshComplete();
                Utility.HideDialog(getActivity());
                setNoEntriesMessage();
            }
        });
    }

    private void startSingleViewActivity(){
        final Intent intent = new Intent(getActivity(), SingleEntryActivity.class);
        intent.putExtra(SingleEntryActivity.IS_ENTRY_ID_API, true);
        intent.putExtra(SingleEntryActivity.ID, deeplinkEntryId);
        startActivity(intent);
    }

    private void setNoEntriesMessage(){
        if (entryAdapter.getItemCount() != 0)
            return;
        textNoData.setVisibility(View.VISIBLE);
        if (isSearchAPI){
            textNoData.setText(getString(R.string.nothinh_found_for) + " \"" + searchTerm + "\"");
        }else
            textNoData.setText(getString(R.string.there_are_no_entries_yet));
    }

    private void downloadFirstFile() {
        if (entryAdapter.getItemCount() == 0 || entryAdapter.getEntry(0) == null || entryAdapter.getEntry(0).getType() == null)
            return;
        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    public void run() {
                        final EntryPojo entryPojo = entryAdapter.getEntry(0);
                        if (entryPojo == null)
                            return;
                        switch (entryPojo.getType()) {
                            case "audio":
                                downloadFileManager.downloadFile(entryPojo.getAudioLink(), 0);
                                break;
                            case "video":
                                downloadFileManager.downloadFile(entryPojo.getVideoLink(), 0);
                                break;
                            case "video_youtube":
//                                downloadFile(0);
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
                if (type != null && !type.equals("image") && !type.equals("video_youtube"))
                    entryAdapter.getEntryAtPosition(currentPosition).showProgressBar();
            }
            PlayerManager.getInstance().standardizePrevious();
            PlayerManager.getInstance().stopPlayer();
            cancelDownloadFile(oldPosition);
            downloadFile(currentPosition);
        }

        @Override
        public void onScrolled() {
            onScrolledList();
        }
    };

    protected void onScrolledList(){

    }

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
                PlayerManager.getInstance().init(getActivity(), entryItem, filePath, entryAdapter);
                PlayerManager.getInstance().tryToPlayNew();
            }
        }
    }

    @Override
    public void onFailed() {

    }

    protected void cancelDownloadFile(int cancelPosition) {
        if(cancelPosition == -1 || cancelPosition >= entryAdapter.getItemCount())
            return;
        final EntryPojo entryPojo = entryAdapter.getEntry(cancelPosition);
        if (entryPojo == null || entryPojo.getType() == null)
            return;
        switch (entryPojo.getType()) {
            case "audio":
                downloadFileManager.cancelFile(entryPojo.getAudioLink());
                break;
            case "video":
                downloadFileManager.cancelFile(entryPojo.getVideoLink());
                break;
            case "video_youtube":
//                YouTubePlayerManager.getInstance().cancelPlayer((BaseActivity) getActivity());
                break;
        }
    }

    protected void downloadFile(int currentPosition) {
        final EntryPojo entryPojo = entryAdapter.getEntry(currentPosition);
        if (entryPojo == null || entryPojo.getType() == null)
            return;
        switch (entryPojo.getType()) {
            case "audio":
                downloadFileManager.downloadFile(entryPojo.getAudioLink(), currentPosition);
                break;
            case "video":
                downloadFileManager.downloadFile(entryPojo.getVideoLink(), currentPosition);
                break;
            case "video_youtube":
//                if (entryAdapter.getEntryAtPosition(currentPosition) != null)
//                    YouTubePlayerManager.getInstance().initialize((BaseActivity)getActivity(), entryPojo.getVideoLink(), entryAdapter.getEntryAtPosition(currentPosition).getContainerPlayer());
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        tryHideNewEntry();
        getEntryRequest(1);
    }

    private void tryHideNewEntry() {
        HomeFragment homeFragment = null;
        try {
            homeFragment = (HomeFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_content);
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
        if (homeFragment != null) {
            homeFragment.tryHideNewEntry();
        }
    }

    protected void getArgs() {
        final Bundle extras = getArguments();
        if (extras != null) {
            if (extras.containsKey(IS_SEARCH_API)) {
                isSearchAPI = extras.getBoolean(IS_SEARCH_API);

                if (extras.containsKey(SEARCH_TERM)) {
                    searchTerm = extras.getString(SEARCH_TERM);
                }

                if (extras.containsKey(LATEST_OR_POPULAR))
                    latestORPopular = extras.getString(LATEST_OR_POPULAR);
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
                    latestORPopular = extras.getString(LATEST_OR_POPULAR);
                }

                if (extras.containsKey(CATEGORY_ID)) {
                    categoryId = extras.getString(CATEGORY_ID);
                }
            }
            if (extras.containsKey(IS_VOTE_API)) {
                isVoteAPI = extras.getBoolean(IS_VOTE_API);

                if (extras.containsKey(VOTE_TYPE)) {
                    voteType = extras.getString(VOTE_TYPE);
                }
            }
            if (extras.containsKey(IS_SEARCH_BEGIN_API)){
                isSearchBeginApi = true;
                if (extras.containsKey(SEARCH_TERM)) {
                    searchTerm = extras.getString(SEARCH_TERM);
                }

                if (extras.containsKey(LATEST_OR_POPULAR))
                    latestORPopular = extras.getString(LATEST_OR_POPULAR);
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

    public void resetBundleExtra(){
        isSearchAPI = false;
        searchTerm = null;
        latestORPopular = null;
        isMobitAPI = false;
        isEntryIdAPI = false;
        deeplinkEntryId = null;
        isEntryAPI = false;
        categoryId = null;
        isVoteAPI = false;
        voteType = null;
    }

    public void setIsVoteApi(boolean isVote){
        isVoteAPI = isVote;
    }

    public void setVoteType(final String _voteType){
        voteType = _voteType;
    }

    public void resetAndLoadFirstPage(){
        getEntryRequest(1);
        entryAdapter.clearArrayEntry();
        Utility.ShowProgressDialog(getActivity(), getString(R.string.loading));
    }

    public RecyclerViewAdapter getEntryAdapter() {
        return entryAdapter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case YouTubePlayerActivity.REMOVE_ENTRY:
                    final int removePosition = data.getIntExtra(YouTubePlayerActivity.ENTRY_POSITION, -1);
                    if (removePosition != -1){
                        getEntryAdapter().onRemoveEntry(removePosition);
                    }
                    break;

            }
        }
    }

}
