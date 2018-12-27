package com.hlm.download.core;

import java.io.File;

/**
 */
public interface ISimpleDownloader {
    void download(String url, String saveDir, String fileName, OnSimpleDownloadListener listener);

    File syncDownload(String url, String saveDir, String fileName);
}
