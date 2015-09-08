package com.mobstar.geo_filtering;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mobstar.R;
import com.mobstar.custom.CheckableView;

/**
 * Created by lipcha on 08.09.15.
 */
public class SelectCurrentRegionActivity extends Activity implements CheckableView.OnCheckedChangeListener, View.OnClickListener {

    private CheckableView cbEurope, cbSouthAmerica, cbOceania, cbAsia, cbNorthAmerica, cbAfrica;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_current_region);
        findViews();
        setListeners();
    }

    private void findViews(){
        cbEurope         = (CheckableView) findViewById(R.id.cbEurope);
        cbSouthAmerica   = (CheckableView) findViewById(R.id.cbSouthAmerica);
        cbOceania        = (CheckableView) findViewById(R.id.cbOceania);
        cbAsia           = (CheckableView) findViewById(R.id.cbAsia);
        cbNorthAmerica   = (CheckableView) findViewById(R.id.cbNorthAmerica);
        cbAfrica         = (CheckableView) findViewById(R.id.cbAfrica);
        btnOk            = (Button) findViewById(R.id.btnOk);
    }

    private void setListeners(){
        cbEurope.setOnCheckedChangeListener(this);
        cbSouthAmerica.setOnCheckedChangeListener(this);
        cbOceania.setOnCheckedChangeListener(this);
        cbAsia.setOnCheckedChangeListener(this);
        cbNorthAmerica.setOnCheckedChangeListener(this);
        cbAfrica.setOnCheckedChangeListener(this);
        btnOk.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:

                break;
        }
    }

    @Override
    public void onCheckedChange(CheckableView _view, boolean _checked) {

    }
}
