/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wondersgroup.android.sdk.ui.leavehosresult;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wondersgroup.android.sdk.R;
import com.wondersgroup.android.sdk.constants.IntentExtra;
import com.wondersgroup.android.sdk.constants.SpKey;
import com.wondersgroup.android.sdk.ui.inhospitalhistory.view.InHospitalHistory;
import com.wondersgroup.android.sdk.utils.QRCodeUtil;
import com.wondersgroup.android.sdk.utils.SpUtil;
import com.wondersgroup.android.sdk.utils.StatusBarUtils;

/**
 * Created by x-sir on 2018/12/6 :)
 * Function:出院结算结果页面
 */
public class LeaveHosResultActivity extends AppCompatActivity {

    private static final String TAG = "LeaveHosResultActivity";
    private TextView tvResult;
    private TextView tvTreatName;
    private TextView tvIdNum;
    private TextView tvHospitalName;
    private TextView tvBillDate;
    private TextView tvBillNo;
    private TextView tvInHosTotalFee;
    private TextView tvYiBaoFee;
    private TextView tvCashFee;
    private TextView tvPrepayFee;
    private TextView tvWillFee;
    private TextView tvFailedToast;
    private TextView tvInHosHis;
    private ImageView ivQrCode;

    private String mOrgCode = "";
    private String mOrgName = "";
    private String mFeeTotal = "";
    private String mFeeCashTotal = "";
    private String mFeeYbTotal = "";
    private String mFeePrepayTotal;
    private String mFeeNeedCashTotal;
    /**
     * 是否支付成功
     */
    private boolean mIsSuccess = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_hos_result);
        StatusBarUtils.tint(this);
        findViews();
        initData();
        initListener();
    }

    private void initData() {
        String name = SpUtil.getInstance().getString(SpKey.NAME, "");
        String idNum = SpUtil.getInstance().getString(SpKey.ID_NUM, "");
        String payPlatTradeNo = SpUtil.getInstance().getString(SpKey.PAY_PLAT_TRADE_NO, "");
        String payStartTime = SpUtil.getInstance().getString(SpKey.PAY_START_TIME, "");

        Intent intent = getIntent();
        if (intent != null) {
            mIsSuccess = intent.getBooleanExtra(IntentExtra.IS_SUCCESS, false);
            mOrgCode = intent.getStringExtra(IntentExtra.ORG_CODE);
            mOrgName = intent.getStringExtra(IntentExtra.ORG_NAME);
            mFeeTotal = intent.getStringExtra(IntentExtra.FEE_TOTAL);
            mFeeCashTotal = intent.getStringExtra(IntentExtra.FEE_CASH_TOTAL);
            mFeeYbTotal = intent.getStringExtra(IntentExtra.FEE_YB_TOTAL);
            mFeePrepayTotal = intent.getStringExtra(IntentExtra.FEE_PREPAY_TOTAL);
            mFeeNeedCashTotal = intent.getStringExtra(IntentExtra.FEE_NEED_CASH_TOTAL);
        }

        if (!mIsSuccess) {
            tvResult.setText(getString(R.string.wonders_self_failed1));
            tvInHosHis.setText(getString(R.string.wonders_back_to_home_page));
            tvFailedToast.setVisibility(View.VISIBLE);
        }

        tvTreatName.setText(name);
        tvIdNum.setText(idNum);
        tvHospitalName.setText(mOrgName);
        tvBillDate.setText(payStartTime);
        tvBillNo.setText(payPlatTradeNo);
        tvInHosTotalFee.setText(mFeeTotal);
        tvYiBaoFee.setText(mFeeYbTotal);
        tvCashFee.setText(mFeeCashTotal);
        tvPrepayFee.setText(mFeePrepayTotal);
        tvWillFee.setText(mFeeNeedCashTotal);

        if (!TextUtils.isEmpty(payPlatTradeNo)) {
            createQrCode(payPlatTradeNo);
        }
    }

    private void createQrCode(String payPlatTradeNo) {
        Bitmap bitmap = QRCodeUtil.createQrCodeBitmap(payPlatTradeNo, 200, 200, null);
        if (bitmap != null) {
            ivQrCode.setImageBitmap(bitmap);
        }
    }

    private void initListener() {
        tvInHosHis.setOnClickListener(v -> {
            // 如果结算成功就跳转到住院历史页面，失败就销毁页面返回到首页
            if (mIsSuccess) {
                InHospitalHistory.actionStart(LeaveHosResultActivity.this, mOrgCode, mOrgName);
            }
            finish();
        });
    }

    private void findViews() {
        tvResult = findViewById(R.id.tvResult);
        tvTreatName = findViewById(R.id.tvTreatName);
        tvIdNum = findViewById(R.id.tvIdNum);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvBillDate = findViewById(R.id.tvBillDate);
        tvBillNo = findViewById(R.id.tvBillNo);
        tvInHosTotalFee = findViewById(R.id.tvInHosTotalFee);
        tvYiBaoFee = findViewById(R.id.tvYiBaoFee);
        tvCashFee = findViewById(R.id.tvCashFee);
        tvPrepayFee = findViewById(R.id.tvPrepayFee);
        tvWillFee = findViewById(R.id.tvWillFee);
        tvFailedToast = findViewById(R.id.tvFailedToast);
        tvInHosHis = findViewById(R.id.tvInHosHis);
        ivQrCode = findViewById(R.id.ivQrCode);
    }

    public static void actionStart(Context context, boolean isSuccess, String orgCode, String orgName, String feeTotal,
                                   String feeCashTotal, String feeYbTotal, String feePrepayTotal, String feeNeedCashTotal) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, LeaveHosResultActivity.class);
        intent.putExtra(IntentExtra.IS_SUCCESS, isSuccess);
        intent.putExtra(IntentExtra.ORG_CODE, orgCode);
        intent.putExtra(IntentExtra.ORG_NAME, orgName);
        intent.putExtra(IntentExtra.FEE_TOTAL, feeTotal);
        intent.putExtra(IntentExtra.FEE_CASH_TOTAL, feeCashTotal);
        intent.putExtra(IntentExtra.FEE_YB_TOTAL, feeYbTotal);
        intent.putExtra(IntentExtra.FEE_PREPAY_TOTAL, feePrepayTotal);
        intent.putExtra(IntentExtra.FEE_NEED_CASH_TOTAL, feeNeedCashTotal);
        context.startActivity(intent);
    }
}
