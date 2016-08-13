package mkchx.pomelo.sampleapp.ui.main.mvp;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import mkchx.pomelo.sampleapp.R;
import mkchx.pomelo.sampleapp.domain.interfaces.ResponseHandler;
import mkchx.pomelo.sampleapp.domain.models.dto.FoursquareDto;
import mkchx.pomelo.sampleapp.ui.main.MainActivity;

public class MainModel implements MainHelper.Model {

    MainHelper.Presenter mPresenter;

    public MainModel(MainHelper.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        mPresenter = null;
    }

    @Override
    public void fetchPhotos(String venueId) {

        MainActivity a = ((MainHelper.View) mPresenter.getView()).getActivity();

        Map<String, String> params = new HashMap<>();
        params.put("limit", "1");
        params.put("client_id", a.getString(R.string.fsq_client_id));
        params.put("client_secret", a.getString(R.string.fsq_client_secret));
        params.put("v", "20160723");

        a.getApiWrapper().getVenuePhotos(venueId, params, new ResponseHandler() {

            @Override
            public void onResponse(Object data) {

                FoursquareDto foursquareDto = (FoursquareDto) data;

                // we limit the request to 1, if there's a photo, we will get it
                mPresenter.onPhotoResponse(foursquareDto.getResponse().getPhotos());
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(a, R.string.api_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void fetchVenues(LatLng latLng) {

        MainActivity a = ((MainHelper.View) mPresenter.getView()).getActivity();

        Map<String, String> params = new HashMap<>();
        params.put("ll", latLng.latitude + "," + latLng.longitude);
        params.put("categoryId", "4bf58dd8d48988d117951735");
        params.put("venuePhotos", "1");
        params.put("client_id", a.getString(R.string.fsq_client_id));
        params.put("client_secret", a.getString(R.string.fsq_client_secret));
        params.put("v", "20160722");

        mPresenter.setVenueBasedOnSelectedMarker();
        mPresenter.clearData();

        a.getApiWrapper().getSearchItems(params, new ResponseHandler() {

            @Override
            public void onResponse(Object data) {

                FoursquareDto foursquareDto = (FoursquareDto) data;

                mPresenter.onVenuesResponse(foursquareDto.getResponse().getVenues());
            }

            @Override
            public void onFailure(String message) {

                Toast.makeText(a, R.string.api_failure, Toast.LENGTH_SHORT).show();
                mPresenter.onErrorResponse();
            }
        });
    }

}
