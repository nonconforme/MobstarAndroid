package com.mobstar.home.new_home_screen.profile;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobstar.BaseActivity;
import com.mobstar.api.DownloadFileManager;
import com.mobstar.custom.recycler_view.RemoveAnimation;
import com.mobstar.custom.recycler_view.sticky_recycler_view.StickyHeadersTouchListener;
import com.mobstar.home.new_home_screen.EntryItem;
import com.mobstar.home.new_home_screen.HomeVideoListBaseFragment;
import com.mobstar.player.PlayerManager;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.HashMap;

/**
 * Created by lipcha on 22.09.15.
 */
public class ProfileFragment extends HomeVideoListBaseFragment implements EntryItem.OnChangeEntryListener {


    private UserProfile user;
    private int uploadPosition = -1;
    private int profilePosition = -1;
    private boolean isNotification = false;

    public static final ProfileFragment getInstance(final UserProfile userData, final boolean isNotification){
        final ProfileFragment profileFragment = new ProfileFragment();
        final Bundle args = new Bundle();
        args.putSerializable(NewProfileActivity.USER, userData);
        args.putBoolean(NewProfileActivity.IS_NOTIFICATION, isNotification);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void getEntryRequest(int pageNo) {
        final HashMap<String, String> params = new HashMap<>();
        if (user != null && user.getUserId() != null)
            params.put("user", user.getUserId());
        params.put("page", Integer.toString(pageNo));
        textNoData.setVisibility(View.GONE);
        if (isNotification)
            getEntry(Constant.GET_ENTRY + user.getEntryId(),null,1);
        else
            getEntry(Constant.MIX_ENTRY, params, pageNo);
    }

    @Override
    protected void getArgs() {
        final Bundle args = getArguments();
        if (args.containsKey(NewProfileActivity.USER))
            user = (UserProfile) args.getSerializable(NewProfileActivity.USER);
        if (args.containsKey(NewProfileActivity.IS_NOTIFICATION))
            isNotification = args.getBoolean(NewProfileActivity.IS_NOTIFICATION);


    }

    @Override
    protected void downloadFile(int currentPosition) {
        if (currentPosition <= 0 || entryAdapter.getArrEntries().size() == 0)
            return;
        super.downloadFile(currentPosition - 1);
    }

    @Override
    public void onDownload(String filePath, int position) {
        super.onDownload(filePath, position + 1);
    }

    @Override
    protected void cancelDownloadFile(int cancelPosition) {
        if (cancelPosition <= 0 || entryAdapter.getArrEntries().size() == 0)
            return;
        super.cancelDownloadFile(cancelPosition - 1);
    }

    @Override
    protected void createEntryList() {
        pullToRefreshRecyclerView.setOnRefreshListener(this);
        recyclerView = pullToRefreshRecyclerView.getRefreshableView();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new RemoveAnimation(this));
        entryAdapter = new ProfileEntryAdapter((BaseActivity) getActivity(), user);
        recyclerView.setAdapter(entryAdapter);
        downloadFileManager = new DownloadFileManager(getActivity(), this);
        endlessRecyclerOnScrollListener.setLinearLayoutManager((LinearLayoutManager) recyclerView.getLayoutManager());
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);

        initStickyHeader();
    }

    private void initStickyHeader(){
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration((ProfileEntryAdapter)entryAdapter);
        recyclerView.addItemDecoration(headersDecor);
        StickyHeadersTouchListener touchListener = new StickyHeadersTouchListener(recyclerView, headersDecor, getActivity());
        touchListener.setOnHeaderClickListener(
                new StickyHeadersTouchListener.OnHeaderClickListener() {

                    @Override
                    public void onHeaderClickLeftButton() {
                        PlayerManager.getInstance().stopPlayer();
                        uploadPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        onChangePage(ProfileEntryAdapter.PROFILE_PAGE);
                    }

                    @Override
                    public void onHeaderClickRightButton() {
                        profilePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        onChangePage(ProfileEntryAdapter.UPDATES_PAGE);
                    }
                });
        recyclerView.addOnItemTouchListener(touchListener);
    }

    private void onChangePage(int page) {
        final  ProfileEntryAdapter adapter = ((ProfileEntryAdapter) entryAdapter);
        if (page != adapter.getPage()){
            adapter.setPage(page);
        }
        int position;
        switch (page){
            case ProfileEntryAdapter.PROFILE_PAGE:
                if (profilePosition != -1) {
                    position = profilePosition;
                    recyclerView.scrollToPosition(position);
                }
                break;
            case ProfileEntryAdapter.UPDATES_PAGE:
                if (uploadPosition != -1) {
                    position = uploadPosition + 1;
                    recyclerView.scrollToPosition(position);
                    Handler handler = new Handler();
                    final int finalPosition = position;
                    handler.postDelayed(
                            new Runnable() {
                                public void run() {
                                    downloadFile(finalPosition);
                                }
                            }, 500);
                }
                break;
        }
    }

    public void setUserProfile(final UserProfile userProfile){
        user = userProfile;
        ((ProfileEntryAdapter)entryAdapter).setUserProfile(userProfile);
        entryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void refreshEntryList() {
        super.refreshEntryList();
        if (entryAdapter.getArrEntries().size() > 0)
            ((NewProfileActivity) getActivity()).setIAmStar(entryAdapter.getEntry(0).getIAmStar());
    }

    @Override
    public void onRemoveEntry(int position) {

    }

    @Override
    public void onFollowEntry(String uId, String isMyStar) {
        entryAdapter.onFollowEntry(uId, isMyStar);
    }

    @Override
    public void onChangeEntry(EntryPojo entryPojo) {

    }
}
