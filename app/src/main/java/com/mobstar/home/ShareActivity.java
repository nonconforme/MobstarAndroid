package com.mobstar.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.google.android.gms.plus.PlusShare;
import com.mobstar.AdWordsManager;
import com.mobstar.R;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.login.facebook.FacebookManager;
import com.mobstar.login.facebook.FacebookResponse;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.twitter.ImageTwitter;
import com.mobstar.twitter.ImageTwitter.OnCompleteListener;
import com.mobstar.utils.ImageDownloader;
import com.mobstar.utils.ImageDownloader.ImageLoaderListener;
import com.mobstar.utils.Utility;
import com.rosaloves.bitlyj.Url;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

public class ShareActivity extends Activity implements OnClickListener, FacebookManager.OnFacebookSignInCompletedListener, FacebookCallback<Sharer.Result> {

	private EntryP entryPojo;
	private TextView textUserName, textTime, textDescription;
	private ImageView imgUserPic;
	private ImageButton btnClose;
	private TextView btnTweet, btnSendToFriend, btnAddToGPlus, btnFBPost;
	private File picFile;
	private Uri pngUri;
	private ImageDownloader mDownloader;
	private static Bitmap bitmap;
	private FileOutputStream fos;
	private String shareText;
	private String ShortURL="";
	private String twitterShareText;
	private boolean isTalent=false;
	private String UserName,UserImg;

    private FacebookManager facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebook = new FacebookManager(getApplicationContext(), this, this);
        setContentView(R.layout.activity_share);
        prepareShareText();
        getBundleExtra();
        initControls();
        Utility.SendDataToGA("Share Screen", ShareActivity.this);
    }

    private void prepareShareText(){
        shareText = getResources().getString(R.string.share_text) + " ";
        twitterShareText = getResources().getString(R.string.twitter_share_text) + " ";
    }

    private void getBundleExtra(){
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isTalent = bundle.getBoolean("isTalent");
            if (isTalent) {
                UserName = bundle.getString("UserName");
                UserImg = bundle.getString("UserImg");
                if (UserImg != null && UserImg.length() > 0) {
                    mDownloader = new ImageDownloader(UserImg
                            , getParent(), bitmap, new ImageLoaderListener() {
                        @Override
                        public void onImageDownloaded(Bitmap bmp) {
                            bitmap = bmp;

                            if (Utility.isNetworkAvailable(ShareActivity.this)) {
                                pngUri = saveImageToSD();
                            } else {
                                Toast.makeText(ShareActivity.this, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

			            /*--- we need to call execute() since nothing will happen otherwise ---*/
                    mDownloader.execute();
                }

            } else {
                entryPojo = (EntryP) getIntent().getSerializableExtra("entry");
            }
        }
    }


    private void initControls() {
        findViews();
        setListeners();
        initializeViews();
    }

    private void initializeViews(){
        if (!isTalent) {
            Utility.ShowProgressDialog(this, getString(R.string.generating_shorten_link));

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Url url = null;
                    url = as("niravspaceo",
                            "R_5e9eb981a6e34baea49713adbff50779").call(shorten("http://share.mobstar.com/info.php?id=" + entryPojo.getEntry().getId()));
                    //		 }

                    ShortURL = url.getShortUrl();
                    shareText = shareText + ShortURL;
                    Utility.HideDialog(ShareActivity.this);
                }
            }).start();

            textUserName.setText(entryPojo.getUser().getFullName());
            textDescription.setText(Utility.unescape_perl_string(entryPojo.getEntry().getName()));
            textTime.setText(entryPojo.getEntry().getCreatedAgo());

            imgUserPic = (ImageView) findViewById(R.id.imgUserPic);

            if (entryPojo.getUser().getProfileImage().equals("")) {
                imgUserPic.setImageResource(R.drawable.ic_pic_small);
            } else {
                imgUserPic.setImageResource(R.drawable.ic_pic_small);

                Picasso.with(this).load(entryPojo.getUser().getProfileImage()).resize(Utility.dpToPx(this, 45), Utility.dpToPx(this, 45)).centerCrop().placeholder(R.drawable.ic_pic_small)
                        .error(R.drawable.ic_pic_small).transform(new RoundedTransformation(Utility.dpToPx(this, 45), 0)).into(imgUserPic);

            }
        } else {
            ShortURL = UserName;
            shareText = "#mobstar " + UserName;
            textUserName.setText(UserName);
            textTime.setVisibility(View.GONE);
            textDescription.setVisibility(View.GONE);
        }

    }

    private void findViews(){
        btnClose = (ImageButton) findViewById(R.id.btnClose);
        textUserName = (TextView) findViewById(R.id.textUserName);
        textTime = (TextView) findViewById(R.id.textTime);
        textDescription = (TextView) findViewById(R.id.textDescription);
        btnTweet = (TextView) findViewById(R.id.btnTweet);
        btnSendToFriend = (TextView) findViewById(R.id.btnSendToFriend);
        btnAddToGPlus = (TextView) findViewById(R.id.btnAddToGPlus);
        btnFBPost = (TextView) findViewById(R.id.btnFBPost);
    }

    private void setListeners(){
        btnClose.setOnClickListener(this);
        btnTweet.setOnClickListener(this);
        btnSendToFriend.setOnClickListener(this);
        btnAddToGPlus.setOnClickListener(this);
        btnFBPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnClose:
                onBackPressed();
                break;
            case R.id.btnTweet:
                postToTitter();
                break;
            case R.id.btnSendToFriend:
                sendToFriends();
                break;
            case R.id.btnAddToGPlus:
                addToGplus();
                break;
            case R.id.btnFBPost:
                postToFacebook();
                break;
        }
    }

    private void postToTitter(){
        twitterShareText = twitterShareText + ShortURL;
        if (isTalent && picFile != null) {

            Utility.ShowProgressDialog(this, getString(R.string.loading));

            boolean authOnly = false;
            ImageTwitter mTweet = new ImageTwitter(ShareActivity.this, authOnly, twitterShareText, picFile);
            mTweet.setOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(final String action) {

                    if (action.equals("Success")) {

                        Utility.HideDialog(ShareActivity.this);
                        AdWordsManager.getInstance().sendSharedEntryEvent();

                    } else {
                        Utility.HideDialog(ShareActivity.this);
                    }

                }
            });
            mTweet.send();
        } else {
            Utility.ShowProgressDialog(this, getString(R.string.loading));

            boolean authOnly = false;
            ImageTwitter mTweet = new ImageTwitter(ShareActivity.this, authOnly, twitterShareText, null);
            mTweet.setOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(final String action) {
                    // TODO Auto-generated method stub

                    if (action.equals("Success")) {

                        Utility.HideDialog(ShareActivity.this);
                        AdWordsManager.getInstance().sendSharedEntryEvent();

                    } else {
                        Utility.HideDialog(ShareActivity.this);
                    }

                }
            });
            mTweet.send();
        }
    }

    private void sendToFriends(){
        if (isTalent) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_SUBJECT, "Mobstar");
            i.putExtra(Intent.EXTRA_TEXT, shareText);
            if (pngUri != null) {
                i.putExtra(Intent.EXTRA_STREAM, pngUri);
            }
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
                AdWordsManager.getInstance().sendSharedEntryEvent();
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ShareActivity.this, getString(R.string.there_are_no_email_clients_installed), Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_SUBJECT, "Mobstar");
            i.putExtra(Intent.EXTRA_TEXT, shareText);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
                AdWordsManager.getInstance().sendSharedEntryEvent();
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ShareActivity.this, getString(R.string.there_are_no_email_clients_installed), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void addToGplus(){
        if (isTalent && pngUri != null) {
            Intent shareIntent = ShareCompat.IntentBuilder.from(ShareActivity.this)
                    .setText(shareText)
                    .setType("image/jpeg")
                    .setStream(pngUri)
                    .getIntent()
                    .setPackage("com.google.android.apps.plus");
            startActivityForResult(shareIntent, 0);
            AdWordsManager.getInstance().sendSharedEntryEvent();
        } else {
            Intent shareIntent = new PlusShare.Builder(ShareActivity.this).setType("text/plain")
                    .setText(shareText + "\n#mobstar").getIntent();
            startActivityForResult(shareIntent, 0);
            AdWordsManager.getInstance().sendSharedEntryEvent();
        }

    }

    private void postToFacebook() {
        if (FacebookManager.isLoggedIn()){
            Utility.ShowProgressDialog(this, getString(R.string.uploading_your_post) + "...");
            facebook.post(entryPojo, isTalent, UserImg, shareText, this);
        }
        else {
            facebook.signInWithFacebook();
        }
    }

    //    facebook login callback

    @Override
    public void onFacebookLoginSuccess(FacebookResponse response) {
        Utility.ShowProgressDialog(this, getString(R.string.uploading_your_post) + "...");
        facebook.post(entryPojo, isTalent, UserImg, shareText, this);
    }

    @Override
    public void onFacebookLoginFailure() {

    }

    //  facebook share callback

    @Override
    public void onSuccess(Sharer.Result result) {
        Utility.HideDialog(this);
        showSuccessPostDialog();
    }

    @Override
    public void onCancel() {
        Utility.HideDialog(this);
    }

    @Override
    public void onError(FacebookException e) {
        Utility.HideDialog(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(e.getMessage())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showSuccessPostDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.post_successfully_shared_on_your_wall))
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.onActivityResult(requestCode, resultCode, data);
    }


    public void showToast(final String message) {
        if (!isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(ShareActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void showError(final String message) {

        if (!isFinishing()) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(ShareActivity.this, message, Toast.LENGTH_SHORT).show();
                    Utility.HideDialog(ShareActivity.this);
                }
            });
        }

    }


    private Uri saveImageToSD() {
        /*--- this method will save your downloaded image to SD card ---*/

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    /*--- you can select your preferred CompressFormat and quality. 
	     * I'm going to use JPEG and 100% quality ---*/
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	    /*--- create a new file on SD card ---*/
        picFile = new File(Environment.getExternalStorageDirectory()
                + File.separator + "myDownloadedImage.png");
//	    infoLog("file path"+file.getAbsolutePath().toString());
        try {
            picFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
	    /*--- create a new FileOutputStream and write bytes to file ---*/
        try {
            fos = new FileOutputStream(picFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bytes.toByteArray());
            fos.close();
//	        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            if (!picFile.exists())
                picFile.mkdirs();

//           File pngFile = new File(file, "jetsam.png");
            //Save file encoded as PNG
            pngUri = Uri.fromFile(picFile);
            return pngUri;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
