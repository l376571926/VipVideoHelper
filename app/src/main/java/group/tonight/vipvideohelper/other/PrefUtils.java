package group.tonight.vipvideohelper.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {
    public static final String KEY_LAST_SELECT_API_INDEX = "last_parse_index";
    private static SharedPreferences INSTANCE;

    public static SharedPreferences init(Context context) {
        if (INSTANCE == null) {
            synchronized (PrefUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static SharedPreferences get() {
        if (INSTANCE == null) {
            throw new NullPointerException("必须在Application的onCreate()中调用PrefUtils.init(this)");
        }
        return INSTANCE;
    }
}
