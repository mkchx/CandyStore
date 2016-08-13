package mkchx.pomelo.sampleapp.ui.main.mvp;

import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mkchx.pomelo.sampleapp.AppStart;
import mkchx.pomelo.sampleapp.ConstKey;
import mkchx.pomelo.sampleapp.R;
import mkchx.pomelo.sampleapp.domain.interfaces.MainInterface;
import mkchx.pomelo.sampleapp.domain.models.api.Items;
import mkchx.pomelo.sampleapp.domain.models.api.Photos;
import mkchx.pomelo.sampleapp.domain.models.api.Venues;
import mkchx.pomelo.sampleapp.domain.models.dto.AnimDto;
import mkchx.pomelo.sampleapp.domain.models.dto.LocationDto;
import mkchx.pomelo.sampleapp.domain.services.LocationService;
import mkchx.pomelo.sampleapp.domain.services.network.ConnectivityResult;
import mkchx.pomelo.sampleapp.domain.services.network.NetworkChangeReceiver;
import mkchx.pomelo.sampleapp.domain.utils.AnimationUtil;
import mkchx.pomelo.sampleapp.domain.utils.DebugUtil;
import mkchx.pomelo.sampleapp.ui.main.DetailFragment;
import mkchx.pomelo.sampleapp.ui.main.MainActivity;

public class MainPresenter implements MainHelper.Presenter {

    WeakReference<MainHelper.View> mMainView;
    MainHelper.Model mMainModel;

    NetworkChangeReceiver mNetworkChangeReceiver;

    AnimDto mRevealDto, mHideDto;
    HashMap<String, Venues> mCollection = new HashMap<>();

    Venues mVenues;
    Photos mPhotos;

    Marker mSelected;


    LatLng mUserLatLng;
    String mAddress;

    LocationService mLocationService;
    Handler mHandler = new Handler();
    Runnable mRunnable = () -> {

        MainActivity a = getView().getActivity();

        // Get the address based on the maps center lat,lng
        if (mLocationService != null) {
            mAddress = mLocationService.getGeoLocation().getAddress(mUserLatLng);

            a.toggleBottomSheet(mAddress);
            a.updateAddressInfo(mAddress);
        }

        mMainModel.fetchVenues(mUserLatLng);
    };

    public void injectView(MainHelper.View mainView) {
        mMainView = new WeakReference<>(mainView);
    }

    public void registerNetworkChange(MainInterface that) {
        mNetworkChangeReceiver = new NetworkChangeReceiver(that);
    }

    public void injectModel(MainHelper.Model mainModel) {
        mMainModel = mainModel;
    }

    @Override
    public boolean hasView() {
        return (mMainView != null);
    }

    /**
     * @return the view's instance if not null
     * @throws NullPointerException
     */
    @Override
    public MainHelper.View getView() throws NullPointerException {

        try {
            return mMainView.get();
        } catch (Exception e) {
            throw new NullPointerException();
        }
    }

    @Override
    public void onDestroy() {

        mMainView = null;
        mMainModel = null;
        mRevealDto = null;
        mHideDto = null;

        if (mNetworkChangeReceiver != null) {
            mNetworkChangeReceiver.destroy();
            mNetworkChangeReceiver = null;
        }

        if (mLocationService != null) {
            mLocationService.onDestroy();
            mLocationService = null;
        }
    }

    @Override
    public void onStart() {
        // TODO: implement
    }

    @Override
    public void onPause() {
        unregisterBus();

        if (mLocationService != null) {
            mLocationService.onPause();
        }
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        getView().getActivity().registerReceiver(mNetworkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        if (mLocationService != null) {
            mLocationService.onResume();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle ouState) {
        ouState.putParcelable(ConstKey.KEY_VENUE, mVenues);
        ouState.putParcelable(ConstKey.KEY_PHOTO, mPhotos);
    }

    @Override
    public void onRestoreSaved(Bundle savedInstanceState) {

        MainActivity a = getView().getActivity();

        if (savedInstanceState.containsKey(ConstKey.KEY_SELECTED)) {

            // restore position
            CameraPosition cameraPosition = savedInstanceState.getParcelable(ConstKey.KEY_SELECTED);

            if (cameraPosition != null) {
                mUserLatLng = cameraPosition.target;
                a.updateCameraPosition(mUserLatLng);
            }
        }

        if (savedInstanceState.containsKey(ConstKey.KEY_VENUE)) {
            mVenues = savedInstanceState.getParcelable(ConstKey.KEY_VENUE);

            // case we had window info opened, before orientation happen
            // trigger the marker click event, update the view
            if (mVenues != null) {

                loadWindowInfoData();

                ViewCompat.setElevation(a.getAppBarLayout(), 0);
            }
        }

        if (savedInstanceState.containsKey(ConstKey.KEY_PHOTO)) {
            mPhotos = savedInstanceState.getParcelable(ConstKey.KEY_PHOTO);
        }
    }

    // ~ Subscribers

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkInternetAvailability(ConnectivityResult event) {

        if (event != null) {
            getView().getActivity().setHasInternet(event.isConnected());
        }
    }

    @Subscribe
    public void updateLocation(LocationDto locationDto) {

        MainActivity a = getView().getActivity();

        // pause location updates
        if (mLocationService != null) {
            mLocationService.onPause();
        }

        if (locationDto.getAddress().equals("")) {
            return;
        }

        mUserLatLng = locationDto.getLatLng();
        mAddress = locationDto.getAddress();

        a.toggleProgressAnim(true);
        a.updateCameraPosition(mUserLatLng);
        a.updateAddressInfo(mAddress);

        mMainModel.fetchVenues(mUserLatLng);

        // Expand the bottom sheet, showing NOW the address
        a.toggleBottomSheet(mAddress);
    }

    /**
     * Registers the given subscriber to receive events
     */
    @Override
    public void registerBus() {

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * Unregister the subscriber
     */
    @Override
    public void unregisterBus() {

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().removeAllStickyEvents();
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * Posts the given object to the event bus
     *
     * @param o object
     */
    @Override
    public void postBus(Object o) {
        EventBus.getDefault().post(o);
    }


    /**
     * Show / hide fragment
     *
     * @param activity instance of Activity
     * @param fm       instance of fragment manager
     * @param fragment the fragment to toggle
     * @param show     action visibility
     */
    @Override
    public void toggleFragment(AppCompatActivity activity, FragmentManager fm, Fragment fragment, boolean show) {

        if (!activity.isFinishing() && fragment != null) {

            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            if (show) {

                fragmentTransaction.show(fragment);

            } else {
                fragmentTransaction.hide(fragment);
            }

            fragmentTransaction.commit();
        }
    }

    /**
     * creates a new fragment and adds it to the backstack
     *
     * @param a        instance of activity
     * @param fm       instance of fragment manager
     * @param fragment the newly created fragment instance
     * @param resId    the layout id to append
     */
    @Override
    public void createFragment(AppCompatActivity a, FragmentManager fm, Fragment fragment, int resId) {

        if (!a.isFinishing() && fragment != null && resId != 0) {

            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            Fragment existedFragment = fm.findFragmentById(resId);

            if (existedFragment instanceof DetailFragment) {
                fragmentTransaction.replace(resId, fragment, ((Object) fragment).getClass().getName());
            } else {
                fragmentTransaction.add(resId, fragment, ((Object) fragment).getClass().getName());
                fragmentTransaction.addToBackStack(((Object) fragment).getClass().getName());
            }

            fragmentTransaction.commit();
        }
    }

    @Override
    public void onInfoClick() {

        MainActivity a = getView().getActivity();

        Bundle args = new Bundle();
        args.putParcelable(ConstKey.KEY_VENUE, mVenues);
        args.putParcelable(ConstKey.KEY_PHOTO, mPhotos);

        if (!AppStart.getApp().getDeviceConfig().isTabletAndHorizontal()) {
            a.getBar().setDisplayHomeAsUpEnabled(true);
            a.onDetailVisibility(true);
        }

        if (a.getDetailFragment() == null) {
            a.setDetailFragment(new DetailFragment().newInstance(args));

            createFragment(a, a.getManager(), a.getDetailFragment(), R.id.content_frame);

        } else {

            toggleFragment(a, a.getManager(), a.getDetailFragment(), true);

            setVenueBasedOnSelectedMarker();

            if (a.getDetailFragment() != null) {
                a.getDetailFragment().updateDetailView(mVenues, mPhotos);
            }
        }
    }

    @Override
    public void onDismissClick() {
        toggleWindowInfo(false);
    }

    @Override
    public void onGpsClick() {

        // request the location once more
        if (mLocationService != null && mLocationService.isGpsEnabled()) {
            mLocationService.onResume();
        } else {
            getView().getActivity().promptGpsSnack();
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

        if (AppStart.getApp().getDeviceConfig().isTabletAndHorizontal()) {

            setVenueBasedOnSelectedMarker();

            if (mVenues != null) {
                mMainModel.fetchPhotos(mVenues.getId());
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

    @Override
    public void onCameraIdle(LatLng latLng) {

        DebugUtil.output("e", "camera", "idle");

        if (latLng != null) {

            MainActivity a = getView().getActivity();

            mUserLatLng = latLng;

            if (mUserLatLng.latitude != 0f && mUserLatLng.longitude != 0f) {

                a.toggleProgressAnim(true);

                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 500);

            } else {
                a.toggleProgressAnim(false);
            }
        }
    }

    @Override
    public void toggleWindowInfo(boolean show) {

        MainActivity a = getView().getActivity();

        FrameLayout uiWindowInfo = a.getWindowInfo();
        AppBarLayout uiAppBarLayout = a.getAppBarLayout();

        if (Build.VERSION.SDK_INT >= 21) {

            // create our anim dto data once
            initAnimDto();

            if (uiWindowInfo.getVisibility() != View.VISIBLE && show) {

                ViewCompat.setElevation(uiAppBarLayout, 0);
                AnimationUtil.revealEffect(mRevealDto);

            } else if (uiWindowInfo.getVisibility() == View.VISIBLE && !show) {
                AnimationUtil.hideEffect(mHideDto);
                mSelected = null;
            }

        } else {

            if (uiWindowInfo.getVisibility() != View.VISIBLE && show) {
                uiWindowInfo.setVisibility(View.VISIBLE);
                loadWindowInfoData();
            } else if (uiWindowInfo.getVisibility() == View.VISIBLE && !show) {
                uiWindowInfo.setVisibility(View.INVISIBLE);

                if (mSelected != null) {
                    mSelected.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin));
                    mSelected = null;
                }

                getView().getActivity().clearWindowInfoForm();
            }
        }

        a.getCloseButton().setVisibility((show ? View.VISIBLE : View.GONE));
    }

    @Override
    public void setVenueBasedOnSelectedMarker() {

        for (Map.Entry<String, Venues> entry : mCollection.entrySet()) {

            // get the active venue based on marker id's
            if (mSelected != null && mSelected.getId().equals(entry.getKey())) {
                mVenues = entry.getValue();
            }
        }
    }

    @Override
    public void clearData() {
        mCollection.clear();
        getView().getActivity().clearMap();
    }

    @Override
    public void loadWindowInfoData() {

        setVenueBasedOnSelectedMarker();

        if (mVenues != null) {

            mMainModel.fetchPhotos(mVenues.getId());

            getView().getActivity().setWindowInfoForm(mVenues);
        }
    }

    @Override
    public void createService() {

        MainActivity a = getView().getActivity();

        mLocationService = new LocationService(this, a.getPermissionGps());
        mLocationService.onStart();

        if (!mLocationService.isGpsEnabled()) {
            a.promptGpsSnack();
        }
    }

    @Override
    public void onPhotoResponse(Photos photos) {

        mPhotos = photos;

        if (!AppStart.getApp().getDeviceConfig().isTabletAndHorizontal()) {

            MainActivity a = getView().getActivity();

            if (mPhotos.getCount() > 0) {

                Items item = mPhotos.getItems().get(0);
                // TODO: image quality based on device density
                Picasso.with(a).load(item.getPrefix() + "200x200" + item.getSuffix()).into(a.getWindowPhoto());

            } else {
                a.getWindowPhoto().setImageDrawable(null);
            }
        }
    }

    @Override
    public void onVenuesResponse(List<Venues> venues) {

        mVenues = null;
        mSelected = null;

        for (Venues venue : venues) {

            Marker marker = getView().getActivity().addMarkerToMap(venue);

            if (marker != null) {

                // if we have previously clicked a marker, find which is and make it active
                if (mVenues != null && venue.getId().equals(mVenues.getId())) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin_selected));
                    mSelected = marker;
                }

                mCollection.put(marker.getId(), venue);
            }
        }

        getView().getActivity().toggleProgressAnim(false);
    }

    @Override
    public void onErrorResponse() {
        getView().getActivity().toggleProgressAnim(false);
    }

    private void initAnimDto() {

        MainActivity a = getView().getActivity();

        FrameLayout uiWindowInfo = a.getWindowInfo();
        AppBarLayout uiAppBarLayout = a.getAppBarLayout();

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
                ViewCompat.setElevation(uiAppBarLayout, a.getDefaultElevation());

                if (mSelected != null) {
                    mSelected.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.venue_pin));
                    mSelected = null;
                }

                getView().getActivity().clearWindowInfoForm();
            });
        }
    }
}
