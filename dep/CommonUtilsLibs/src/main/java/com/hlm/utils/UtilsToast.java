package com.hlm.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


public class UtilsToast {
    public static void show(final Context context, final String msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void show(final Context context, int txtId){
        String msg = context.getResources().getString(txtId);
        show(context,msg);
    }
}
