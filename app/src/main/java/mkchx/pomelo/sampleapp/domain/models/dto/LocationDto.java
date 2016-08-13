package mkchx.pomelo.sampleapp.domain.models.dto;

import com.google.android.gms.maps.model.LatLng;

public class LocationDto {

    LatLng latLng;
    String address;

    public LocationDto(LatLng latLng, String address) {
        this.latLng = latLng;
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getAddress() {
        return address;
    }
}
