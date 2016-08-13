package mkchx.pomelo.sampleapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import mkchx.pomelo.sampleapp.domain.injection.component.ApiComponent;
import mkchx.pomelo.sampleapp.domain.injection.component.AppComponent;
import mkchx.pomelo.sampleapp.domain.injection.component.DaggerApiComponent;
import mkchx.pomelo.sampleapp.domain.injection.component.DaggerAppComponent;
import mkchx.pomelo.sampleapp.domain.injection.module.ApiModule;
import mkchx.pomelo.sampleapp.domain.injection.module.AppModule;
import mkchx.pomelo.sampleapp.domain.injection.module.NetModule;
import mkchx.pomelo.sampleapp.domain.injection.module.PicassoModule;
import mkchx.pomelo.sampleapp.domain.injection.module.PresenterModule;
import mkchx.pomelo.sampleapp.domain.utils.DeviceConfigUtil;

public class AppStart extends Application {

    AppComponent mAppComponent;
    ApiComponent mApiComponent;

    DeviceConfigUtil mDeviceConfig;

    static AppStart mInstance;

    //RefWatcher refWatcher;

    /*public RefWatcher getRefWatcher(Context context) {
        AppStart application = (AppStart) context.getApplicationContext();
        return application.refWatcher;
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        //refWatcher = LeakCanary.install(this);
        mDeviceConfig = new DeviceConfigUtil(this);

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(getString(R.string.api_url)))
                .picassoModule(new PicassoModule(this))
                .presenterModule(new PresenterModule())
                .build();

        mApiComponent = DaggerApiComponent.builder()
                .appComponent(mAppComponent)
                .apiModule(new ApiModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public ApiComponent getApiComponent() {
        return mApiComponent;
    }

    public DeviceConfigUtil getDeviceConfig() {
        return mDeviceConfig;
    }

    public static AppStart getApp() {
        return mInstance;
    }

}