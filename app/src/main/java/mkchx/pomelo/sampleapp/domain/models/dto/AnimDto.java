package mkchx.pomelo.sampleapp.domain.models.dto;

import android.view.View;

public class AnimDto {

    private int cX;
    private int cY;
    private int radius;
    private Runnable runnable;
    private View view;

    public AnimDto(View v, int x, int y, int r, Runnable runnable) {
        this.cX = x;
        this.cY = y;
        this.view = v;
        this.radius = r;
        this.runnable = runnable;
    }

    public int getcX() {
        return cX;
    }

    public int getcY() {
        return cY;
    }

    public int getRadius() {
        return radius;
    }

    public void executeRunnable() {

        if (runnable != null) {
            runnable.run();
        }
    }

    public View getView() {
        return view;
    }
}
