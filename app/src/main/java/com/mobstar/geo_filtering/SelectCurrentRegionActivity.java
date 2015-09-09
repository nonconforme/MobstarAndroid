package com.mobstar.geo_filtering;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.mobstar.R;
import com.mobstar.custom.CheckableView;
import com.mobstar.pojo.ContinentsPojo;
import com.mobstar.utils.Constant;

import org.json.JSONObject;

import java.util.HashMap;

import api.ConnectCallback;
import api.RestClient;
import api.responce.BaseResponse;
import api.responce.ContinentResponse;
import api.responce.UserAccountResponse;

/**
 * Created by lipcha on 08.09.15.
 */
public class SelectCurrentRegionActivity extends Activity implements CheckableView.OnCheckedChangeListener, View.OnClickListener {

    private CheckableView cbEurope, cbSouthAmerica, cbOceania, cbAsia, cbNorthAmerica, cbAfrica;
    private Button btnOk;
    private CheckableView checkedRegionView;
    private Toast mToast;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_current_region);
        findViews();
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
//            onClickOk();
                getAcoount();
                break;
        }

    }

    private void getAcoount(){
        RestClient.getInstance(this).getRequest(Constant.USER_ACCOUNT, null, new ConnectCallback<UserAccountResponse>() {
            @Override
            public void onSuccess(UserAccountResponse object) {
                Log.d("tag", object.toString());
            }

            @Override
            public void onFailure(String error) {

            }
        });
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
        params.put("userContinent", Integer.toString(continents.ordinal()));
        showProgress();
        RestClient.getInstance(this).postRequest(Constant.USER_CONTINENT, params, new ConnectCallback<ContinentResponse>() {

            @Override
            public void onSuccess(ContinentResponse object) {
                hideProgress();
            }

            @Override
            public void onFailure(String error) {
                hideProgress();
                showToastNotification(error);
            }
        });
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
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
    }

    private void hideProgress(){
        if (progressDialog != null)
            progressDialog.hide();
    }
}
