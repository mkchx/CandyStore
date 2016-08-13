package mkchx.pomelo.sampleapp.domain.services.network;

public class ConnectivityResult {

    boolean connected;

    public ConnectivityResult(boolean status) {
        connected = status;
    }

    public boolean isConnected() {
        return connected;
    }
}
