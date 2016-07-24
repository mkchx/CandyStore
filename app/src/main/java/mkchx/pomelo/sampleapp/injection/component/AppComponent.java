package mkchx.pomelo.sampleapp.injection.component;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import mkchx.pomelo.sampleapp.injection.module.AppModule;
import mkchx.pomelo.sampleapp.injection.module.ConnectionModule;
import mkchx.pomelo.sampleapp.injection.module.NetModule;
import mkchx.pomelo.sampleapp.injection.module.PicassoModule;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {AppModule.class, NetModule.class, PicassoModule.class, ConnectionModule.class})
public interface AppComponent {

    Retrofit provideRetrofit();

    Picasso providePicasso();

    NetworkEvents provideNetworkEvents();

    BusWrapper provideBusWrapper();

}
