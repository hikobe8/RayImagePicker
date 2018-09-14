package com.ray.image_picker.util;

import android.content.Context;
import android.util.DisplayMetrics;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-14 10:09
 *  description : 
 */
public class DeviceUtil {

    public static int getDeviceWidth(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

}
