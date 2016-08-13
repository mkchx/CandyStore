package mkchx.pomelo.sampleapp.domain.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ConvertUtil {

    public static float pxToDp(Context context, int px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);

        return dp;
    }

    public static float dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);

        return px;
    }
}
