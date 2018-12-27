package com.hlm.os.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

public interface IAppHelper {
    String ALI_PAY_PKGNAME = "com.eg.android.AlipayGphone";//支付宝包名

    String WECHAT_PKGNAME = "com.tencent.mm";//微信包名

    String APKTYPE = "application/vnd.android.package-archive";
    //安装apk
    void installApp(String path,String fileAuthority);
    //卸载apk
    void uninstallApp(String pkgName);
    //获取安装apk
    PackageInfo getPackageInfo(String pkgName);

    ApplicationInfo getApplicaitonInfo(String pkgName);

    PackageInfo getPackageInfoByApk(String apkPath);

    ApplicationInfo getApplicaitonInfoByApk(String pkgName);
    //判断是否安装
    boolean isInstallApp(String pkgName);

    int getVerCodeInstallApp(String pkgName);

    int getApkVerCode(String apkPath);
    //打开app
    boolean startApp(String path);

    String processName(Context context,int pid);

    String processName();

}
