package com.mobstar.home.new_home_screen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.DownloadFileManager;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.EntriesResponse;
import com.mobstar.custom.pull_to_refresh.PullToRefreshBase;
import com.mobstar.custom.pull_to_refresh.PullToRefreshRecyclerView;
import com.mobstar.custom.recycler_view_animation.LandingAnimator;
import com.mobstar.player.PlayerManager;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lipcha on 14.09.15.
 */
public class HomeVideoListBaseFragment extends Fragment implements PullToRefreshBase.OnRefreshListener<RecyclerView>,DownloadFileManager.DownloadCallback {

    public static final String IS_ENTRY_ID_API = "isEntryIdAPI";
    public static final String DEEP_LINKED_ID = "deepLinkedId";
    public static final String LATEST_OR_POPULAR = "LatestORPopular";
    public static final String CATEGORY_ID = "categoryId";
    public static final String IS_ENTRY_IPI = "isEntryAPI";

    private boolean isSearchAPI, isMobitAPI, isVoteAPI, isEntryIdAPI, isEntryAPI;
    private String SearchTerm, deeplinkEntryId, LatestORPopular, CategoryId, VoteType;
    private SharedPreferences preferences;
    private ArrayList<EntryPojo> arrEntryPojos = new ArrayList<>();


    private RecyclerViewAdapter entryAdapter;
    private RecyclerView recyclerView;
    private PullToRefreshRecyclerView pullToRefreshRecyclerView;
    private DownloadFileManager downloadFileManager;

    public static HomeVideoListBaseFragment newInstance(final boolean isEntryIdAPI, final String deepLinkedId, final String sLatestPopular, final String categoryId, boolean isEntryAPI){
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
        getEntryRequest(0);
        return inflatedView;
    }

    private void findViews(final View inflatedView){
//        recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerView);
        pullToRefreshRecyclerView = (PullToRefreshRecyclerView) inflatedView.findViewById(R.id.pullToRefreshRecyclerView);
    }

    private void getEntryRequest(final int pageNo){
        final HashMap<String, String> params = new HashMap<>();
        String url = Constant.GET_ENTRY;
        if (isEntryIdAPI) {
            if (deeplinkEntryId != null)
                url = url + deeplinkEntryId;
//            Query = Constant.SERVER_URL + Constant.GET_ENTRY  + deeplinkEntryId;
        }else if (isSearchAPI) {
            url = url + Constant.SEARCH_ENTRY;
            params.put("term", SearchTerm);
//            Query = Constant.SERVER_URL + Constant.SEARCH_ENTRY + "?term=" + SearchTerm;
        } else if (isEntryAPI) {
            if(CategoryId != null && CategoryId.length() > 0){
                params.put("excludeVotes", "true");
                params.put("orderBy", LatestORPopular);
                params.put("category", CategoryId);
                params.put("page", Integer.toString(pageNo));
//                Query = Constant.SERVER_URL + Constant.ENTRY + "?excludeVotes=true&orderBy=" + LatestORPopular +"&category="+CategoryId+ "&page=" + pageNo;
            }
            else {
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
        RestClient.getInstance(getActivity()).getRequest(url, params, new ConnectCallback<EntriesResponse>() {

            @Override
            public void onSuccess(EntriesResponse object) {
                if (pageNo == 0) {
                    arrEntryPojos = new ArrayList<EntryPojo>();
                    entryAdapter.setArrEntryes(arrEntryPojos);
                    downloadFirstFile();
                }
                arrEntryPojos.addAll(object.getArrEntry());
                refreshEntryList();
                Utility.HideDialog(getActivity());
                pullToRefreshRecyclerView.onRefreshComplete();
            }

            @Override
            public void onFailure(String error) {
                pullToRefreshRecyclerView.onRefreshComplete();
            }
        });
    }

    private void downloadFirstFile(){
        if (arrEntryPojos.size() == 0)
            return;
        switch (arrEntryPojos.get(0).getType()) {
            case "audio":
                downloadFileManager.downloadFile(arrEntryPojos.get(0).getAudioLink(), 0);
                break;
            case "video":
                downloadFileManager.downloadFile(arrEntryPojos.get(0).getVideoLink(), 0);
                break;
        }
    }

    private void refreshEntryList(){
        entryAdapter.notifyDataSetChanged();
    }

    private void createEntryList(){
        pullToRefreshRecyclerView.setOnRefreshListener(this);
        recyclerView = pullToRefreshRecyclerView.getRefreshableView();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new LandingAnimator());
        entryAdapter = new RecyclerViewAdapter(arrEntryPojos, (BaseActivity) getActivity());
        recyclerView.setAdapter(entryAdapter);
        downloadFileManager = new DownloadFileManager(getActivity(), this);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int currentPage) {
                Utility.ShowProgressDialog(getActivity(), getString(R.string.loading));
                getEntryRequest(currentPage);
            }

            @Override
            public void onLoadNewFile(int currentPosition, int oldPosition) {
                cancelDownloadFile(oldPosition);
                downloadFile(currentPosition);
            }
        });
    }

    @Override
    public void onDownload(String filePath, int position) {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (EndlessRecyclerOnScrollListener.getTopVisiblePosition(recyclerView, linearLayoutManager) == position){
            final EntryItem entryItem = entryAdapter.getEntryAtPosition(position);
            if (entryItem != null) {
                PlayerManager.getInstance().init(getActivity(), entryItem, filePath);
                boolean sdsvs = PlayerManager.getInstance().tryToPlayNew();
                Log.d("tag","" +  sdsvs);
            }
        }
    }

    @Override
    public void onFailed() {

    }

    private void cancelDownloadFile(int cancelPosition){
        switch (arrEntryPojos.get(cancelPosition).getType()) {
            case "audio":
                downloadFileManager.cancelFile(arrEntryPojos.get(cancelPosition).getAudioLink());
                break;
            case "video":
                downloadFileManager.cancelFile(arrEntryPojos.get(cancelPosition).getVideoLink());
                break;
        }
    }

    private void downloadFile(int currentPosition){

        switch (arrEntryPojos.get(currentPosition).getType()) {
            case "audio":
                downloadFileManager.downloadFile(arrEntryPojos.get(currentPosition).getAudioLink(), currentPosition);
                break;
            case "video":
                downloadFileManager.downloadFile(arrEntryPojos.get(currentPosition).getVideoLink(), currentPosition);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        getEntryRequest(0);
    }

    private void getArgs(){
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
            if (extras.containsKey(IS_ENTRY_ID_API)) {
                isEntryIdAPI = extras.getBoolean(IS_ENTRY_ID_API);
                deeplinkEntryId = extras.getString(DEEP_LINKED_ID);

            }
            if (extras.containsKey(IS_ENTRY_IPI)) {
                isEntryAPI = extras.getBoolean(IS_ENTRY_IPI);
                if (extras.containsKey(LATEST_OR_POPULAR)) {
                    LatestORPopular = extras.getString(LATEST_OR_POPULAR);
                }

                if(extras.containsKey(CATEGORY_ID)) {
                    CategoryId = extras.getString(CATEGORY_ID);
                }
            }
            if (extras.containsKey("isVoteAPI")) {
                isVoteAPI = extras.getBoolean("isVoteAPI");

                if (extras.containsKey("VoteType")) {
                    VoteType = extras.getString("VoteType");
                }
            }
        }
    }
}
