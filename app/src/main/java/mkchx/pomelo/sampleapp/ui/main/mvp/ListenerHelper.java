package mkchx.pomelo.sampleapp.ui.main.mvp;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import mkchx.pomelo.sampleapp.domain.models.api.Photos;
import mkchx.pomelo.sampleapp.domain.models.api.Venues;

public interface ListenerHelper {

    void onInfoClick();

    void onDismissClick();

    void onGpsClick();

    boolean onMarkerClick(Marker marker);

    void onCameraIdle(LatLng latLng);

    // ~ network

    void onPhotoResponse(Photos photos);

    void onVenuesResponse(List<Venues> venues);

    void onErrorResponse();
}
