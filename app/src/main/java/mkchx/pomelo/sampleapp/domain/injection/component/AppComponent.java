package mkchx.pomelo.sampleapp.domain.injection.component;

import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import mkchx.pomelo.sampleapp.domain.injection.module.AppModule;
import mkchx.pomelo.sampleapp.domain.injection.module.NetModule;
import mkchx.pomelo.sampleapp.domain.injection.module.PicassoModule;
import mkchx.pomelo.sampleapp.domain.injection.module.PresenterModule;
import mkchx.pomelo.sampleapp.ui.main.mvp.MainPresenter;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {AppModule.class, NetModule.class, PicassoModule.class, PresenterModule.class})
public interface AppComponent {

    Retrofit provideRetrofit();

    Picasso providePicasso();

    MainPresenter provideMainPresenter();

}
