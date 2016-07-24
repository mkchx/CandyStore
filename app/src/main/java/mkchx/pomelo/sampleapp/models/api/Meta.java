package mkchx.pomelo.sampleapp.models.api;

public class Meta {

    private int code;
    private ErrorType errorType;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}