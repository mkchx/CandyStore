package mkchx.pomelo.sampleapp.domain.injection.component;

import dagger.Component;
import mkchx.pomelo.sampleapp.domain.injection.module.ApiModule;
import mkchx.pomelo.sampleapp.domain.injection.scopes.MainScope;
import mkchx.pomelo.sampleapp.ui.main.MainActivity;

@MainScope
@Component(dependencies = AppComponent.class, modules = ApiModule.class)
public interface ApiComponent {

    void inject(MainActivity activity);

}
