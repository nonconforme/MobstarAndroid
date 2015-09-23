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
    private static final String WROTE_COMMENT_EVENT="EdDjCK7t-l8Qm-m0wgM";
    private static final String LOG_TAG = AdWordsManager.class.getName();
    private static final String SHARED_ENTRY_EVENT = "lR8jCLax9F8Qm-m0wgM";
    private static final String MESSAGE_SENT_EVENT = "fdWaCKLz-l8Qm-m0wgM";
    private static final String FOLLOWED_USER_EVENT = "M2K_CKuv9F8Qm-m0wgM";
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

    public void sendWroteCommentEvent(){
        Log.d(LOG_TAG,"sendWroteCommentEvent");
        AdWordsConversionReporter.reportWithConversionId(context,
                CONVERSION_ID, WROTE_COMMENT_EVENT, "0.00", true);
    }
    public void sendSharedEntryEvent(){
        Log.d(LOG_TAG,"sendSharedEntryEvent");
        AdWordsConversionReporter.reportWithConversionId(context,
                CONVERSION_ID, SHARED_ENTRY_EVENT, "0.00", true);
    }
    public void sendMessageSentEvent(){
        Log.d(LOG_TAG,"sendMessageSentEvent");
        AdWordsConversionReporter.reportWithConversionId(context,
                CONVERSION_ID, MESSAGE_SENT_EVENT, "0.00", true);
    }
    public void sendFollowedUserEvent(){
        Log.d(LOG_TAG,"sendFollowedUserEvent");
        AdWordsConversionReporter.reportWithConversionId(context,
                CONVERSION_ID, FOLLOWED_USER_EVENT, "0.00", true);
    }
}
