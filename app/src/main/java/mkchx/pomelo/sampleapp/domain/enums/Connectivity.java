package mkchx.pomelo.sampleapp.domain.enums;

public enum Connectivity {

    STATUS_NOT_CONNECTED(0),
    STATUS_WIFI(1),
    STATUS_MOBILE(2);

    int status;

    Connectivity(int id) {
        this.status = id;
    }

    public int getStatus() {
        return status;
    }
}
