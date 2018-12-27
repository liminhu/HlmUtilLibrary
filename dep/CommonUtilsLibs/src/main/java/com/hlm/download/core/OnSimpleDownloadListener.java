package com.hlm.download.core;

public interface OnSimpleDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(String url, String downloadPath);

        /**
         * @param progress
         * 下载进度
         */
        void onDownloading(String url, int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed(String url, Exception e);
    }
