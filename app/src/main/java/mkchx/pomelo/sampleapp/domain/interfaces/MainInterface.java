package mkchx.pomelo.sampleapp.domain.interfaces;

import mkchx.pomelo.sampleapp.ui.main.mvp.MainPresenter;

public interface MainInterface {

    boolean isConnected();

    boolean gpsGoodness();

    MainPresenter getPresenter();
}
