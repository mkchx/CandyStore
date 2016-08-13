package mkchx.pomelo.sampleapp.ui.global.mvp;

public interface GlobalHelper {

    interface View {

        boolean hasInternet();
    }

    interface Model {

    }

    interface Presenter<V> {

        boolean hasView();

        V getView();
    }
}
