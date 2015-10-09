package com.mobstar.home;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.mobstar.R;
import com.mobstar.utils.Constant;

/**
 * Created by vasia on 28.08.15.
 */
public class HowToVoteActivity extends Activity implements View.OnClickListener {

    public static final String HOW_TO_VOTE = "how_to_vote";
    private Button btnOk;
    private CheckBox cbDonTShowAgain;
    private ImageButton btnClose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_vote);
        findViews();
        setListeners();
    }

    private void findViews(){
        btnOk = (Button) findViewById(R.id.btnOk);
        cbDonTShowAgain = (CheckBox) findViewById(R.id.cbDonTShowAgain);
        btnClose = (ImageButton) findViewById(R.id.btnClose);
    }

    private void setListeners() {
        btnOk.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
            case R.id.btnClose:
                stopThisActivity();
                break;
        }
    }

    private void stopThisActivity(){
        if (cbDonTShowAgain.isChecked()){
            SharedPreferences.Editor editor = getSharedPreferences(Constant.MOBSTAR_PREF, MODE_PRIVATE).edit();
            editor.putBoolean(HOW_TO_VOTE, false);
            editor.commit();
        }
        finish();
    }
}
