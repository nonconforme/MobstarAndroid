package com.mobstar.home.new_home_screen.profile;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.home.new_home_screen.EntryItem;
import com.mobstar.home.new_home_screen.RecyclerViewAdapter;
import com.mobstar.pojo.EntryPojo;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

/**
 * Created by lipcha on 22.09.15.
 */
public class ProfileEntryAdapter extends RecyclerViewAdapter implements StickyRecyclerHeadersAdapter<ProfileStickyHeaderItem> {


    public static final int UPDATES_PAGE = 1;
    public static final int PROFILE_PAGE = 2;


    public static final int ENTRY_ITEM_VIEW_TYPE = 3;
    public static final int PROFILE_HEADER_VIEW_TYPE = 4;
    public static final int PROFILE_VIEW_TYPE = 5;
    public static final int NO_DATA_VIEW_TYPE = 6;

    private UserProfileData userData;
    private int page = UPDATES_PAGE;
    private ProfileStickyHeaderItem stickyHeaderItem;


    public ProfileEntryAdapter(BaseActivity activity, UserProfileData _userdata) {
        super(activity);
        userData = _userdata;
        page = UPDATES_PAGE;

    }

    public void setPage(int _page){
        page = _page;
        stickyHeaderItem.setupViews(_page);
        notifyDataSetChanged();
    }

    public int getPage(){
        return page;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == PROFILE_HEADER_VIEW_TYPE){
            final View inflatedView = layoutInflater.inflate(R.layout.new_layout_profile_header, viewGroup, false);
            final ProfileHeaderItem itemProfile = new ProfileHeaderItem(inflatedView);
            return itemProfile;
        }
        if (viewType == PROFILE_VIEW_TYPE){
            final View inflatedView = layoutInflater.inflate(R.layout.fragment_profile, viewGroup, false);
            final ProfileItem profileItem = new ProfileItem(inflatedView);
            return profileItem;
        }

        if (viewType == NO_DATA_VIEW_TYPE){
            final View inflatedView = layoutInflater.inflate(R.layout.layout_profile_nodata, viewGroup, false);
            final NoDataItem noDataItem = new NoDataItem(inflatedView);
            return noDataItem;
        }

        final View inflatedView = layoutInflater.inflate(R.layout.row_item_entry, viewGroup, false);
        final EntryItem entryItem = new EntryItem(inflatedView);
        itemsList.add(entryItem);
        return entryItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewItem, int position) {
        if (position == 0)
            ((ProfileHeaderItem)viewItem).init(baseActivity, userData);
        else{
            switch (page){
                case UPDATES_PAGE:
                    if (arrEntryes.size() == 0)
                        ((NoDataItem)viewItem).init(baseActivity);
                    else
                        ((EntryItem)viewItem).init(arrEntryes.get(position - 1), position, baseActivity, this);
                    break;
                case PROFILE_PAGE:
                    ((ProfileItem)viewItem).init(baseActivity, userData.getUserPic(), userData.getUserName());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (page == UPDATES_PAGE ){
            if (arrEntryes.size() == 0)
                return 2;
            else
                return arrEntryes.size() + 1;
        }
        else return 2;
    }

    @Override
    public long getHeaderId(int position) {
        if (position >= 1)
            return 1;
        return -1;
    }

    @Override
    public ProfileStickyHeaderItem onCreateHeaderViewHolder(ViewGroup viewGroup) {
        final View inflatedView = layoutInflater.inflate(R.layout.layout_profile_sticky_header, viewGroup, false);
        inflatedView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        final ProfileStickyHeaderItem stickyHeader = new ProfileStickyHeaderItem(inflatedView);
        return stickyHeader;
    }

    @Override
    public void onBindHeaderViewHolder(ProfileStickyHeaderItem stickyHeader, int i) {
        stickyHeader.init(baseActivity, UPDATES_PAGE);
        stickyHeaderItem = stickyHeader;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return PROFILE_HEADER_VIEW_TYPE;
        switch (page){
            case UPDATES_PAGE:
                if (arrEntryes.size() == 0)
                    return NO_DATA_VIEW_TYPE;
                else return ENTRY_ITEM_VIEW_TYPE;
            case PROFILE_PAGE:
                return PROFILE_VIEW_TYPE;
        }
        return PROFILE_HEADER_VIEW_TYPE;
    }
}

