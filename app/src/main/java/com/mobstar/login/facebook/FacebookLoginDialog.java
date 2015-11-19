package com.mobstar.login.facebook;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.mobstar.R;

/**
 * Created by lipcha on 18.11.15.
 */
public class FacebookLoginDialog extends Dialog implements View.OnClickListener {

    private Button btnDeny, btnAllow;
    private ImageButton btnClose;
    private OnFacebookAcceptListener facebookAcceptListener;

    public FacebookLoginDialog(Context context) {
        super(context, R.style.DialogTheme);
        initViews();

    }

    public void setOnAcceptListener(final OnFacebookAcceptListener _onFacebookAcceptListener){
        facebookAcceptListener = _onFacebookAcceptListener;
    }

    private void initViews(){
        setContentView(R.layout.dialog_fb);

        btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnAllow = (Button) findViewById(R.id.btnAllow);
        btnDeny = (Button) findViewById(R.id.btnDeny);

        btnClose.setOnClickListener(this);
        btnAllow.setOnClickListener(this);
        btnDeny.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnClose:
                dismiss();
                break;
            case R.id.btnAllow:
                if (facebookAcceptListener != null)
                    facebookAcceptListener.onFacebookAccept();
                break;
            case R.id.btnDeny:
                dismiss();
                break;
        }
    }

    public interface OnFacebookAcceptListener{
        void onFacebookAccept();
    }
}
