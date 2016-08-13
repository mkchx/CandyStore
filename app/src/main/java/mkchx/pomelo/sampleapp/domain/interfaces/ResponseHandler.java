package mkchx.pomelo.sampleapp.domain.interfaces;

public abstract class ResponseHandler {
    public abstract void onResponse(Object data);

    public abstract void onFailure(String message);
}
