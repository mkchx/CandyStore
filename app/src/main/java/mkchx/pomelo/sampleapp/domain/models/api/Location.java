package mkchx.pomelo.sampleapp.domain.models.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Location implements Parcelable {

    String address;
    double lat;
    double lng;
    String postalCode;
    String cc;
    String city;
    String state;
    String country;
    String[] formattedAddress;

    public String[] getFormattedAddress() {
        return formattedAddress;
    }

    public String getAddress() {
        return address;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCc() {
        return cc;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.postalCode);
        dest.writeString(this.cc);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.country);
        dest.writeStringArray(this.formattedAddress);
    }

    public Location() {
    }

    protected Location(Parcel in) {
        this.address = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.postalCode = in.readString();
        this.cc = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.country = in.readString();
        this.formattedAddress = in.createStringArray();
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}