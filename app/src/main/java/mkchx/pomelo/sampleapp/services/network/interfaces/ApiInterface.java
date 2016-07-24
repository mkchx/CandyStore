package mkchx.pomelo.sampleapp.services.network.interfaces;

import java.util.Map;

import mkchx.pomelo.sampleapp.models.dto.FoursquareDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiInterface<T> {

    @GET("venues/search")
    Call<FoursquareDto> getSearch(@QueryMap Map<String, String> params);

    @GET("venues/{venue_id}/photos")
    Call<FoursquareDto> getPhotos(@Path("venue_id") String venueId, @QueryMap Map<String, String> params);
}