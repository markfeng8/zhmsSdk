/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wondersgroup.android.sdk.ui.familydoctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.wondersgroup.android.sdk.R;
import com.wondersgroup.android.sdk.constants.IntentExtra;
import com.wondersgroup.android.sdk.constants.SpKey;
import com.wondersgroup.android.sdk.entity.JsInvokeBean;
import com.wondersgroup.android.sdk.ui.paymentdetails.view.PaymentDetailsActivity;
import com.wondersgroup.android.sdk.utils.LogUtil;
import com.wondersgroup.android.sdk.utils.SpUtil;
import com.wondersgroup.android.sdk.utils.StatusBarUtils;
import com.wondersgroup.android.sdk.utils.WToastUtil;
import com.wondersgroup.android.sdk.widget.TitleBarLayout;

/**
 * Created by x-sir on 2018/11/30 :)
 * Function:家庭医生
 */
public class FamilyDoctorActivity extends AppCompatActivity {

    private static final String TAG = "FamilyDoctorActivity";
    private WebView webView;
    private ProgressBar progressBar;
    private TitleBarLayout titleLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_doctor);
        StatusBarUtils.tint(this);
        findViews();
        initData();
        initListener();
    }

    private void initListener() {
        titleLayout.setOnBackListener(() -> {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.addJavascriptInterface(new JavaScriptInterface(), "nativeMethod");
//        webView.loadUrl("file:///android_asset/familyDoctor.html");

        Intent intent = getIntent();
        if (intent != null) {
            String idCardUrl = intent.getStringExtra(IntentExtra.ID_CARD);
            webView.loadUrl(idCardUrl);
        }
    }

    private void findViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        titleLayout = findViewById(R.id.titleLayout);
    }

    /**
     * 打开家庭医生页面
     *
     * @param context 上下文
     * @param idCard  身份证号码
     */
    public static void actionStart(Context context, @NonNull String idCard) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, FamilyDoctorActivity.class);
        intent.putExtra(IntentExtra.ID_CARD, idCard);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public class JavaScriptInterface {

        public JavaScriptInterface() {

        }

        /**
         * 与 js 交互时用到的方法，在 js 里直接调用的
         */
        @JavascriptInterface
        public void toPayment(String json) {
            if (!TextUtils.isEmpty(json)) {
                LogUtil.json(TAG, json);
                WToastUtil.showLong(json);

                JsInvokeBean jsInvokeBean = new Gson().fromJson(json, JsInvokeBean.class);
                String name = jsInvokeBean.getName();
                String cardType = jsInvokeBean.getCardType();
                String cardNum = jsInvokeBean.getCardNum();
                String orgCode = jsInvokeBean.getOrgCode();
                String orgName = jsInvokeBean.getOrgName();
                String idType = jsInvokeBean.getIdType();
                String idNum = jsInvokeBean.getIdNum();
                String hiCode = jsInvokeBean.getHiCode();

                SpUtil.getInstance().save(SpKey.NAME, name);
                SpUtil.getInstance().save(SpKey.CARD_TYPE, cardType);
                SpUtil.getInstance().save(SpKey.CARD_NUM, cardNum);
                SpUtil.getInstance().save(SpKey.ID_TYPE, idType);
                SpUtil.getInstance().save(SpKey.ID_NUM, idNum);
                SpUtil.getInstance().save(SpKey.HI_CODE,hiCode);

                PaymentDetailsActivity.actionStart(
                        FamilyDoctorActivity.this, orgCode, orgName, hiCode,false);
            }
        }

        @JavascriptInterface
        public void finishPage() {
            FamilyDoctorActivity.this.finish();
        }
    }
}
