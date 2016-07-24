package mkchx.pomelo.sampleapp.views.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.widget.Toolbar;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import mkchx.pomelo.sampleapp.AppStart;
import mkchx.pomelo.sampleapp.R;
import mkchx.pomelo.sampleapp.services.DeviceConfig;
import mkchx.pomelo.sampleapp.services.network.ApiWrapper;
import mkchx.pomelo.sampleapp.views.fragments.DetailFragment;
import retrofit2.Retrofit;

public class BaseFragmentActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, AppCompatCallback {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @Inject
    Retrofit mRetrofit;

    @Inject
    Picasso mPicasso;

    @Inject
    NetworkEvents mNetworkEvents;

    @Inject
    BusWrapper mBusWrapper;

    ApiWrapper mApiWrapper;
    DeviceConfig mDeviceConfig;

    ActionBar mActionBar;
    FragmentManager mFragmentManager;

    public ApiWrapper getApiWrapper() {
        return mApiWrapper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mToolBar = null;
        mActionBar = null;

        mRetrofit = null;
        mPicasso = null;

        if (mApiWrapper != null) {
            mApiWrapper.destroy();
            mApiWrapper = null;
        }

        mFragmentManager = null;
    }

    public void setFragmentManager(FragmentManager m) {

        if (mFragmentManager == null) {
            mFragmentManager = m;
            mFragmentManager.addOnBackStackChangedListener(this);
        }
    }

    public void createFragment(final Fragment fragment, final int resId) {

        if (!isFinishing() && fragment != null && resId != 0) {

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);

            Fragment existedFragment = mFragmentManager.findFragmentById(resId);

            if (existedFragment instanceof DetailFragment) {
                fragmentTransaction.replace(resId, fragment, ((Object) fragment).getClass().getName());
            } else {
                fragmentTransaction.add(resId, fragment, ((Object) fragment).getClass().getName());
                fragmentTransaction.addToBackStack(null);
            }

            fragmentTransaction.commit();
        }
    }

    public void toggleFragment(final Fragment fragment, boolean show) {

        if (!isFinishing() && fragment != null) {

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);

            if (show) {
                fragmentTransaction.show(fragment);

            } else {
                fragmentTransaction.hide(fragment);
            }

            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        AppStart.getApp().getApiComponent().inject(this);

        mApiWrapper = new ApiWrapper(mRetrofit);
        mDeviceConfig = new DeviceConfig(this);

        if (!mDeviceConfig.isDeviceTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }

    @Override
    public void onBackStackChanged() {

    }
}