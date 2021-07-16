package com.wondersgroup.android.healthcity_sdk;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.tencent.bugly.crashreport.CrashReport;
import com.wondersgroup.android.sdk.WondersSdk;
import com.wondersgroup.android.sdk.entity.ConfigOption;

import cn.com.epsoft.zjessc.ZjEsscSDK;

/**
 * Created by x-sir on 2018/8/1 :)
 * Function:
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        ConfigOption option = new ConfigOption()
                .setDebug(true)
                //.setDebug(BuildConfig.DEBUG) // 设置是否为调试模式(调试模式可以打印日志，上线后建议设为 false)
                .setEnv("test"); // 设置环境(test 或 TEST 为测试环境，不设置或者其他默认为正式环境)
        // Application context & option parameters.
        initEpSoft(this, true);
        WondersSdk.getInstance().init(this, option);


        // 测试阶段建议设置成 true，发布时设置为 false
        CrashReport.initCrashReport(getApplicationContext(), "0010f85d4e", BuildConfig.DEBUG);

    }

    /**
     * 集成省电子社保卡
     */
    private void initEpSoft(Context context, boolean isDebug) {
        System.loadLibrary("nllvm1624117532");
        // isDebug 为 true 时是测试环境，false 时为正式环境
        ZjEsscSDK.init(isDebug, (Application) context, "3309000201");

        // 设置主题颜色
        ZjEsscSDK.setTitleColor("#1E90FF");
        // 设置主题字体颜色
        ZjEsscSDK.setTextColor("#FFFFFF");
        // 设置是否打印日志
        ZjEsscSDK.setLogDebug(isDebug);
    }
}
