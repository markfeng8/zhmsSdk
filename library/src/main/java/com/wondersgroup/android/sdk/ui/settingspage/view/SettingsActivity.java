package com.wondersgroup.android.sdk.ui.settingspage.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wondersgroup.android.sdk.R;
import com.wondersgroup.android.sdk.base.MvpBaseActivity;
import com.wondersgroup.android.sdk.constants.MapKey;
import com.wondersgroup.android.sdk.constants.OrgConfig;
import com.wondersgroup.android.sdk.constants.SpKey;
import com.wondersgroup.android.sdk.ui.afterpayrules.AfterPayRuleActivity;
import com.wondersgroup.android.sdk.ui.settingspage.contract.SettingsContract;
import com.wondersgroup.android.sdk.ui.settingspage.presenter.SettingsPresenter;
import com.wondersgroup.android.sdk.utils.BrightnessManager;
import com.wondersgroup.android.sdk.utils.LogUtil;
import com.wondersgroup.android.sdk.utils.SpUtil;
import com.wondersgroup.android.sdk.utils.StringUtils;
import com.wondersgroup.android.sdk.utils.WToastUtil;

import java.util.HashMap;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by x-sir on 2018/8/1 :)
 * Function:设置页面
 */
public class SettingsActivity extends MvpBaseActivity<SettingsContract.IView,
        SettingsPresenter<SettingsContract.IView>> implements SettingsContract.IView {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private TextView tvName;
    private TextView tvIcNum;
    private TextView tvSignDate;
    private TextView tvPhone;
    private TextView tvAfterPayState;
    private TextView tvElectronicCardState;
    private TextView tvLookRule;
    private TextView tvTermination;
    private ImageView ivEditPhone;
    private ImageView ivBackground;
    private EditText etPhone;
    private LinearLayout llPhone;
    private EditText etVerifyCode;
    private TextView tvOriginalPhone;
    private TextView tvUpdateTitle;
    private TextView tvOpen;
    private TextView tvPhoneNum;
    private TextView tvGetSmsCode;
    private PopupWindow popupWindow;
    private CountdownView countDownView;
    private View popupView;
    private String mName;
    private String mIdNo;
    private String mCardNo;
    private String mPhone;
    private String mSignDate;
    /**
     * 标志是哪个弹窗， 1 为修改通知手机号，2 为解约医后付
     */
    private int mPopupWindowFlag = -1;
    private String mNoticePhone;

    @Override
    protected SettingsPresenter<SettingsContract.IView> createPresenter() {
        return new SettingsPresenter<>();
    }

    @Override
    protected void bindView() {
        setContentView(R.layout.wonders_group_activity_settings);
        findViews();
        initData();
        initListener();
    }

    @Override
    public void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 解约成功的回调
     */
    @Override
    public void terminationSuccess() {
        finish();
    }

    @Override
    public void onUpdateSuccessResult() {
        // 刷新显示手机号
        if (mPopupWindowFlag == 1) {
            if (!TextUtils.isEmpty(mNoticePhone)) {
                tvPhone.setText(mNoticePhone);
                SpUtil.getInstance().save(SpKey.PHONE, mNoticePhone);
            }
        }
    }

    private void initData() {
        mName = SpUtil.getInstance().getString(SpKey.NAME, "");
        mIdNo = SpUtil.getInstance().getString(SpKey.ID_NUM, "");
        mCardNo = SpUtil.getInstance().getString(SpKey.CARD_NUM, "");
        // 显示签约手机号还是传过来的手机号？
        mPhone = SpUtil.getInstance().getString(SpKey.PHONE, "");
        mSignDate = SpUtil.getInstance().getString(SpKey.SIGN_DATE, "");

        tvName.setText(mName);
        tvIcNum.setText(StringUtils.getMosaicIdNum(mIdNo));
        tvPhone.setText(mPhone);
        tvSignDate.setText(mSignDate);

        String state = "";
        String signingStatus = SpUtil.getInstance().getString(SpKey.SIGNING_STATUS, "");
        // 00未签约（医后付状态给NULL）
        if ("00".equals(signingStatus)) {
            state = "未签约";
            // 01已签约
        } else if ("01".equals(signingStatus)) {
            /*
             * 1：正常（缴清或未使用医后付服务）2：欠费(医后付后有欠费的概要信)
             */
            String paymentStatus = SpUtil.getInstance().getString(SpKey.PAYMENT_STATUS, "");
            if ("1".equals(paymentStatus)) {
                state = "正常";
            } else if ("2".equals(paymentStatus)) {
                state = "欠费";
            }
            // 02 其他
        } else if ("02".equals(signingStatus)) {
            state = "其他";
        }
        tvAfterPayState.setText(state);

        String mobPayStatus = SpUtil.getInstance().getString(SpKey.ELE_CARD_STATUS, "");
        // 00 未签约
        if ("00".equals(mobPayStatus)) {
            tvElectronicCardState.setText("未开通");
            // 01已签约
        } else if ("01".equals(mobPayStatus)) {
            tvElectronicCardState.setText("已开通");
            // 02 其他
        } else if ("02".equals(mobPayStatus)) {
            tvElectronicCardState.setText("其他");
        }
    }

    private void initListener() {
        ivEditPhone.setOnClickListener(v -> {
            mPopupWindowFlag = 1;
            showPopupWindow();
            BrightnessManager.lightoff(SettingsActivity.this);
        });
        // 解约医后付
        tvTermination.setOnClickListener(v -> {
            mPopupWindowFlag = 2;
            showPopupWindow();
            BrightnessManager.lightoff(SettingsActivity.this);
        });
        // 查看协议
        tvLookRule.setOnClickListener(v -> startActivity(
                new Intent(SettingsActivity.this, AfterPayRuleActivity.class)));
    }

    private void findViews() {
        tvName = findViewById(R.id.tvName);
        tvIcNum = findViewById(R.id.tvIcNum);
        tvSignDate = findViewById(R.id.tvSignDate);
        tvPhone = findViewById(R.id.tvPhone);
        ivEditPhone = findViewById(R.id.ivEditPhone);
        tvAfterPayState = findViewById(R.id.tvAfterPayState);
        tvElectronicCardState = findViewById(R.id.tvElectronicCardState);
        tvLookRule = findViewById(R.id.tvLookRule);
        tvTermination = findViewById(R.id.tvTermination);
    }

    private void showPopupWindow() {
        LogUtil.i(TAG, "popupWindow == null ? " + (popupWindow == null));
        if (popupWindow == null) {
            popupView = View.inflate(SettingsActivity.this, R.layout.wonders_group_popupwindow_update_phone, null);
            popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            popupWindow.setOnDismissListener(() -> BrightnessManager.lighton(SettingsActivity.this));
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);

            etPhone = popupView.findViewById(R.id.etPhone);
            llPhone = popupView.findViewById(R.id.llPhone);
            etVerifyCode = popupView.findViewById(R.id.etVerifyCode);
            tvUpdateTitle = popupView.findViewById(R.id.tvUpdateTitle);
            tvOpen = popupView.findViewById(R.id.tvOpen);
            ivBackground = popupView.findViewById(R.id.ivBackground);
            tvPhoneNum = popupView.findViewById(R.id.tvPhoneNum);
            tvOriginalPhone = popupView.findViewById(R.id.tvOriginalPhone);
            countDownView = popupView.findViewById(R.id.countDownView);
            tvGetSmsCode = popupView.findViewById(R.id.tvGetSmsCode);

            // 获取验证码
            tvGetSmsCode.setOnClickListener(v -> {
                if (mPopupWindowFlag == 1) {
                    mNoticePhone = etPhone.getText().toString();
                    if (!TextUtils.isEmpty(mNoticePhone) && mNoticePhone.length() == 11) {
                        tvGetSmsCode.setVisibility(View.GONE);
                        countDownView.setVisibility(View.VISIBLE);
                        countDownView.start(60000);
                        mPresenter.sendVerifyCode(mNoticePhone, OrgConfig.IDEN_CLASS2);
                    } else {
                        WToastUtil.show("手机号为空或不正确！");
                    }
                } else if (mPopupWindowFlag == 2) {
                    tvGetSmsCode.setVisibility(View.GONE);
                    countDownView.setVisibility(View.VISIBLE);
                    countDownView.start(60000);
                    mPresenter.sendVerifyCode(mPhone, OrgConfig.IDEN_CLASS3);
                }
            });

            countDownView.setOnCountdownEndListener(cv -> {
                countDownView.setVisibility(View.GONE);
                tvGetSmsCode.setVisibility(View.VISIBLE);
            });

            // 关闭
            popupView.findViewById(R.id.ivClose).setOnClickListener(v -> {
                popupWindow.dismiss();
                BrightnessManager.lighton(SettingsActivity.this);
            });

            // 开通
            tvOpen.setOnClickListener(v -> {
                String verifyCode = etVerifyCode.getText().toString();
                if (!TextUtils.isEmpty(verifyCode)) {
                    HashMap<String, String> param = new HashMap<>();
                    param.put(MapKey.IDEN_CODE, verifyCode);
                    param.put(MapKey.ID_NO, StringUtils.getMosaicIdNum(mIdNo));
                    param.put(MapKey.CARD_NO, mCardNo);
                    // 修改手机号
                    if (mPopupWindowFlag == 1) {
                        String phone = etPhone.getText().toString();
                        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
                            param.put(MapKey.PHONE, phone);
                            param.put(MapKey.NAME, mName);
                            mPresenter.sendOpenRequest(param);
                        } else {
                            WToastUtil.show("手机号为空或非法！");
                        }
                        // 解约医后付
                    } else if (mPopupWindowFlag == 2) {
                        param.put(MapKey.PHONE, mPhone);
                        mPresenter.termination(param);
                    }
                } else {
                    WToastUtil.show("验证码不能为空！");
                }
            });
        }

        String code = etVerifyCode.getText().toString();
        if (!TextUtils.isEmpty(code)) {
            etVerifyCode.setText("");
        }

        if (mPopupWindowFlag == 1) {
            ivBackground.setImageResource(R.drawable.wonders_group_pop_window2);
            tvUpdateTitle.setText(getString(R.string.wonders_update_notification_phone));
            String phoneText = getString(R.string.wonders_original_phone) + mPhone;
            tvOriginalPhone.setVisibility(View.VISIBLE);
            tvOriginalPhone.setText(phoneText);
            tvOpen.setText("确认修改");
            tvPhoneNum.setVisibility(View.GONE);
            llPhone.setVisibility(View.VISIBLE);
        } else if (mPopupWindowFlag == 2) {
            ivBackground.setImageResource(R.drawable.wonders_group_pop_window1);
            tvUpdateTitle.setText(getString(R.string.wonders_cancel_after_pay));
            tvOriginalPhone.setVisibility(View.INVISIBLE);
            tvOpen.setText("确认取消");
            tvPhoneNum.setVisibility(View.VISIBLE);
            llPhone.setVisibility(View.GONE);
            String phoneContent = "手机号：" + mPhone;
            tvPhoneNum.setText(phoneContent);
        }

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            BrightnessManager.lighton(SettingsActivity.this);
        }
        popupWindow.showAtLocation(SettingsActivity.this.findViewById(R.id.activity_settings),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public static void actionStart(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupView != null) {
            popupView.destroyDrawingCache();
            popupView = null;
        }
    }
}
