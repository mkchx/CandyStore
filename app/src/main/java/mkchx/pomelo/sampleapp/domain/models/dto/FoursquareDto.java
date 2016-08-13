package mkchx.pomelo.sampleapp.domain.models.dto;

import mkchx.pomelo.sampleapp.domain.models.api.Meta;
import mkchx.pomelo.sampleapp.domain.models.api.Response;

public class FoursquareDto {

    Meta meta;
    Response response;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}