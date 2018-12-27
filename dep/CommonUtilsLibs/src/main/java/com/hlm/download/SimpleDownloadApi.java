package com.hlm.download;

import com.hlm.download.core.ISimpleDownloader;
import com.hlm.download.core.OnSimpleDownloadListener;

import java.io.File;

public class SimpleDownloadApi implements ISimpleDownloader{
    private static ISimpleDownloader mDownloader;
    private SimpleDownloadApi(){
    }

    public static ISimpleDownloader share(){
        if(null == mDownloader)
            mDownloader = new SimpleDownloadApi();
        return mDownloader;
    }
    @Override
    public void download(String url, String saveDir, String fileName, OnSimpleDownloadListener listener){
        SimpleDownloader.download(url,saveDir,fileName,listener);
    }
    @Override
    public File syncDownload(String url, String saveDir, String fileName){
        return SimpleDownloader.syncDownloadFile(url,saveDir,fileName);
    }


}
