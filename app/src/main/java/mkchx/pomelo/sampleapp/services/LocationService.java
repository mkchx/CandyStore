package mkchx.pomelo.sampleapp.services;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import mkchx.pomelo.sampleapp.interfaces.LocationInterface;

public class LocationService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    LocationInterface locationInterface;

    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;
    LocationManager mLocationManager;
    Location mCurrentLocation;

    GeoLocation mGeoLocation;

    Context mContext;

    boolean mPermissionGps;

    public LocationService(Activity activity, boolean permissionGps) {
        this.mContext = activity.getApplicationContext();
        this.mPermissionGps = permissionGps;
        this.mGeoLocation = new GeoLocation(activity.getApplicationContext());

        this.locationInterface = (LocationInterface) activity;


        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    private void createLocationRequest() {

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        mLocationRequest = LocationRequest.create();

        // mLocationRequest.setNumUpdates(1);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public GeoLocation getGeoLocation() {
        return mGeoLocation;
    }

    public void setLocation(Location location) {
        this.mCurrentLocation = location;
    }

    public Location getLocation() {
        return mCurrentLocation;
    }

    public boolean isGpsEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {

        // case gps was off and we failed getting the user location
        if (mCurrentLocation == null && isGpsEnabled()) {
            mCurrentLocation = location;
            updateTheLocation();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {

        if (mPermissionGps) {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mCurrentLocation != null && locationInterface != null) {
                updateTheLocation();
            }
        }
    }

    private void updateTheLocation() {

        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        locationInterface.updateLocation(latLng, mGeoLocation.getAddress(latLng));
    }

    //

    public void onDestroy() {

        if (mGoogleApiClient != null) {

            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }

        locationInterface = null;

        mLocationManager = null;
        mLocationRequest = null;
        mCurrentLocation = null;
    }

    public void onStart() {

        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void onResume() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    public void onPause() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //
}
