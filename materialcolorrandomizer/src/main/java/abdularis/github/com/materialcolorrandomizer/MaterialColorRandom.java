package abdularis.github.com.materialcolorrandomizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;

import java.util.Collection;
import java.util.Random;

import androidx.annotation.NonNull;

/**
 * Created by abdularis on 13/12/17.
 *
 * Material color randomizer (There are many colors removed, because those doesn't fit my project)
 */

public class MaterialColorRandom {
    private static final int MAX_ITERATION = 16;
    private static final String SHARED_PREFS_NAME = "AndroidIdColorGenerator";
    private static Random sRand = new Random(System.currentTimeMillis());

    private final String mPackageName;
    private Resources mResources;

    private SharedPreferences mPrefs;

    private static String INSTANCE_NAME = "";
    private static MaterialColorRandom INSTANCE = null;
    public static MaterialColorRandom getInstance(@NonNull Context context, String name) {
        if (INSTANCE != null && INSTANCE_NAME.equals(name))
            return INSTANCE;
        INSTANCE = new MaterialColorRandom(context, name);
        INSTANCE_NAME = name;
        return INSTANCE;
    }

    public static MaterialColorRandom getInstance(@NonNull Context context) {
        return getInstance(context, SHARED_PREFS_NAME + "Default");
    }

    private MaterialColorRandom(@NonNull Context context, String name) {
        mPackageName = context.getPackageName();
        mResources = context.getResources();
        mPrefs = context.getSharedPreferences(SHARED_PREFS_NAME + name, Context.MODE_PRIVATE);
    }

    public void clearColors() {
        mPrefs.edit().clear().apply();
    }

    public int getRandomMaterialColor(String id) {
        int color = mPrefs.getInt(id, -1);
        if (color != -1) return color;

        Collection<?> savedColors = mPrefs.getAll().values();
        int arrayId = mResources.getIdentifier("material_color", "array", mPackageName);

        if (arrayId != 0) {
            TypedArray colors = mResources.obtainTypedArray(arrayId);

            for (int i = 0; i < MAX_ITERATION; i++) {
                int index = getRandomInt(0, colors.length());
                color = colors.getColor(index, 0);
                if (!savedColors.contains(color)) {
                    mPrefs.edit().putInt(id, color).apply();
                    break;
                }
            }

            colors.recycle();
        }

        return color;
    }

    private static int getRandomInt(int min, int max) {
        return Math.max(min, sRand.nextInt(max));
    }

    private static float[] hsv = new float[3];
    public static int getLightenedColor(int baseColor, float lightIntensity) {
        lightIntensity = Math.min(Math.max(0, lightIntensity), 1.0f);
        Color.colorToHSV(baseColor, hsv);
        hsv[1] -= lightIntensity * hsv[1];
        hsv[2] += lightIntensity * (1.0f - hsv[2]);
        return Color.HSVToColor(hsv);
    }
}
