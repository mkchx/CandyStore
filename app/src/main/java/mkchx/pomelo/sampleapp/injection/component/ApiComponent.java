package mkchx.pomelo.sampleapp.injection.component;

import dagger.Component;
import mkchx.pomelo.sampleapp.injection.module.ApiModule;
import mkchx.pomelo.sampleapp.injection.scopes.MainScope;
import mkchx.pomelo.sampleapp.views.activity.BaseFragmentActivity;

@MainScope
@Component(dependencies = AppComponent.class, modules = ApiModule.class)
public interface ApiComponent {

    void inject(BaseFragmentActivity activity);

}
