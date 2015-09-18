package com.mobstar.api;

import android.content.Context;
import android.os.Environment;

import com.mobstar.api.responce.OnFileDownloadCallback;
import com.mobstar.utils.Utility;

import java.io.File;
import java.util.HashMap;

/**
 * Created by lipcha on 15.09.15.
 */
public class DownloadFileManager {

    private String PATH = "";

    private HashMap<String, Integer> listDownloadingFile;
    private Context mContext;
    private DownloadCallback downloadCallback;

    public DownloadFileManager(final Context _context, DownloadCallback _downloadCallback){
        mContext = _context;
        downloadCallback  =_downloadCallback;
        listDownloadingFile = new HashMap<>();
        PATH = Environment.getExternalStorageDirectory().getPath()
                + "/Android/data/" + mContext.getPackageName() +"/";
    }

    public void downloadFile(final String _fileUrl, final int position){
        if (listDownloadingFile.containsKey(_fileUrl))
            return;
        final String filePath = PATH + Utility.GetFileNameFromURl(_fileUrl);
        final File file = new File(filePath);
        if (file.exists() && downloadCallback != null){
            downloadCallback.onDownload(filePath, position);
            return;
        }
        listDownloadingFile.put(filePath, position);
        RestClient.getInstance(mContext).getFileRequest(_fileUrl, filePath, new OnFileDownloadCallback() {
            @Override
            public void onDownload(File file) {
                int position = listDownloadingFile.get(file.getAbsolutePath());
                if (downloadCallback != null)
                    downloadCallback.onDownload(file.getAbsolutePath(), position);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (downloadCallback != null)
                    downloadCallback.onFailed();
            }
        });
    }

    public void cancelFile(final String _tag){
        listDownloadingFile.remove(_tag);
        RestClient.getInstance(mContext).cancelRequest(_tag);
    }

    public interface DownloadCallback{
        void onDownload(final String filePath, final int position);
        void onFailed();
    }
}