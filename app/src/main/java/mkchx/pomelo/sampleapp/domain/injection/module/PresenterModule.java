package mkchx.pomelo.sampleapp.domain.injection.module;

import dagger.Module;
import dagger.Provides;
import mkchx.pomelo.sampleapp.ui.main.mvp.MainPresenter;

@Module
public class PresenterModule {

    @Provides
    MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }
}