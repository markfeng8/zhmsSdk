package com.wondersgroup.android.sdk.ui.openafterpay.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wondersgroup.android.sdk.R;
import com.wondersgroup.android.sdk.base.MvpBaseActivity;
import com.wondersgroup.android.sdk.constants.SpKey;
import com.wondersgroup.android.sdk.ui.openafterpay.contract.OpenAfterPayContract;
import com.wondersgroup.android.sdk.ui.openafterpay.presenter.OpenAfterPayPresenter;
import com.wondersgroup.android.sdk.utils.SpUtil;
import com.wondersgroup.android.sdk.utils.WToastUtil;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by x-sir on 2018/8/1 :)
 * Function:开通医后付页面
 */
public class OpenAfterPayActivity extends MvpBaseActivity<OpenAfterPayContract.IView,
        OpenAfterPayPresenter<OpenAfterPayContract.IView>> implements OpenAfterPayContract.IView {

    private EditText etPhone;
    private EditText etSmsCode;
    private Button btnOpen;
    private Button btnBackToHome;
    private TextView btnGetSmsCode;
    private ToggleButton toggleButton;
    private LinearLayout llOpenPager;
    private LinearLayout llOpenSuccess;
    private CountdownView countDownView;
    private boolean isAgreeRule = false;
    private static final String TAG = "OpenAfterPayActivity";

    @Override
    protected OpenAfterPayPresenter<OpenAfterPayContract.IView> createPresenter() {
        return new OpenAfterPayPresenter<>();
    }

    @Override
    protected void bindView() {
        setContentView(R.layout.wonders_group_activity_open_afterpay);
        initViews();
        initData();
        initListener();
    }

    private void initData() {
        String phone = SpUtil.getInstance().getString(SpKey.PASS_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            etPhone.setText(phone);
            etPhone.setSelection(phone.length());
        }
    }

    private void initViews() {
        etPhone = findViewById(R.id.etPhone);
        etSmsCode = findViewById(R.id.etSmsCode);
        btnGetSmsCode = findViewById(R.id.btnGetSmsCode);
        toggleButton = findViewById(R.id.toggleButton);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnOpen = findViewById(R.id.btnOpen);
        llOpenPager = findViewById(R.id.llOpenPager);
        llOpenSuccess = findViewById(R.id.llOpenSuccess);
        countDownView = findViewById(R.id.countDownView);
    }

    private void initListener() {
        btnGetSmsCode.setOnClickListener(v -> {
            mPresenter.sendSmsCode();
        });
        btnOpen.setOnClickListener(v -> {
            if (isAgreeRule) {
                sendOpenRequest();
            } else {
                WToastUtil.show("请先勾选同意！");
            }
        });
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> isAgreeRule = isChecked);
        btnBackToHome.setOnClickListener(v -> finish());
        countDownView.setOnCountdownEndListener(cv -> {
            countDownView.setVisibility(View.GONE);
            btnGetSmsCode.setVisibility(View.VISIBLE);
        });
    }

    private void sendOpenRequest() {
        String phone = etPhone.getText().toString().trim();
        String smsCode = etSmsCode.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(smsCode)) {
            mPresenter.openAfterPay(phone, smsCode);
        } else {
            WToastUtil.show("手机号或验证为不能为空！");
        }
    }

    @Override
    public String getPhoneNumber() {
        return etPhone.getText().toString();
    }

    @Override
    public void onAfterPayOpenSuccess() {
        llOpenPager.setVisibility(View.GONE);
        llOpenSuccess.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAfterPayOpenFailed() {
        WToastUtil.show("开通失败！");
    }

    @Override
    public void showCountDownView() {
        btnGetSmsCode.setVisibility(View.GONE);
        countDownView.setVisibility(View.VISIBLE);
        countDownView.start(60000);
    }

    public static void actionStart(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, OpenAfterPayActivity.class);
        context.startActivity(intent);
    }
}
