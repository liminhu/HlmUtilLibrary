package com.hlm.download;

import android.text.TextUtils;

import com.hlm.download.core.OnSimpleDownloadListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class SimpleDownloader {

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public static void download(final String url, final String saveDir, final String fileName, final OnSimpleDownloadListener listener) {
        final long duration = 0;
        Request request = new Request.Builder().url(url).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                // 下载失败
                DownloadHandler.postToUi(new Runnable() {
                    @Override
                    public void run() {
                        if (null != listener)
                            listener.onDownloadFailed(url, e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                final String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    String destFileName = fileName;
                    if (TextUtils.isEmpty(fileName)) {
                        destFileName = getNameFromUrl(url);
                    }
                    final File file = new File(savePath, destFileName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        final int progress = (int) (sum * 1.0f / total * 100);
                        fos.flush();
                        if (System.currentTimeMillis() - duration > 500 || progress == 10) {
                            // 下载中
                            DownloadHandler.postToUi(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != listener)
                                        listener.onDownloading(url, progress);
                                }
                            });
                        }
                    }
                    // 下载完成
                    DownloadHandler.postToUi(new Runnable() {
                        @Override
                        public void run() {
                            if (null != listener)
                                listener.onDownloadSuccess(url, file.getAbsolutePath());
                        }
                    });
                } catch (final Exception e) {
                    DownloadHandler.postToUi(new Runnable() {
                        @Override
                        public void run() {
                            if (null != listener)
                                listener.onDownloadFailed(url, e);
                        }
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private static String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    private static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static File syncDownloadFile(final String url, final String saveDir, final String fileName){
        Request request = new Request.Builder().url(url).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            Response response = okHttpClient.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            if(!new File(saveDir).exists()){
                new File(saveDir).mkdirs();
            }
            File temDownloadFile = new File(saveDir, fileName);
            FileOutputStream ops = new FileOutputStream(temDownloadFile);
            BufferedOutputStream bops = new BufferedOutputStream(ops);
            BufferedInputStream bips = new BufferedInputStream(inputStream);
            byte[] data = new byte[1024 * 1024];
            int ln = -1;
            while ((ln = bips.read(data)) != -1){
                bops.write(data,0,ln);
                bops.flush();
            }
            return temDownloadFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
