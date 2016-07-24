package mkchx.pomelo.sampleapp.interfaces;

import mkchx.pomelo.sampleapp.services.DeviceConfig;

public interface MainInterface {
    boolean isConnected();
    boolean gpsGoodness();

    DeviceConfig getDeviceConfig();
}
