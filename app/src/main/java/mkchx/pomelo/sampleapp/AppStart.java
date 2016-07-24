package mkchx.pomelo.sampleapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import mkchx.pomelo.sampleapp.injection.component.ApiComponent;
import mkchx.pomelo.sampleapp.injection.component.AppComponent;
import mkchx.pomelo.sampleapp.injection.component.DaggerApiComponent;
import mkchx.pomelo.sampleapp.injection.component.DaggerAppComponent;
import mkchx.pomelo.sampleapp.injection.module.ApiModule;
import mkchx.pomelo.sampleapp.injection.module.AppModule;
import mkchx.pomelo.sampleapp.injection.module.ConnectionModule;
import mkchx.pomelo.sampleapp.injection.module.NetModule;
import mkchx.pomelo.sampleapp.injection.module.PicassoModule;

public class AppStart extends Application {

    AppComponent mAppComponent;
    ApiComponent mApiComponent;

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

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(getString(R.string.api_url)))
                .picassoModule(new PicassoModule(this))
                .connectionModule(new ConnectionModule(this))
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

    public static AppStart getApp() {
        return mInstance;
    }

}