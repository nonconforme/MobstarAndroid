package com.mobstar.api.responce;

import java.io.File;

/**
 * Created by lipcha on 11.09.15.
 */
public interface OnFileDownloadCallback {
    void onDownload(File file);
    void onFailure(String errorMessage);
}
