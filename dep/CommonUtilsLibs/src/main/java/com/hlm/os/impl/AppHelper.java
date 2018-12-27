package com.hlm.os.impl;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.hlm.os.core.IAppHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class AppHelper implements IAppHelper {
    private static AppHelper mHelper;
    private Application mApp;
    private PackageManager mPackageManager;

    private AppHelper(Application app) {
        this.mApp = app;
        this.mPackageManager = app.getPackageManager();
    }

    public static AppHelper share(Application app) {
        if (null == mHelper) {
            mHelper = new AppHelper(app);
        }
        return mHelper;
    }

    @Override
    public void installApp(String path, String fileAuthority) {
        try {
            File targetApk = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkUri = Uri.fromFile(targetApk);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                apkUri = FileProvider.getUriForFile(mApp, fileAuthority, targetApk);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, APKTYPE);
            } else {
                intent.addCategory(Intent.CATEGORY_DEFAULT);
            }
            intent.setDataAndType(apkUri, APKTYPE);
            mApp.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uninstallApp(String pkgName) {
        Uri uri = Uri.parse("package:" + pkgName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApp.startActivity(intent);
    }

    @Override
    public PackageInfo getPackageInfo(String pkgName) {
        try {
            return mApp.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ApplicationInfo getApplicaitonInfo(String pkgName) {
        PackageInfo info = getPackageInfo(pkgName);
        return info == null ? null : info.applicationInfo;
    }

    @Override
    public PackageInfo getPackageInfoByApk(String apkPath) {
        return mPackageManager.getPackageArchiveInfo(apkPath,0);
    }

    @Override
    public ApplicationInfo getApplicaitonInfoByApk(String pkgName) {
        PackageInfo info = getPackageInfoByApk(pkgName);
        return info == null?null:info.applicationInfo;
    }

    @Override
    public boolean isInstallApp(String pkgName) {
        return getApplicaitonInfo(pkgName) != null ? true : false;
    }

    @Override
    public int getVerCodeInstallApp(String pkgName) {
        PackageInfo info = getPackageInfo(pkgName);
        return info == null ? 0 : info.versionCode;
    }

    @Override
    public int getApkVerCode(String apkPath) {
        PackageInfo info = mPackageManager.getPackageArchiveInfo(apkPath, 0);
        return info == null ? 0 : info.versionCode;
    }

    @Override
    public boolean startApp(String pkgName) {
        Intent intent = mApp.getPackageManager().getLaunchIntentForPackage(pkgName);
        if (null != intent) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApp.startActivity(intent);
            return true;
        }
        return false;
    }


    @Override
    public String processName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    @Override
    public String processName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
