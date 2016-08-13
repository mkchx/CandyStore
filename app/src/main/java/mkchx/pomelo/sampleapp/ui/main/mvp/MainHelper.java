package mkchx.pomelo.sampleapp.ui.main.mvp;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import mkchx.pomelo.sampleapp.domain.models.api.Photos;
import mkchx.pomelo.sampleapp.domain.models.api.Venues;
import mkchx.pomelo.sampleapp.ui.global.mvp.FragmentHelper;
import mkchx.pomelo.sampleapp.ui.global.mvp.GlobalHelper;
import mkchx.pomelo.sampleapp.ui.global.mvp.LifeCycleHelper;
import mkchx.pomelo.sampleapp.ui.main.MainActivity;

public interface MainHelper {

    interface View extends GlobalHelper.View {

        MainActivity getActivity();

    }

    interface Model extends GlobalHelper.Model {

        void onDestroy();

        void fetchPhotos(String venueId);

        void fetchVenues(LatLng latLng);
    }

    interface Presenter extends GlobalHelper.Presenter, FragmentHelper, LifeCycleHelper, ListenerHelper {

        void registerBus();

        void unregisterBus();

        void postBus(Object o);

        void toggleWindowInfo(boolean toggle);

        void setVenueBasedOnSelectedMarker();

        void clearData();

        void loadWindowInfoData();

        void createService();

    }
}
