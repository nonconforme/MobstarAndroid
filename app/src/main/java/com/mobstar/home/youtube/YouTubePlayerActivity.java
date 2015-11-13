package com.mobstar.home.youtube;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.home.new_home_screen.EntryItem;
import com.mobstar.pojo.EntryPojo;

/**
 * Created by lipcha on 10.11.15.
 */
public class YouTubePlayerActivity extends BaseActivity implements View.OnClickListener, EntryItem.OnChangeEntryListener {

    public static final String ENTRY_POJO = "entry_pojo";
    public static final String ENTRY_POSITION = "position";
    public static final int REMOVE_ENTRY = 150;

    private EntryPojo entryPojo;
    private int entryPosition;

    private ImageButton btnClose;
    private FrameLayout entryItemContainer;
    private YouTubeEntryItem youTubeEntryItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_player);
        getIntentData();
        findViews();
        setListeners();
        addEntryItem();

    }

    private void findViews(){
        btnClose = (ImageButton) findViewById(R.id.btnClose);
        entryItemContainer = (FrameLayout) findViewById(R.id.entryItemContainer);
    }

    private void setListeners(){
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnClose:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void addEntryItem(){
        youTubeEntryItem = new YouTubeEntryItem(entryItemContainer, true);
        youTubeEntryItem.init(entryPojo, 0, this, this);
    }

    private void getIntentData(){
        final Intent args = getIntent();
        if (args == null)
            return;
        entryPojo = (EntryPojo) args.getSerializableExtra(ENTRY_POJO);
        entryPosition = args.getIntExtra(ENTRY_POSITION, -1);
    }

    @Override
    public void onRemoveEntry(int position) {
        final Intent intent = new Intent();
        intent.putExtra(ENTRY_POSITION, entryPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFollowEntry(String uId, String isMyStar) {
        entryPojo.setIsMyStar(isMyStar);
        youTubeEntryItem.refreshEntry(entryPojo);
    }

    @Override
    public void onChangeEntry(EntryPojo _entryPojo) {
        entryPojo = _entryPojo;
        youTubeEntryItem.refreshEntry(_entryPojo);

    }
}
