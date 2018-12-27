package com.hlm.utils.network;

import android.text.TextUtils;

import com.hlm.config.CommonConfig;
import com.hlm.utils.MyLog;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NetWorkRequest {
    private static final int CONNECT_TIME_OUT = 15000;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static String request(String requestUrl, String data) {
        String result = null;
        String content = getContent(data);
        MyLog.e("<请求参数>", "api:" + requestUrl + "---结果:" + content);
        int i = 0;
        while (i < 2) {
            try {
                OkHttpClient client = (new OkHttpClient.Builder()).hostnameVerifier(new NetWorkRequest.AllHostnameTrustVerifier()).connectTimeout(15000L, TimeUnit.MILLISECONDS).readTimeout(15000L, TimeUnit.MILLISECONDS).writeTimeout(15000L, TimeUnit.MILLISECONDS).build();
                RequestBody requestBody = RequestBody.create(JSON, content);
                Request request = (new okhttp3.Request.Builder()).url(fixUrl(requestUrl)).post(requestBody).build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                MyLog.e("<请求结果>", "api:" + requestUrl + "---结果:" + result);
                break;
            } catch (Exception var10) {
                MyLog.e("<请求异常>", "api:" + requestUrl + "---异常信息:" + var10.getMessage());
                var10.printStackTrace();
                ++i;
            }
        }

        return result;
    }

    public static final String DEVICE = "device";
    public static final String CALLER = "caller";
    public static final String TOKEN = "token";
    public static final String REQTIME = "reqTime";

    private static String getContent(String data) {
        JSONObject jsonObject = new JSONObject();

        try {
            if (!TextUtils.isEmpty(data)) {
                jsonObject = new JSONObject(data);
            }

            jsonObject.put(DEVICE, CommonConfig.getDevice());
            jsonObject.put(CALLER, CommonConfig.getCaller());
            jsonObject.put(TOKEN, CommonConfig.getToken());
            jsonObject.put(REQTIME, System.currentTimeMillis());
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(jsonObject.toString());
        return stringBuilder.toString();
    }


    private static class AllHostnameTrustVerifier implements HostnameVerifier {
        public AllHostnameTrustVerifier() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    private static String fixUrl(String url) {
        if (!url.startsWith("http")) {
            url = String.format("%s%s", CommonConfig.getBaseUrl(), url);
        }
        return url;
    }
}
