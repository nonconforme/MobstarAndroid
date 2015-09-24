package com.mobstar.home.new_home_screen.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobstar.BaseActivity;
import com.mobstar.api.DownloadFileManager;
import com.mobstar.custom.recycler_view.RemoveAnimation;
import com.mobstar.home.new_home_screen.HomeVideoListBaseFragment;
import com.mobstar.utils.Constant;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.HashMap;

/**
 * Created by lipcha on 22.09.15.
 */
public class ProfileFragment extends HomeVideoListBaseFragment {

    public static final String USER = "user";

    private UserProfileData userData;

    public static final ProfileFragment getInstance(final UserProfileData userData){
        final ProfileFragment profileFragment = new ProfileFragment();
        final Bundle args = new Bundle();
        args.putSerializable(USER, userData);
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
        if (userData!= null && userData.getUserId() != null)
            params.put("user", userData.getUserId());
        params.put("page", Integer.toString(pageNo));
        textNoData.setVisibility(View.GONE);
        getEntry(Constant.MIX_ENTRY, params, pageNo);
    }

    @Override
    protected void getArgs() {
        final Bundle args = getArguments();
        if (args.containsKey(USER))
            userData = (UserProfileData) args.getSerializable(USER);

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
        entryAdapter = new ProfileEntryAdapter((BaseActivity) getActivity(), userData);
        recyclerView.setAdapter(entryAdapter);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration((ProfileEntryAdapter) entryAdapter));
        downloadFileManager = new DownloadFileManager(getActivity(), this);
        endlessRecyclerOnScrollListener.setLinearLayoutManager((LinearLayoutManager) recyclerView.getLayoutManager());
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);


        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration((ProfileEntryAdapter)entryAdapter);
        recyclerView.addItemDecoration(headersDecor);
        StickyHeadersTouchListener touchListener = new StickyHeadersTouchListener(recyclerView, headersDecor, getActivity());
        touchListener.setOnHeaderClickListener(
                new StickyHeadersTouchListener.OnHeaderClickListener() {

                    @Override
                    public void onHeaderClickLeftButton() {
                        onChangePage(ProfileEntryAdapter.PROFILE_PAGE);
                    }

                    @Override
                    public void onHeaderClickRightButton() {
                        onChangePage(ProfileEntryAdapter.UPDATES_PAGE);
                    }
                });
        recyclerView.addOnItemTouchListener(touchListener);
    }

    private boolean onChangePage(int page) {
        final  ProfileEntryAdapter adapter = ((ProfileEntryAdapter) entryAdapter);
        if (page != adapter.getPage()){
            adapter.setPage(page);
        }
        return page == ProfileEntryAdapter.ENTRY_ITEM_VIEW_TYPE;
    }
}
