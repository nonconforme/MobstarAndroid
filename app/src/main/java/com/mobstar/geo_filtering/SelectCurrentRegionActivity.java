package com.mobstar.geo_filtering;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.ContinentResponse;
import com.mobstar.custom.CheckableView;
import com.mobstar.home.HomeActivity;
import com.mobstar.pojo.ContinentsPojo;
import com.mobstar.utils.Constant;

import java.util.HashMap;

/**
 * Created by lipcha on 08.09.15.
 */
public class SelectCurrentRegionActivity extends Activity implements CheckableView.OnCheckedChangeListener, View.OnClickListener {

    public static final String START_HOME_ACTIVITY = "start_home_activity";

    private CheckableView cbEurope, cbSouthAmerica, cbOceania, cbAsia, cbNorthAmerica, cbAfrica;
    private Button btnOk;
    private CheckableView checkedRegionView;
    private Toast mToast;
    private ProgressDialog progressDialog;
    private boolean startHomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_current_region);
        findViews();
        final Intent intent = getIntent();
        startHomeActivity = intent.getBooleanExtra(START_HOME_ACTIVITY, true);
        setListeners();
        assignedContinents();
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

    private void assignedContinents(){
        cbEurope.setTag(ContinentsPojo.Continents.EUROPE);
        cbSouthAmerica.setTag(ContinentsPojo.Continents.SOUTH_AMERICA);
        cbOceania.setTag(ContinentsPojo.Continents.OCEANIA);
        cbAsia.setTag(ContinentsPojo.Continents.ASIA);
        cbNorthAmerica.setTag(ContinentsPojo.Continents.NORTH_AMERICA);
        cbAfrica.setTag(ContinentsPojo.Continents.AFRICA);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
            onClickOk();
                break;
        }
    }

    private void onClickOk(){
        final ContinentsPojo.Continents selectedContinents = getSelectedContinents();
        if (selectedContinents == null) {
            showToastNotification(getString(R.string.no_select_current_region_message));
            return;
        }
        postCurrentRegionRequest(selectedContinents);
    }

    private void postCurrentRegionRequest(ContinentsPojo.Continents continents){
        final HashMap<String, String> params = new HashMap<>();
        params.put(ContinentResponse.KEY_CONTINENT, Integer.toString(continents.ordinal()));
        showProgress();
        RestClient.getInstance(this).postRequest(Constant.USER_CONTINENT, params, new ConnectCallback<ContinentResponse>() {

            @Override
            public void onSuccess(ContinentResponse object) {
                hideProgress();
                startHomeActivity();
            }

            @Override
            public void onFailure(String error) {
                hideProgress();
                showToastNotification(error);
            }
        });
    }

    private void startHomeActivity(){
        if (startHomeActivity) {
            final Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private ContinentsPojo.Continents getSelectedContinents(){
        final CheckableView[] checkableViews = {cbEurope, cbSouthAmerica, cbOceania, cbAsia, cbNorthAmerica, cbAfrica};

        for (CheckableView checkableView : checkableViews) {
            if (checkableView.isChecked())
                return (ContinentsPojo.Continents) checkableView.getTag();
        }
        return null;
    }

    @Override
    public void onCheckedChange(CheckableView _view, boolean _checked) {
        if (checkedRegionView == null) {
            checkedRegionView = _view;
            return;
        }
        if (checkedRegionView != _view && _checked) {
            checkedRegionView.setChecked(false);
            checkedRegionView = _view;
        }
    }

    private void showToastNotification(final String _message){
        if (mToast == null)
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setText(_message);
        mToast.show();

    }

    private void showProgress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

    private void hideProgress(){
        if (progressDialog != null)
            progressDialog.hide();
    }
}
