package com.hlm.config;

import android.app.Application;

import com.hlm.exception.NotInitModelException;

import org.json.JSONObject;

/**
 * util 通用配置
 * 1，设置 数据网络是否加载图片
 */

public class CommonConfig {
    /**
     * 是否隐藏图片显示 在移动网络环境下
     */
    private boolean hideImageInMobileNetWork = false;
    /**
     * 模块是否在debug 环境下
     */
    private boolean debug = true;

    private String baseUrl;
    //通用参数
    private JSONObject device;
    private JSONObject caller;

    private String token;

    private static CommonConfig config;
    private Application application;

    private CommonConfig(Application application) {
        this.application = application;
    }

    /**
     * 进入应用必须调用初始化
     *
     * @param application
     */
    public static CommonConfig initLibrary(Application application) {
        if (null == config) {
            config = new CommonConfig(application);
        }
        return config;
    }

    /**
     * 设置基础的URL
     *
     * @param baseUrl
     */
    public static void setBaseUrl(String baseUrl) {
        config.baseUrl = baseUrl;
    }

    public static void setDeviceAndCaller(JSONObject device, JSONObject caller) {
        config.device = device;
        config.caller = caller;
    }

    public static void setToken(String token){
        config.token = token;
    }

    public static void setHideImageInMobileNetwork(boolean hide) {
        checkInitLibrary();
        config.hideImageInMobileNetWork = hide;
    }

    /**
     * 检查是否初始化模块
     */
    private static void checkInitLibrary() {
        if (config == null) {
            throw new NotInitModelException();
        }
    }

    /**
     * 设置是否在debug 环境下
     *
     * @param debug
     */
    public static void setInDebug(boolean debug) {
        checkInitLibrary();
        config.debug = debug;
    }


    public static boolean isHideImageInMobileNetWork() {
        checkInitLibrary();
        return config.hideImageInMobileNetWork;
    }


    public static boolean isDebug() {
        checkInitLibrary();
        return config.debug;
    }

    public static String getBaseUrl() {
        return config.baseUrl;
    }

    public static JSONObject getDevice(){
        return config.device;
    }

    public static JSONObject getCaller(){
        return config.caller;
    }

    public static String getToken(){
        return config.token;
    }
}
