package com.wondersgroup.android.sdk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.wondersgroup.android.sdk.constants.Exceptions;

/**
 * Created by x-sir on 2018/8/2 :)
 * Function:Wonders sdk global application.
 */
public class WondersApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    static Context sContext;

    public WondersApplication() {
        sContext = this;
    }

    public static Context getsContext() {
        checkNotNull(sContext, Exceptions.APPLICATION_CONTEXT_NULL);
        return sContext;
    }

    private static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
}
