package mkchx.pomelo.sampleapp.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface LocationInterface {

    void updateLocation(LatLng latLng, String address);
}
