package mkchx.pomelo.sampleapp.ui.global.mvp;

import java.lang.ref.WeakReference;

public class GlobalPresenter implements GlobalHelper.Presenter {

    WeakReference<GlobalHelper.View> mGlobalView;

    public void injectView(GlobalHelper.View globalView) {
        mGlobalView = new WeakReference<>(globalView);
    }

    /**
     * Check if the view has been injected or not
     *
     * @return true if it has been added
     */
    @Override
    public boolean hasView() {
        return (mGlobalView != null);
    }

    /**
     * @return the view's instance if not null
     * @throws NullPointerException
     */
    @Override
    public GlobalHelper.View getView() throws NullPointerException {
        try {
            return (hasView() ? mGlobalView.get() : null);
        } catch (Exception e) {
            throw new NullPointerException();
        }
    }
}
