package com.app.tomeetme.helper.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class FontTypefaceUtils {

    private static HashMap<String, Typeface> typefaceHashMap = new HashMap<String, Typeface>();

    public FontTypefaceUtils(){

    }

    static Typeface getTypeface(Context context, String key) {
        if (!typefaceHashMap.containsKey(key))
            typefaceHashMap.put(key, Typeface.createFromAsset(context.getAssets(), key));
        return typefaceHashMap.get(key);
    }

    public static Typeface getRoboto(Context context) {
        return getTypeface(context, "fonts/Roboto-Regular.ttf");
    }

    public static Typeface getRobotoLight(Context context) {
        return getTypeface(context, "fonts/Roboto-Light.ttf");
    }

    public static Typeface getRobotoLightItalic(Context context) {
        return getTypeface(context, "fonts/Roboto-LightItalic.ttf");
    }

    public static Typeface getRobotoThin(Context context) {
        return getTypeface(context, "fonts/Roboto-Thin.ttf");
    }

    public static Typeface getRobotoMedium(Context context) {
        return getTypeface(context, "fonts/Roboto-Medium.ttf");
    }

    public static Typeface getRobotoMediumItalic(Context context) {
        return getTypeface(context, "fonts/Roboto-MediumItalic.ttf");
    }

    public static Typeface getRobotoBold(Context context) {
        return getTypeface(context, "fonts/Roboto-Bold.ttf");
    }

    public static Typeface getRobotoBoldItalic(Context context) {
        return getTypeface(context, "fonts/Roboto-BoldItalic.ttf");
    }

    public static Typeface getRobotoCondensedRegular(Context context) {
        return getTypeface(context, "fonts/RobotoCondensed-Regular.ttf");
    }

    public static Typeface getRobotoCondensedLight(Context context) {
        return getTypeface(context, "fonts/RobotoCondensed-Light.ttf");
    }

    public static Typeface getRobotoCondensedLightItalic(Context context) {
        return getTypeface(context, "fonts/RobotoCondensed-LightItalic.ttf");
    }

    public static Typeface getRobotoCondensedBold(Context context) {
        return getTypeface(context, "fonts/RobotoCondensed-Bold.ttf");
    }

    public static Typeface getKnockout(Context context) {
        return getTypeface(context, "fonts/Knockout-29.otf");
    }

}
