package mkchx.pomelo.sampleapp.ui.global.mvp;

import android.os.Bundle;

public interface LifeCycleHelper {

    void onDestroy();

    void onStart();

    void onPause();

    void onResume();

    void onSaveInstanceState(Bundle outState);

    void onRestoreSaved(Bundle outState);
}
