package mkchx.pomelo.sampleapp.domain.services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mkchx.pomelo.sampleapp.domain.utils.DebugUtil;

public class GeoLocationService {

    Context mContext;

    public GeoLocationService(Context context) {
        mContext = context;
    }

    public String getAddress(LatLng latLng) {

        if (latLng == null)
            return "";

        Geocoder geoCoder = new Geocoder(mContext, Locale.US);
        StringBuilder builder = new StringBuilder();

        try {

            List<Address> address = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String userLocation = "";

            if (address.size() > 0) {

                String countryCode = address.get(0).getCountryCode();

                if (countryCode.toLowerCase().equals("gr")) {

                    int maxLines = address.get(0).getMaxAddressLineIndex();
                    for (int i = 0; i < maxLines; i++) {
                        String addressStr = address.get(0).getAddressLine(i);

                        builder.append(addressStr);
                        builder.append(" ");
                    }

                } else {
                    return userLocation;
                }

                return builder.toString();
            }

            return userLocation;

        } catch (IOException e) {
            DebugUtil.output("e", "GeoLocationService", "IOException " + e.getMessage());
        } catch (NullPointerException e) {
            DebugUtil.output("e", "GeoLocationService", "NullPointerException " + e.getMessage());
        }
        return "";
    }

}
