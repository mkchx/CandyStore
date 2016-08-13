package mkchx.pomelo.sampleapp.ui.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import mkchx.pomelo.sampleapp.AppStart;
import mkchx.pomelo.sampleapp.ConstKey;
import mkchx.pomelo.sampleapp.R;
import mkchx.pomelo.sampleapp.domain.interfaces.MainInterface;
import mkchx.pomelo.sampleapp.domain.models.api.Venues;
import mkchx.pomelo.sampleapp.domain.services.network.ApiWrapper;
import mkchx.pomelo.sampleapp.domain.utils.ConvertUtil;
import mkchx.pomelo.sampleapp.ui.main.mvp.MainHelper;
import mkchx.pomelo.sampleapp.ui.main.mvp.MainModel;
import mkchx.pomelo.sampleapp.ui.main.mvp.MainPresenter;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements MainInterface, MainHelper.View {

    final int GOOGLE_PLAY_SERVICES_REQUEST = 2;
    final int PERMISSIONS_REQUEST = 1;

    float mDefaultElevation;
    boolean mHasInternet, mPermissionGPS;

    GoogleMap mGoogleMap;

    DetailFragment detailFragment;
    BottomSheetBehavior mBottomSheetBehavior;

    // ~ Getters / Setters

    public ApiWrapper getApiWrapper() {
        return mApiWrapper;
    }

    public boolean getPermissionGps() {
        return mPermissionGPS;
    }

    public float getDefaultElevation() {
        return mDefaultElevation;
    }

    public ImageView getCloseButton() {
        return uiCloseButton;
    }

    public FrameLayout getWindowInfo() {
        return uiWindowInfo;
    }

    public ImageView getWindowPhoto() {
        return uiWindowPhoto;
    }

    public AppBarLayout getAppBarLayout() {
        return uiAppBarLayout;
    }

    public ActionBar getBar() {
        return mActionBar;
    }

    public DetailFragment getDetailFragment() {
        return detailFragment;
    }

    public FragmentManager getManager() {
        return mFragmentManager;
    }

    public void setDetailFragment(DetailFragment detailFragment) {
        this.detailFragment = detailFragment;
    }

    public void setHasInternet(boolean hasInternet) {
        this.mHasInternet = hasInternet;
    }

    // ~ Injection

    @Inject
    Lazy<MainPresenter> mMainPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @Inject
    Retrofit mRetrofit;

    @Inject
    Picasso mPicasso;

    ApiWrapper mApiWrapper;

    ActionBar mActionBar;
    FragmentManager mFragmentManager;

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
        getPresenter().onInfoClick();
    }

    @OnClick(R.id.close_btn)
    public void onDismissClick() {
        getPresenter().onDismissClick();
    }

    @OnClick(R.id.gps_btn)
    public void onGpsClick() {
        getPresenter().onGpsClick();
    }

    //

    public void onDetailVisibility(boolean show) {

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

    private void onBackFromDetail() {
        onDetailVisibility(false);

        DetailFragment f = (DetailFragment) mFragmentManager.findFragmentById(R.id.content_frame);

        if (f != null) {
            getPresenter().toggleFragment(this, mFragmentManager, f, false);
        }

        // we are dismissing the detail fragment, we are heading back home
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

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
                        getPresenter().createService();

                    } else {

                        // Permission Denied
                        mPermissionGPS = false;
                        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);

                        if (!showRationale) {

                        } else {

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
        getPresenter().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onResume();
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
    public MainPresenter getPresenter() {
        return mMainPresenter.get();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getPresenter().onDestroy();

        if (detailFragment != null) {
            detailFragment.destroy();
            detailFragment = null;
        }

        if (mApiWrapper != null) {
            mApiWrapper.destroy();
            mApiWrapper = null;
        }

        mToolBar = null;
        mActionBar = null;

        mRetrofit = null;
        mPicasso = null;

        mFragmentManager = null;

        uiCoordinatorLayout = null;
        uiAppBarLayout = null;

        mMainPresenter = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(ConstKey.KEY_SELECTED, mGoogleMap.getCameraPosition());
        outState.putBoolean(ConstKey.KEY_CONNECTION, mHasInternet);

        getPresenter().onSaveInstanceState(outState);

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

            if (!AppStart.getApp().getDeviceConfig().isTabletAndHorizontal()) {
                onBackFromDetail();
            }
        } else if (uiCloseButton.getVisibility() == View.VISIBLE) {
            getPresenter().toggleWindowInfo(false);
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AppStart.getApp().getApiComponent().inject(this);

        getPresenter().injectView(this);
        getPresenter().injectModel(new MainModel(getPresenter()));
        getPresenter().registerNetworkChange(this);

        mApiWrapper = new ApiWrapper(mRetrofit);
        mFragmentManager = getSupportFragmentManager();

        if (!AppStart.getApp().getDeviceConfig().isDeviceTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mActionBar = getSupportActionBar();

            if (AppStart.getApp().getDeviceConfig().isDeviceTablet()) {

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

            getPresenter().onRestoreSaved(savedInstanceState);

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

                if (resultCode == RESULT_OK) {
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

                mGoogleMap.setOnCameraIdleListener(() -> getPresenter().onCameraIdle((mGoogleMap != null ? mGoogleMap.getCameraPosition().target : null)));
                mGoogleMap.setOnMarkerClickListener(marker -> getPresenter().onMarkerClick(marker));

                updateMapSettings();
                updateCameraPosition(mGoogleMap.getCameraPosition().target);
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

    public void clearMap() {

        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
    }

    public Marker addMarkerToMap(Venues venue) {

        if (mGoogleMap != null) {
            return mGoogleMap.addMarker(new MarkerOptions()
                    .position(venue.getLocation().getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin)));
        }

        return null;
    }

    public void updateCameraPosition(LatLng latLng) {

        if (latLng != null && mGoogleMap != null) {

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    public void updateAddressInfo(String address) {

        if (uiAddressText != null) {
            uiAddressText.setText(address);
        }

        if (uiUserLocation != null && uiUserLocation.getVisibility() != View.VISIBLE) {
            uiUserLocation.setVisibility(View.VISIBLE);
        }
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
            getPresenter().createService();
        }
    }

    public void toggleBottomSheet(String address) {

        if (address.equals("")) {

            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

        } else {

            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    public void promptGpsSnack() {

        Snackbar.make(uiCoordinatorLayout, R.string.snack_gps_title, 5000)
                .setAction(R.string.snack_gps_action, view -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }).show();
    }

    @Override
    public MainActivity getActivity() {
        return this;
    }

    @Override
    public boolean hasInternet() {
        return mHasInternet;
    }

    public void clearWindowInfoForm() {
        uiWindowPhoto.setImageDrawable(null);
        uiWindowTitle.setText("");
        uiWindowAddress.setText("");
        uiWindowCategory.setText("");
        uiWindowStats.setText("");
        uiWindowStats.setVisibility(View.INVISIBLE);
    }

    public void setWindowInfoForm(Venues venues) {
        uiWindowTitle.setText(venues.getName());
        uiWindowAddress.setText((venues.getLocation() != null ? venues.getLocation().getAddress() : ""));
        uiWindowCategory.setText((venues.getCategories() != null && venues.getCategories().size() > 0 ? venues.getCategories().get(0).getName() : ""));
        uiWindowStats.setText((venues.getStats() != null ? String.valueOf(venues.getStats().getCheckinsCount()) : "0"));
        uiWindowStats.setVisibility(View.VISIBLE);
    }
}