package mkchx.pomelo.sampleapp.domain.models.api;

import java.util.List;

public class Response {

    List<Venues> venues;
    Photos photos;

    public List<Venues> getVenues() {
        return venues;
    }

    public Photos getPhotos() {
        return photos;
    }
}