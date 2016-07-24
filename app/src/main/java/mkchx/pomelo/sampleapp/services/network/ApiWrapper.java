package mkchx.pomelo.sampleapp.services.network;

import java.io.IOException;
import java.util.Map;

import mkchx.pomelo.sampleapp.interfaces.ResponseHandler;
import mkchx.pomelo.sampleapp.models.dto.FoursquareDto;
import mkchx.pomelo.sampleapp.services.network.interfaces.ApiInterface;
import mkchx.pomelo.sampleapp.utils.DebugUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiWrapper {

    Retrofit mRetrofit;
    ApiInterface mApiInterface;

    public void destroy() {
        mRetrofit = null;
        mApiInterface = null;
    }

    public ApiWrapper(Retrofit retrofit) {
        mRetrofit = retrofit;
        mApiInterface = retrofit.create(ApiInterface.class);
    }

    public void getVenuePhotos(String venueId, Map<String, String> params, final ResponseHandler responseHandler) {

        Call<FoursquareDto> call = mApiInterface.getPhotos(venueId, params);

        call.enqueue(new Callback<FoursquareDto>() {
            @Override
            public void onResponse(Call<FoursquareDto> call, Response<FoursquareDto> response) {

                DebugUtil.output("Get request", "Url :" + response.raw().request().url());

                if (response.isSuccessful()) {
                    responseHandler.onResponse(response.body());
                } else {
                    responseHandler.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<FoursquareDto> call, Throwable t) {
                responseHandler.onFailure(t.getMessage());
            }
        });
    }

    public void getSearchItems(Map<String, String> params, final ResponseHandler responseHandler) {

        Call<FoursquareDto> call = mApiInterface.getSearch(params);

        call.enqueue(new Callback<FoursquareDto>() {
            @Override
            public void onResponse(Call<FoursquareDto> call, Response<FoursquareDto> response) {

                DebugUtil.output("Get request", "Url :" + response.raw().request().url());

                if (response.isSuccessful()) {
                    responseHandler.onResponse(response.body());
                } else {
                    responseHandler.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<FoursquareDto> call, Throwable t) {
                responseHandler.onFailure(t.getMessage());
            }
        });
    }

    public Response getTestSearch(Map<String, String> params) throws IOException {

        Call call = mApiInterface.getSearch(params);

        return call.execute();
    }

    public Response getTestPhoto(String venueId, Map<String, String> params) throws IOException {

        Call call = mApiInterface.getPhotos(venueId, params);

        return call.execute();
    }
}
