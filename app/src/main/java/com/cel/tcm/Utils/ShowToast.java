package com.cel.tcm.Utils;

import android.content.Context;

import com.shashank.sony.fancytoastlib.FancyToast;

public class ShowToast {

    Context context;
    String message;

    public static void onSuccess(Context context, String message) {
        FancyToast.makeText(context, message, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
    }

    public static void onError(Context context, String message) {
        FancyToast.makeText(context, message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
    }
}
