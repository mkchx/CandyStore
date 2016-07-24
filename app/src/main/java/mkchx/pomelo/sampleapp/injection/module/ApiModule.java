package mkchx.pomelo.sampleapp.injection.module;

import dagger.Module;
import dagger.Provides;
import mkchx.pomelo.sampleapp.injection.scopes.MainScope;
import mkchx.pomelo.sampleapp.services.network.interfaces.ApiInterface;
import retrofit2.Retrofit;

@Module
public class ApiModule {

    @Provides
    @MainScope
    public ApiInterface provideApiInterface(Retrofit retrofit) {
        return retrofit.create(ApiInterface.class);
    }

}

