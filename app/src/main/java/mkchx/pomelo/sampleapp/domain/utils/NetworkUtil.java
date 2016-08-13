package mkchx.pomelo.sampleapp.domain.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import mkchx.pomelo.sampleapp.domain.enums.Connectivity;

public class NetworkUtil {

    public static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return Connectivity.STATUS_WIFI.getStatus();

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return Connectivity.STATUS_MOBILE.getStatus();
        }

        return Connectivity.STATUS_NOT_CONNECTED.getStatus();
    }
}
