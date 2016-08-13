package mkchx.pomelo.sampleapp.ui.global.mvp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public interface FragmentHelper {

    void toggleFragment(AppCompatActivity activity, FragmentManager fm, Fragment fragment, boolean show);

    void createFragment(AppCompatActivity a, FragmentManager fm, Fragment fragment, int resId);

}
