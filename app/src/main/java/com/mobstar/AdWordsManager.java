package com.mobstar;

import android.content.Context;
import android.util.Log;

import com.google.ads.conversiontracking.AdWordsConversionReporter;

/**
 * Created by Alexandr on 15.09.2015.
 */
public class AdWordsManager {
    private static final String CONVERSION_ID="944583835";
    private static final String ENGAGEMENT_EVENT="KaheCPTOy18Qm-m0wgM";
    private static final String UPLOADING_CONTENT_EVENT="3r1TCJmpwF8Qm-m0wgM";
    private static final String SINGUP_EVENT="Nl0fCIHNy18Qm-m0wgM";
    private static final String FIRST_OPEN_EVENT="Em0jCJabxV8Qm-m0wgM";
    private static final String LOG_TAG = AdWordsManager.class.getName();
    private static AdWordsManager instance;
    private final Context context;

    private AdWordsManager(Context context) {
        this.context = context;
    }

    public static void registerManager(Context context){
        if (instance==null){
            instance = new AdWordsManager(context);
        }
    }

    public static AdWordsManager getInstance(){
        return instance;
    }


    public void sendEngagementEvent(){
        Log.d(LOG_TAG,"sendEngagementEvent");
        AdWordsConversionReporter.reportWithConversionId(context,
                CONVERSION_ID, ENGAGEMENT_EVENT, "0.00", true);
    }
    public void sendUploadingContentEvent(){
        Log.d(LOG_TAG,"sendUploadingContentEvent");
        AdWordsConversionReporter.reportWithConversionId(context,
                CONVERSION_ID, UPLOADING_CONTENT_EVENT, "0.00", true);
    }
    public void sendSingupEvent(){
        Log.d(LOG_TAG,"sendSingupEvent");
        AdWordsConversionReporter.reportWithConversionId(context,
                CONVERSION_ID, SINGUP_EVENT, "0.00", true);
    }
    public void sendFirstOpenEvent(){
        Log.d(LOG_TAG,"sendFirstOpenEvent");
        AdWordsConversionReporter.reportWithConversionId(context,
                CONVERSION_ID, FIRST_OPEN_EVENT, "0.00", true);
    }
}
