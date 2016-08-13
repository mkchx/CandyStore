package mkchx.pomelo.sampleapp.domain.services.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mkchx.pomelo.sampleapp.domain.enums.Connectivity;
import mkchx.pomelo.sampleapp.domain.interfaces.MainInterface;
import mkchx.pomelo.sampleapp.domain.utils.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {

    MainInterface mainInterface;

    public NetworkChangeReceiver(MainInterface mainInterface) {
        this.mainInterface = mainInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int status = NetworkUtil.getConnectivityStatus(context);

        if (mainInterface != null) {
            mainInterface.getPresenter().postBus(new ConnectivityResult(status != Connectivity.STATUS_NOT_CONNECTED.getStatus()));
        }
    }

    public void destroy() {
        mainInterface = null;
    }
}