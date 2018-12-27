package com.hlm.utils;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class UtilSystemInfo {

    public static String getGlobalDeviceId(final Context appContext) {
        try {
            TelephonyManager tm = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    public static String getAndroidId(Context context) {
        String androidId = "0";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }

    public static String getNetworkType(final Context appContext, TelephonyManager telephonyManager) {
        WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        // this will only work for apps which already have wifi permissions.
        try {
            if (wifiManager.isWifiEnabled()) {
                return "wifi";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "edge";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "unknown";
        }
        return "none";
    }

    public static boolean checkConnectivity(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return networkinfo != null && networkinfo.isConnectedOrConnecting();
    }

    public static boolean isWapNetwork(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connMgr.getActiveNetworkInfo();
            if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == 9) {
                return false;
            }
            String currentAPN = info.getExtraInfo();
            if (currentAPN == null)
                return false;
            return currentAPN.equalsIgnoreCase("cmwap") || currentAPN.equalsIgnoreCase("ctwap") || currentAPN.equalsIgnoreCase("3gwap") || currentAPN.equalsIgnoreCase("uniwap");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isWifiNetwork(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connMgr.getActiveNetworkInfo();
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMobileNetwork(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connMgr.getActiveNetworkInfo();
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMobileNetworkConnecting(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static float dip2px(Context context, float dip) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return px;
    }

    public static float sp2px(Context context, float sp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, r.getDisplayMetrics());
        return px;
    }

    public static float px2sp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / scale + 0.5f);
    }

    public static CharSequence getLabel(Context context, String packageName, String className, String defaultValue) {
        try {
            final PackageManager pm = context.getPackageManager();
            CharSequence label = null;
            ComponentName componentName = new ComponentName(packageName, className);
            label = pm.getActivityInfo(componentName, 0).loadLabel(pm);
            if (label == null) {
                label = packageName;
            }
            return label;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static boolean isMIUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Method mReadProcLines;

    public static final void readProcLines(String path, String[] reqFields, long[] outSize) {
        if (mReadProcLines == null) {
            try {
                Class<android.os.Process> cls = android.os.Process.class;
                mReadProcLines = cls.getDeclaredMethod("readProcLines", String.class, String[].class, long[].class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            mReadProcLines.invoke(null, path, reqFields, outSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int getParentPid(int pid) {
        String[] procStatusLabels = {"PPid:"};
        long[] procStatusValues = new long[1];
        procStatusValues[0] = -1;
        readProcLines("/proc/" + pid + "/status", procStatusLabels, procStatusValues);
        return (int) procStatusValues[0];
    }

    public static final int getUidForPid(int pid) {
        String[] procStatusLabels = {"Uid:"};
        long[] procStatusValues = new long[1];
        procStatusValues[0] = -1;
        readProcLines("/proc/" + pid + "/status", procStatusLabels, procStatusValues);
        return (int) procStatusValues[0];
    }

    public static String getPidCmdline(int pid) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(String.format("/proc/%1$s/cmdline", pid)));
            return br.readLine().trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getCommandLineOutput(String.format("cat /proc/%1$s/cmdline", pid)).trim();
    }

    private static String getCommandLineOutput(String cmdLine) {
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec(cmdLine);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                output += (line + '\n');
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public static String getCurrentCountryString(Context context) {
        return context.getResources().getConfiguration().locale.getDisplayCountry();
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public static String getWifiMacAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();
        if (bAdapt != null) {
            return bAdapt.getAddress();
        }
        return "";
    }


    public static List<String> queryAllSdcardPath(Context context) {
        Set<String> result = new HashSet<String>();
        if (Build.VERSION.SDK_INT > 14) {
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            // 4.0.3开始有本方法
            try {
                Method m = StorageManager.class.getDeclaredMethod("getVolumePaths");
                m.setAccessible(true);
                String[] ps = (String[]) m.invoke(sm);
                for (String p : ps) {
                    File f = new File(p);
                    if (f.exists() && f.canWrite() && f.canRead()) {
                        try {
                            new StatFs(p);
                            result.add(p);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result.size() == 0 && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                result.add(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
        } else {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                result.add(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
        }
        return new ArrayList<String>(result);
    }

    public static File queryDownloadFolder(Context context) {
        // 先尝试使用publicDirectory
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (isFolderAvailable(downloadFolder)) {
            return downloadFolder;
        }


        // 如果publicDirectory不可用，再使用externalFilesDir
        downloadFolder = context.getExternalFilesDir(null);

        if (isFolderAvailable(downloadFolder)) {
            downloadFolder = new File(downloadFolder, "downloads");
            if (downloadFolder.exists())
                return downloadFolder;
            else if (downloadFolder.mkdir())
                return downloadFolder;
        }

        // 如果getFilesDir不可用，则尝试/sdcard/com.iplay.assistant/downloads
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            downloadFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), context.getPackageName());
            if (isFolderAvailable(downloadFolder)) {
                downloadFolder = new File(downloadFolder, "downloads");
                if (downloadFolder.exists())
                    return downloadFolder;
                else if (downloadFolder.mkdir())
                    return downloadFolder;
            }
        }

        return null;
    }

    public static boolean isFolderAvailable(File folder) {
        if (folder != null && folder.canRead() && folder.canWrite()) {
            File tmpFile = new File(folder.getPath(), "tmp");
            try {
                tmpFile.createNewFile();
                if (tmpFile.exists()) {
                    tmpFile.delete();
                    return true;
                } else {
                    return tmpFile.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } else {
            return false;
        }
    }

    public static boolean isAvailableStorageEnough(long total) {
        StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long free = (long) fs.getAvailableBlocks() * (long) fs.getBlockSize();
        return free > total;
    }

    public static class BuildProperties {

        private final Properties properties;

        private BuildProperties() throws IOException {
            properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        }

        public boolean containsKey(final Object key) {
            return properties.containsKey(key);
        }

        public boolean containsValue(final Object value) {
            return properties.containsValue(value);
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return properties.entrySet();
        }

        public String getProperty(final String name) {
            return properties.getProperty(name);
        }

        public String getProperty(final String name, final String defaultValue) {
            return properties.getProperty(name, defaultValue);
        }

        public boolean isEmpty() {
            return properties.isEmpty();
        }

        public Enumeration<Object> keys() {
            return properties.keys();
        }

        public Set<Object> keySet() {
            return properties.keySet();
        }

        public int size() {
            return properties.size();
        }

        public Collection<Object> values() {
            return properties.values();
        }

        public static BuildProperties newInstance() throws IOException {
            return new BuildProperties();
        }

    }

    public static String getUIPName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 根据当前手机的屏幕的 宽度 获取banner imageview 的比例  9/4
     * imageview 父布局需要时linearlayout
     *
     * @param context
     * @return
     */
    public static ViewGroup.LayoutParams getImageParams(Context context, ViewGroup.LayoutParams params) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        params.width = width;
        params.height = width / 16 * 9;
        return params;
    }

    public static boolean hasSIM(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT;
    }

    public static String apiLevelExchangePLatformVersion(int apiLevel) {
        switch (apiLevel) {
            case 15:
                return "Android 4.0.3";
            case 16:
                return "Android 4.1";
            case 17:
                return "Android 4.2";
            case 18:
                return "Android 4.3";
            case 19:
                return "Android 4.4";
            case 20:
                return "Android 4.4W";
            case 21:
                return "Android 5.0";
            case 22:
                return "Android 5.1";
            case 23:
                return "Android 6.0";
            case 24:
                return "Android 7.0";
            case 25:
                return "Android 7.1";
            case 26:
                return "Android 8.0";
            case 27:
                return "Android 8.1";
            case 28:
                return "Android 9.0";
            default:
                return "Android 9+";
        }
    }

    public static boolean isUnAvailable(long size) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks < size;
    }

    public static Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**
     * 对于4.4 以上的机器 如果需要动态设置 view 的paddingtop  即（view 距离上边的高度）
     *
     * @return
     */
    public static void setStatusPadding(View v) {
        if (v != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            v.setPadding(0, UtilSystemInfo.getStatusBarHeight(v.getContext()), 0, 0);
        }
    }


    public static JSONObject getCpuInfo() {
        try {
            Map<String, String> cpuInfoMap = new HashMap<String, String>();
            InputStream is = new FileInputStream("/proc/cpuinfo");
            getStringFromInputStream(is, cpuInfoMap);
            return parseCpuInfoMap(cpuInfoMap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject parseCpuInfoMap(Map<String, String> map) throws JSONException {
        String mapKeys[] = {"processorcnt", "modelname", "features", "cpuimplementer",
                "cpuarchitecture", "cpuvariant", "cpupart", "cpurevision", "hardware",
                "revision", "serial"};

        JSONObject jsonObject = new JSONObject();
        Iterator keys = map.keySet().iterator();
        if (keys != null && keys.hasNext()) {
            while (keys.hasNext()) {
                String key = (String) keys.next();

                for (String mapKey : mapKeys) {
                    if (key.replace(" ", "").toLowerCase().contains(mapKey)) {
                        jsonObject.put(mapKey, map.get(key));
                    }
                }
            }

            if (!jsonObject.has("processorcnt")) {
                jsonObject.put("processorcnt", 1);
            }
        } else {
            return null;
        }

        return jsonObject;
    }

    private static String getStringFromInputStream(InputStream is, Map formatter) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        boolean isMapInitPut = (formatter == null || formatter.size() < 1) ? false : true;
        try {
            while ((line = br.readLine()) != null) {
                if (formatter != null && line.contains(":")) {
                    String[] values = line.split(":");
                    //解析getprop,/proc/meminfo, 按需索取数据，所以map先预定义
                    if (isMapInitPut) {
                        putValueToMap(formatter, values[0], values[1]);
                    }
                    //解析CPUInfo
                    else {
                        //区分cpuinfo中的 processor(cpu个数)->{0 or 1 or 2 or 3} 和 Processor(cpu名称)->{"ARMv7 Processor rev3(v7I)"}
                        if (values[0].trim().equals("processor")) {
                            formatter.put("processorcnt", values[1].trim());
                        } else {
                            formatter.put(values[0].trim(), values[1].trim());
                        }
                    }
                }
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }

        }

        return sb.toString();
    }

    private static void putValueToMap(Map<String, String> map, String key, String val) {
        key = key.replace("[", "").replace("]", "").trim();
        val = val.replace("[", "").replace("]", "").trim();
        Iterator iterator = map.keySet().iterator();
        if (iterator != null && iterator.hasNext()) {
            while (iterator.hasNext()) {
                String lKey = (String) iterator.next();
                if (lKey.equals(key)) {
                    map.put(lKey, val);
                    break;
                }
            }
        }
    }


    /**
     * 获取运营商
     *
     * @return
     */
    public static String getProvider(Context context) {
        String provider = "Unknown";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = telephonyManager.getSubscriberId();
            Log.v("tag", "getProvider.IMSI:" + IMSI);
            if (IMSI == null) {
                if (TelephonyManager.SIM_STATE_READY == telephonyManager
                        .getSimState()) {
                    String operator = telephonyManager.getSimOperator();
                    Log.v("tag", "getProvider.operator:" + operator);
                    if (operator != null) {
                        if (operator.equals("46000")
                                || operator.equals("46002")
                                || operator.equals("46007")) {
                            provider = "中国移动";
                        } else if (operator.equals("46001")) {
                            provider = "中国联通";
                        } else if (operator.equals("46003")) {
                            provider = "中国电信";
                        }
                    }
                }
            } else {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002")
                        || IMSI.startsWith("46007")) {
                    provider = "中国移动";
                } else if (IMSI.startsWith("46001")) {
                    provider = "中国联通";
                } else if (IMSI.startsWith("46003")) {
                    provider = "中国电信";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return provider;
    }

    /**
     * 获取设备的imsi 序列码
     *
     * @param context
     * @return
     */
    public static String getImsi(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }
}