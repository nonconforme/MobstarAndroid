package com.mobstar.home.split;

/**
 * Created by lipcha on 07.09.15.
 */
public interface OnDownloadFileCompletedListener {
    void onCompleted(final String filePath);
    void onFailed();
}
