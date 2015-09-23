package com.mobstar.home.new_home_screen.profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.home.new_home_screen.EntryItem;
import com.mobstar.home.new_home_screen.RecyclerViewAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

/**
 * Created by lipcha on 22.09.15.
 */
public class ProfileEntryAdapter extends RecyclerViewAdapter implements StickyRecyclerHeadersAdapter<ProfileStickyHeader> {

    private static final int ENTRY_ITEM_TYPE = 1;
    private static final int PROFILE_HEADER_VIEW_TYPE = 2;
    private static final int PROFILE_VIEW_TYPE = 3;
    private static final int NO_DATA_VIEW_TYPE = 4;

    private UserProfileData userData;
    private int itemModeType;

    public ProfileEntryAdapter(BaseActivity activity, UserProfileData _userdata) {
        super(activity);
        userData = _userdata;
        itemModeType = ENTRY_ITEM_TYPE;

    }

    public void setItemModeType(int modeType){
        itemModeType = modeType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == PROFILE_HEADER_VIEW_TYPE){
            final View inflatedView = layoutInflater.inflate(R.layout.new_layout_profile_header, viewGroup, false);
            final ProfileItem itemProfile = new ProfileItem(inflatedView);
            return itemProfile;
        }
        final View inflatedView = layoutInflater.inflate(R.layout.row_item_entry, viewGroup, false);
        final EntryItem entryItem = new EntryItem(inflatedView);
        itemsList.add(entryItem);
        return entryItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder entryItem, int position) {
        if (position == 0)
            ((ProfileItem)entryItem).init(baseActivity, userData);
        else{
            switch (itemModeType){
                case ENTRY_ITEM_TYPE:
                    ((EntryItem)entryItem).init(arrEntryes.get(position), position, baseActivity, this);
                    break;
                case PROFILE_VIEW_TYPE:

                    break;
                case NO_DATA_VIEW_TYPE:

                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        switch (itemModeType){
            case PROFILE_VIEW_TYPE:
            case NO_DATA_VIEW_TYPE:
                return 1;
            default:
                return arrEntryes.size();
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position >= 1)
            return 1;
        return -1;
    }

    @Override
    public ProfileStickyHeader onCreateHeaderViewHolder(ViewGroup viewGroup) {
        final View inflatedView = layoutInflater.inflate(R.layout.layout_profile_sticky_header, viewGroup, false);
        final ProfileStickyHeader stickyHeader = new ProfileStickyHeader(inflatedView);
        return stickyHeader;
    }

    @Override
    public void onBindHeaderViewHolder(ProfileStickyHeader stickyHeader, int i) {
        stickyHeader.init(baseActivity, true, null);
    }



    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return PROFILE_HEADER_VIEW_TYPE;
        return itemModeType;
    }
}

