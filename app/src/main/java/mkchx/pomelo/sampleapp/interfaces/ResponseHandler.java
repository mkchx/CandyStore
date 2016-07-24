package mkchx.pomelo.sampleapp.interfaces;

public abstract class ResponseHandler {
    public abstract void onResponse(Object data);

    public abstract void onFailure(String message);
}
