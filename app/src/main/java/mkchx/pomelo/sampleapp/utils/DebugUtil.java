package mkchx.pomelo.sampleapp.utils;

import android.util.Log;

import mkchx.pomelo.sampleapp.AppStart;

public class DebugUtil {

    static boolean debug = true;

    public static void output(String tag, String str) {
        output("", tag, str);
    }

    public static void output(String type, String tag, String str) {

        if (debug) {
            switch (type) {
                case "e":
                    Log.e(tag, str);
                    break;
                case "i":
                    Log.i(tag, str);
                    break;
                case "w":
                    Log.w(tag, str);
                    break;
                default:
                    Log.d(tag, str);
                    break;
            }
        }
    }
}
