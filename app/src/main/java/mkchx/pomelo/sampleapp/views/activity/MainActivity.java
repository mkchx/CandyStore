package mkchx.pomelo.sampleapp.views.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mkchx.pomelo.sampleapp.ConstKey;
import mkchx.pomelo.sampleapp.R;
import mkchx.pomelo.sampleapp.interfaces.LocationInterface;
import mkchx.pomelo.sampleapp.interfaces.MainInterface;
import mkchx.pomelo.sampleapp.interfaces.ResponseHandler;
import mkchx.pomelo.sampleapp.models.api.Items;
import mkchx.pomelo.sampleapp.models.api.Photos;
import mkchx.pomelo.sampleapp.models.api.Venues;
import mkchx.pomelo.sampleapp.models.dto.AnimDto;
import mkchx.pomelo.sampleapp.models.dto.FoursquareDto;
import mkchx.pomelo.sampleapp.services.DeviceConfig;
import mkchx.pomelo.sampleapp.services.LocationService;
import mkchx.pomelo.sampleapp.utils.AnimationUtil;
import mkchx.pomelo.sampleapp.utils.ConvertUtil;
import mkchx.pomelo.sampleapp.views.fragments.DetailFragment;

public class MainActivity extends BaseFragmentActivity implements MainInterface, LocationInterface, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    final int GOOGLE_PLAY_SERVICES_REQUEST = 2;
    final int PERMISSIONS_REQUEST = 1;

    LatLng mUserLatLng;
    String mAddress;
    Marker mSelected;
    Photos mPhotos;
    Venues mVenues;

    HashMap<String, Venues> mCollection = new HashMap<>();

    float mDefaultElevation;
    boolean mHasInternet, mPermissionGPS;
    AnimDto mRevealDto, mHideDto;

    LocationService mLocationService;
    GoogleMap mGoogleMap;

    DetailFragment detailFragment;
    BottomSheetBehavior mBottomSheetBehavior;

    Handler mHandler = new Handler();
    Runnable mRunnable = () -> {

        // Get the address based on the maps center lat,lng
        if (mLocationService != null) {
            mAddress = mLocationService.getGeoLocation().getAddress(mUserLatLng);

            toggleBottomSheet();
            updateAddressInfo();
        }

        // toggleWindowInfo(false);
        fetchVenues(mUserLatLng);
    };

    // ~ Bindings

    @BindView(R.id.content_frame)
    FrameLayout uiDetailFrame;

    @BindView(R.id.bottom_sheet)
    View uiBottomSheet;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout uiCoordinatorLayout;

    @BindView(R.id.progress_bar)
    ProgressBar uiProgressBar;

    @BindView(R.id.close_btn)
    ImageView uiCloseButton;

    @BindView(R.id.user_location)
    AppCompatImageView uiUserLocation;

    @BindView(R.id.address_text)
    TextView uiAddressText;

    @BindView(R.id.toolbar_title)
    TextView uiTextTitle;

    @BindView(R.id.app_bar_layout)
    AppBarLayout uiAppBarLayout;


    // window info

    @BindView(R.id.window_info)
    FrameLayout uiWindowInfo;

    @BindView(R.id.window_photo)
    ImageView uiWindowPhoto;

    @BindView(R.id.window_title)
    TextView uiWindowTitle;

    @BindView(R.id.window_address)
    TextView uiWindowAddress;

    @BindView(R.id.window_category)
    TextView uiWindowCategory;

    @BindView(R.id.window_stats)
    TextView uiWindowStats;

    // Listeners

    @OnClick(R.id.info_container)
    public void onInfoClick() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstKey.KEY_VENUE, mVenues);
        bundle.putParcelable(ConstKey.KEY_PHOTO, mPhotos);

        createDetailFragment(bundle);
    }

    @OnClick(R.id.close_btn)
    public void onDismissClick() {
        toggleWindowInfo(false);
    }

    @OnClick(R.id.gps_btn)
    public void onGpsClick() {

        // request the location once more
        if (mLocationService != null && mLocationService.isGpsEnabled()) {
            mLocationService.onResume();
        } else {
            promptGpsSnack();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectivityEvent(ConnectivityChanged event) {

        ConnectivityStatus status = event.getConnectivityStatus();

        if (status == ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET ||
                status == ConnectivityStatus.MOBILE_CONNECTED) {
            mHasInternet = true;

        } else {

            mHasInternet = false;
        }
    }

    //

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSIONS_REQUEST: {

                Map<String, Integer> perms = new HashMap<>();

                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if (perms.size() > 0) {

                    if (perms.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        // All Permissions Granted
                        mPermissionGPS = true;
                        createService();

                    } else {

                        // Permission Denied
                        mPermissionGPS = false;
                        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);

                        if (!showRationale) {

                            // user denied flagging NEVER ASK AGAIN
                            // either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting

                        } else {

                            // user denied WITHOUT never ask again
                            // a good place to explain the user
                            // why you need the permission and ask if he want
                            // to accept it (the rationale)

                            Snackbar.make(uiCoordinatorLayout, getString(R.string.gps_permission), Snackbar.LENGTH_LONG)
                                    .setAction(getResources().getString(R.string.permissionEnable), v -> {
                                        requestPermissions();
                                    }).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        mNetworkEvents.unregister();

        if (mLocationService != null) {
            mLocationService.onPause();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
        mNetworkEvents.register();

        if (mLocationService != null) {
            mLocationService.onResume();
        }
    }

    @Override
    public boolean isConnected() {
        return mHasInternet;
    }

    @Override
    public boolean gpsGoodness() {
        return mPermissionGPS;
    }

    @Override
    public DeviceConfig getDeviceConfig() {
        return mDeviceConfig;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (detailFragment != null) {
            detailFragment.destroy();
            detailFragment = null;
        }

        if (mLocationService != null) {
            mLocationService.onDestroy();
            mLocationService = null;
        }

        mRevealDto = null;
        mHideDto = null;

        mFragmentManager = null;

        uiCoordinatorLayout = null;
        uiAppBarLayout = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(ConstKey.KEY_SELECTED, mGoogleMap.getCameraPosition());

        outState.putParcelable(ConstKey.KEY_VENUE, mVenues);
        outState.putParcelable(ConstKey.KEY_PHOTO, mPhotos);

        outState.putBoolean(ConstKey.KEY_CONNECTION, mHasInternet);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackFromDetail();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (detailFragment != null && detailFragment.isVisible()) {

            if (!mDeviceConfig.isTabletAndHorizontal()) {
                onBackFromDetail();
            }
        } else if (uiCloseButton.getVisibility() == View.VISIBLE) {
            toggleWindowInfo(false);
        } else {
            finish();
        }
    }

    private void onBackFromDetail() {
        onDetailVisibility(false);

        DetailFragment f = (DetailFragment) mFragmentManager.findFragmentById(R.id.content_frame);

        if (f != null) {
            toggleFragment(f, false);
        }

        // we are dismissing the detail fragment, we are heading back home
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    private void onDetailVisibility(boolean show) {

        uiBottomSheet.setVisibility((show ? View.GONE : View.VISIBLE));
        uiWindowInfo.setVisibility((show ? View.GONE : View.VISIBLE));
        uiCloseButton.setVisibility((show ? View.GONE : View.VISIBLE));

        uiDetailFrame.setVisibility((show ? View.VISIBLE : View.GONE));
    }

    public void toggleProgressAnim(boolean show) {

        if (uiProgressBar != null) {
            uiProgressBar.setVisibility((show ? View.VISIBLE : View.GONE));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setFragmentManager(getSupportFragmentManager());

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mActionBar = getSupportActionBar();

            if (mDeviceConfig.isDeviceTablet()) {

                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolBar.getLayoutParams();
                CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) uiAppBarLayout.getLayoutParams();

                params.setScrollFlags(0);
                appBarLayoutParams.setBehavior(null);
                uiAppBarLayout.setLayoutParams(appBarLayoutParams);
            }

            uiTextTitle.setText(getString(R.string.app_name));
        }

        mDefaultElevation = ConvertUtil.dpToPx(this, 8);

        mBottomSheetBehavior = BottomSheetBehavior.from(uiBottomSheet);
        mBottomSheetBehavior.setPeekHeight((int) ConvertUtil.dpToPx(this, 50));
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(ConstKey.KEY_CONNECTION)) {
                mHasInternet = savedInstanceState.getBoolean(ConstKey.KEY_CONNECTION);
            }

            if (savedInstanceState.containsKey(ConstKey.KEY_GPS)) {
                mPermissionGPS = savedInstanceState.getBoolean(ConstKey.KEY_GPS);
            }

            if (savedInstanceState.containsKey(ConstKey.KEY_SELECTED)) {
                // restore position
                CameraPosition cameraPosition = savedInstanceState.getParcelable(ConstKey.KEY_SELECTED);

                if (cameraPosition != null) {
                    mUserLatLng = cameraPosition.target;
                    updateCameraPosition();
                }
            }

            if (savedInstanceState.containsKey(ConstKey.KEY_VENUE)) {
                mVenues = savedInstanceState.getParcelable(ConstKey.KEY_VENUE);

                // case we had window info opened, before orientation happen
                // trigger the marker click event, update the view
                if (mVenues != null) {

                    loadWindowInfoData();

                    ViewCompat.setElevation(uiAppBarLayout, 0);
                }
            }

            if (savedInstanceState.containsKey(ConstKey.KEY_PHOTO)) {
                mPhotos = savedInstanceState.getParcelable(ConstKey.KEY_PHOTO);
            }

            onInfoClick();
        }

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);

        if (code == ConnectionResult.SUCCESS) {
            requestPermissions();
            onMapSetup();
        } else if (api.isUserResolvableError(code) && api.showErrorDialogFragment(this, code, GOOGLE_PLAY_SERVICES_REQUEST)) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GOOGLE_PLAY_SERVICES_REQUEST:

                if (resultCode == Activity.RESULT_OK) {
                    onMapSetup();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onMapSetup() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(googleMap -> {

            mGoogleMap = googleMap;

            if (mGoogleMap != null) {

                mGoogleMap.setOnCameraChangeListener(this);
                mGoogleMap.setOnMarkerClickListener(this);

                updateMapSettings();
                updateCameraPosition();
            }
        });
    }

    private void updateMapSettings() {

        UiSettings uiSettings = mGoogleMap.getUiSettings();

        uiSettings.setMapToolbarEnabled(false);

        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);

        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
    }

    private void updateCameraPosition() {

        if (mUserLatLng != null && mGoogleMap != null) {

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mUserLatLng, 13);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    private void updateAddressInfo() {

        if (uiAddressText != null) {
            uiAddressText.setText(mAddress);
        }

        if (uiUserLocation != null && uiUserLocation.getVisibility() != View.VISIBLE) {
            uiUserLocation.setVisibility(View.VISIBLE);
        }
    }

    private void fetchPhotos(String venueId) {

        Map<String, String> params = new HashMap<>();
        params.put("limit", "1");
        params.put("client_id", getString(R.string.fsq_client_id));
        params.put("client_secret", getString(R.string.fsq_client_secret));
        params.put("v", "20160723");

        mApiWrapper.getVenuePhotos(venueId, params, new ResponseHandler() {

            @Override
            public void onResponse(Object data) {

                FoursquareDto foursquareDto = (FoursquareDto) data;

                // we limit the request to 1, if there's a photo, we will get it
                mPhotos = foursquareDto.getResponse().getPhotos();

                if (!mDeviceConfig.isTabletAndHorizontal()) {

                    if (mPhotos.getCount() > 0) {

                        Items item = mPhotos.getItems().get(0);
                        // TODO: image quality based on device density
                        Picasso.with(MainActivity.this).load(item.getPrefix() + "200x200" + item.getSuffix()).into(uiWindowPhoto);
                    } else {
                        uiWindowPhoto.setImageDrawable(null);
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, R.string.api_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchVenues(LatLng latLng) {

        Map<String, String> params = new HashMap<>();
        params.put("ll", latLng.latitude + "," + latLng.longitude);
        params.put("categoryId", "4bf58dd8d48988d117951735");
        params.put("venuePhotos", "1");
        params.put("client_id", getString(R.string.fsq_client_id));
        params.put("client_secret", getString(R.string.fsq_client_secret));
        params.put("v", "20160722");

        setVenueBasedOnSelectedMarker();

        mGoogleMap.clear();
        mCollection.clear();

        mApiWrapper.getSearchItems(params, new ResponseHandler() {

            @Override
            public void onResponse(Object data) {

                FoursquareDto foursquareDto = (FoursquareDto) data;

                for (Venues venue : foursquareDto.getResponse().getVenues()) {

                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(venue.getLocation().getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin)));

                    // if we have previously clicked a marker, find which is and make it active
                    if (mVenues != null && venue.getId().equals(mVenues.getId())) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin_selected));
                        mSelected = marker;
                    }

                    mCollection.put(marker.getId(), venue);
                }

                toggleProgressAnim(false);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, R.string.api_failure, Toast.LENGTH_SHORT).show();
                toggleProgressAnim(false);
            }
        });
    }

    private int checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {

        mPermissionGPS = (checkLocationPermission() == PackageManager.PERMISSION_GRANTED);

        if (!mPermissionGPS) {

            List<String> requestPermissions = new ArrayList<>();

            if (checkLocationPermission() != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (requestPermissions.size() > 0) {
                ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[requestPermissions.size()]), PERMISSIONS_REQUEST);
            }

        } else {

            createService();
        }
    }

    private void createService() {

        mLocationService = new LocationService(this, mPermissionGPS);
        mLocationService.onStart();

        if (!mLocationService.isGpsEnabled()) {
            promptGpsSnack();
        }
    }

    private void toggleBottomSheet() {

        if (mAddress.equals("")) {

            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

        } else {

            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    private void promptGpsSnack() {

        Snackbar.make(uiCoordinatorLayout, R.string.snack_gps_title, 5000)
                .setAction(R.string.snack_gps_action, view -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }).show();
    }

    @Override
    public void updateLocation(LatLng latLng, String address) {

        // pause location updates
        if (mLocationService != null) {
            mLocationService.onPause();
        }

        if (address.equals("")) {
            return;
        }

        mUserLatLng = latLng;
        mAddress = address;

        toggleProgressAnim(true);
        updateCameraPosition();
        updateAddressInfo();

        fetchVenues(mUserLatLng);

        // Expand the bottom sheet, showing NOW the address
        toggleBottomSheet();
    }

    @Override
    public void onCameraChange(CameraPosition cp) {

        mUserLatLng = cp.target;

        if (mUserLatLng.latitude != 0f && mUserLatLng.longitude != 0f) {

            toggleProgressAnim(true);

            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, 500);

        } else {
            toggleProgressAnim(false);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        boolean windowOpen = false;

        if (mSelected != null) {

            windowOpen = true;

            // if user clicked on the same marker, do nothing
            if (mSelected.getId().equals(marker.getId()))
                return true;

            // set previous selected marker icon back to default icon
            mSelected.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin));
        }

        mSelected = marker;

        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin_selected));

        if (mDeviceConfig.isTabletAndHorizontal()) {

            setVenueBasedOnSelectedMarker();

            if (mVenues != null) {
                fetchPhotos(mVenues.getId());
                onInfoClick();
            }
        } else {

            // update the view skipping animation
            if (windowOpen)
                loadWindowInfoData();
            else
                toggleWindowInfo(true);
        }

        return true;
    }

    private void toggleWindowInfo(boolean show) {

        if (Build.VERSION.SDK_INT >= 21) {

            // create our anim dto data once
            initAnimDto();

            if (uiWindowInfo.getVisibility() != View.VISIBLE && show) {

                ViewCompat.setElevation(uiAppBarLayout, 0);
                AnimationUtil.revealEffect(mRevealDto);

            } else if (uiWindowInfo.getVisibility() == View.VISIBLE && !show) {
                AnimationUtil.hideEffect(mHideDto);
            }

        } else {

            if (uiWindowInfo.getVisibility() != View.VISIBLE && show) {
                uiWindowInfo.setVisibility(View.VISIBLE);
                loadWindowInfoData();
            } else if (uiWindowInfo.getVisibility() == View.VISIBLE && !show) {
                uiWindowInfo.setVisibility(View.INVISIBLE);
                clearWindowInfoData();
            }
        }

        uiCloseButton.setVisibility((show ? View.VISIBLE : View.GONE));
    }

    private void initAnimDto() {

        if (mRevealDto == null) {

            int cx = uiWindowInfo.getWidth() / 2;
            int cy = 0;
            int r = Math.max(uiWindowInfo.getWidth(), uiWindowInfo.getHeight());

            mRevealDto = new AnimDto(uiWindowInfo, cx, cy, r, this::loadWindowInfoData);
        }

        if (mHideDto == null) {

            int cx = uiWindowInfo.getWidth() / 2;
            int cy = 0;
            int r = Math.max(uiWindowInfo.getWidth(), uiWindowInfo.getHeight());

            mHideDto = new AnimDto(uiWindowInfo, cx, cy, r, () -> {
                ViewCompat.setElevation(uiAppBarLayout, mDefaultElevation);
                clearWindowInfoData();
            });
        }
    }

    private void loadWindowInfoData() {

        setVenueBasedOnSelectedMarker();

        if (mVenues != null) {
            fetchPhotos(mVenues.getId());

            uiWindowTitle.setText(mVenues.getName());
            uiWindowAddress.setText((mVenues.getLocation() != null ? mVenues.getLocation().getAddress() : ""));
            uiWindowCategory.setText((mVenues.getCategories() != null && mVenues.getCategories().size() > 0 ? mVenues.getCategories().get(0).getName() : ""));
            uiWindowStats.setText((mVenues.getStats() != null ? String.valueOf(mVenues.getStats().getCheckinsCount()) : "0"));
            uiWindowStats.setVisibility(View.VISIBLE);
        }
    }

    private void setVenueBasedOnSelectedMarker() {

        for (Map.Entry<String, Venues> entry : mCollection.entrySet()) {

            // get the active venue based on marker id's
            if (mSelected != null && mSelected.getId().equals(entry.getKey())) {
                mVenues = entry.getValue();
            }
        }
    }

    private void clearWindowInfoData() {

        uiWindowPhoto.setImageDrawable(null);
        uiWindowTitle.setText("");
        uiWindowAddress.setText("");
        uiWindowCategory.setText("");
        uiWindowStats.setText("");
        uiWindowStats.setVisibility(View.INVISIBLE);

        if (mSelected != null) {
            mSelected.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin));
            mSelected = null;
        }
    }

    // Fragment ops

    public void createDetailFragment(Bundle args) {

        if (!mDeviceConfig.isTabletAndHorizontal()) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            onDetailVisibility(true);
        }

        if (detailFragment == null) {
            detailFragment = new DetailFragment().newInstance(args);

            createFragment(detailFragment, R.id.content_frame);

        } else {

            toggleFragment(detailFragment, true);
            setVenueBasedOnSelectedMarker();

            detailFragment.updateDetailView(mVenues, mPhotos);
        }

        // TODO add Shared elements
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            detailFragment.setSharedElementEnterTransition(new DetailTransition());
            *//*detailFragment.setEnterTransition(new Explode());
            detailFragment.setExitTransition(new Explode());*//*
            detailFragment.setSharedElementReturnTransition(new DetailTransition());
        }
        */
    }
}