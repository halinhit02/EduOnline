package com.halinhit.eduonline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetConnection extends BroadcastReceiver {

    public static int TYPE_MOBILE = 1;
    public static int TYPE_WIFI = 1;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = CheckInternetConnection.getConnectivityStatus(context);
        String status = null;
        if (conn == CheckInternetConnection.TYPE_WIFI) {
            status = "Đã kết nối mạng Wifi";
        } else if (conn == CheckInternetConnection.TYPE_MOBILE) {
            status = "Đã kết nối mạng di động";
        } else if (conn == CheckInternetConnection.TYPE_NOT_CONNECTED) {
            status = "Không có kết nối";
        }
        return status;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = CheckInternetConnection.getConnectivityStatusString(context);

    }
}
