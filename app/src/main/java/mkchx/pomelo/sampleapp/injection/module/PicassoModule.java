package mkchx.pomelo.sampleapp.injection.module;

import android.content.Context;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PicassoModule {

    Context mContext;

    public PicassoModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @Singleton
    Picasso providePicasso() {
        Picasso picasso = new Picasso.Builder(mContext)
                .memoryCache(new LruCache(mContext))
                .build();

        picasso.setIndicatorsEnabled(false);
        Picasso.setSingletonInstance(picasso);

        return picasso;
    }
}