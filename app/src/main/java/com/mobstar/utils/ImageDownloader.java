package com.mobstar.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ImageDownloader extends AsyncTask<Void, Integer, Void> {
	
	private String url;
	
	private Context c;
	
	private Bitmap bmp;
	
	private ImageLoaderListener listener;


	/*--- constructor ---*/
	public ImageDownloader(String url, Context c, Bitmap bmp, ImageLoaderListener listener) {
	/*--- we need to pass some objects we are going to work with ---*/
	    this.url = url;
	  
	    this.c = c;
	  
	    this.bmp = bmp;
	    this.listener = listener;
	}

	/*--- we need this interface for keeping the reference to our Bitmap from the MainActivity. 
	 *  Otherwise, bmp would be null in our MainActivity*/
	public interface ImageLoaderListener {

	    void onImageDownloaded(Bitmap bmp);

	    }

	@Override
	protected void onPreExecute() {

	   
//	    Toast.makeText(c, "starting download", Toast.LENGTH_SHORT).show();

	    super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... arg0) {

	    bmp = getBitmapFromURL(url);

	   

	    return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

	/*--- show download progress on main UI thread---*/
	  
	    super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {

	    if (listener != null) {
	        listener.onImageDownloaded(bmp);
	        }

	   
//	    Toast.makeText(c, "download complete", Toast.LENGTH_SHORT).show();

	    super.onPostExecute(result);
	}

	public static Bitmap getBitmapFromURL(String link) {
	    /*--- this method downloads an Image from the given URL, 
	     *  then decodes and returns a Bitmap object
	     ---*/
	    try {
	        URL url = new URL(link);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);

	        return myBitmap;

	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.e("getBmpFromUrl error: ", e.getMessage().toString());
	        return null;
	    }
	}

	   
}
