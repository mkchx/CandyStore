package mkchx.pomelo.sampleapp.domain.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import mkchx.pomelo.sampleapp.R;
import mkchx.pomelo.sampleapp.domain.utils.ConvertUtil;

public class DeviceConfigUtil {

    int mDeviceWidth, mDeviceHeight, mPartialContentWidth;
    boolean m7Inch, m10Inch;

    Context mContext;

    public DeviceConfigUtil(Context context) {

        mContext = context;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);

        mDeviceWidth = size.x;
        mDeviceHeight = size.y;

        if (isTabletAndHorizontal() && !isTenInch()) { // 7 inch
            mPartialContentWidth = (int) Math.abs(ConvertUtil.dpToPx(context, 450) - size.x);
        } else if (isTabletAndHorizontal()) { // 10 inch
            mPartialContentWidth = (int) Math.abs(ConvertUtil.dpToPx(context, 550) - size.x);
        } else {
            mPartialContentWidth = size.x;
        }

        m7Inch = context.getResources().getBoolean(R.bool.has_two_panes); // 7'inch
        m10Inch = context.getResources().getBoolean(R.bool.has_three_panes); // 10'inch
    }

    // DeviceConfigUtil Config's

    public int getWidthContent() {

        if (isTabletAndHorizontal()) {
            return mPartialContentWidth;
        } else {
            return mDeviceWidth;
        }
    }

    public int getDeviceWidth() {
        return mDeviceWidth;
    }

    public int getDeviceHeight() {
        return mDeviceHeight;
    }

    public int getPartialContentWidth() {
        return mPartialContentWidth;
    }

    public boolean isTabletAndHorizontal() {
        return (mContext.getResources().getConfiguration().orientation == 2 && isDeviceTablet());
    }

    public boolean isTabletAndVertical() {
        return (mContext.getResources().getConfiguration().orientation == 1 && isDeviceTablet());
    }

    public int getDeviceOrientation() {
        return mContext.getResources().getConfiguration().orientation;
    }

    public boolean isSevenInch() {
        return m7Inch;
    }

    public boolean isTenInch() {
        return m10Inch;
    }

    public boolean isDeviceTablet() {
        return (m7Inch || m10Inch);
    }

    // END

}
