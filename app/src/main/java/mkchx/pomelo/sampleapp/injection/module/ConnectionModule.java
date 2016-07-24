package mkchx.pomelo.sampleapp.injection.module;

import android.content.Context;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.NetworkEvents;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

@Module
public class ConnectionModule {

    Context mContext;
    BusWrapper mBusWrapper;

    public ConnectionModule(Context context) {
        mContext = context;
        mBusWrapper = new BusWrapper() {

            @Override
            public void register(Object object) {
                EventBus.getDefault().register(object);
            }

            @Override
            public void unregister(Object object) {
                EventBus.getDefault().unregister(object);
            }

            @Override
            public void post(Object event) {
                EventBus.getDefault().post(event);
            }
        };
    }

    @Provides
    public NetworkEvents provideNetworkEvents() {
        return new NetworkEvents(mContext, mBusWrapper).enableInternetCheck();
    }

    @Provides
    public BusWrapper provideBusWrapper() {
        return mBusWrapper;
    }
}
