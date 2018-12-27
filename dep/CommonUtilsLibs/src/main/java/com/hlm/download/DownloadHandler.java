package com.hlm.download;

import android.os.Handler;
import android.os.Looper;

class DownloadHandler {
    public static void postToUi(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
