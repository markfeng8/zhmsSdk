package com.wondersgroup.android.sdk.ui.afterpayhome.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.wondersgroup.android.sdk.R;
import com.wondersgroup.android.sdk.adapter.AfterPayHomeAdapter;
import com.wondersgroup.android.sdk.base.MvpBaseActivity;
import com.wondersgroup.android.sdk.constants.Exceptions;
import com.wondersgroup.android.sdk.constants.IntentExtra;
import com.wondersgroup.android.sdk.constants.OrgConfig;
import com.wondersgroup.android.sdk.constants.SpKey;
import com.wondersgroup.android.sdk.entity.AfterHeaderBean;
import com.wondersgroup.android.sdk.entity.AfterPayStateEntity;
import com.wondersgroup.android.sdk.entity.CityBean;
import com.wondersgroup.android.sdk.entity.FeeBillDetailsBean;
import com.wondersgroup.android.sdk.entity.FeeBillEntity;
import com.wondersgroup.android.sdk.entity.HospitalBean;
import com.wondersgroup.android.sdk.entity.HospitalEntity;
import com.wondersgroup.android.sdk.entity.SerializableHashMap;
import com.wondersgroup.android.sdk.entity.Yd0001Entity;
import com.wondersgroup.android.sdk.epsoft.ElectronicSocialSecurityCard;
import com.wondersgroup.android.sdk.ui.afterpayhome.contract.AfterPayHomeContract;
import com.wondersgroup.android.sdk.ui.afterpayhome.presenter.AfterPayHomePresenter;
import com.wondersgroup.android.sdk.ui.paymentdetails.view.PaymentDetailsActivity;
import com.wondersgroup.android.sdk.utils.LogUtil;
import com.wondersgroup.android.sdk.utils.SpUtil;
import com.wondersgroup.android.sdk.widget.selecthospital.CityConfig;
import com.wondersgroup.android.sdk.widget.selecthospital.HospitalPickerView;
import com.wondersgroup.android.sdk.widget.selecthospital.OnCityItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by x-sir on 2018/8/10 :)
 * Function:???????????????
 */
public class AfterPayHomeActivity extends MvpBaseActivity<AfterPayHomeContract.IView,
        AfterPayHomePresenter<AfterPayHomeContract.IView>> implements AfterPayHomeContract.IView {

    private static final String TAG = "AfterPayHomeActivity";
    private RecyclerView recyclerView;
    private TextView tvMoneyNum;
    private TextView tvPayMoney;
    private LinearLayout llNeedPay;
    private AfterPayHomeAdapter mAdapter;
    private HashMap<String, String> mPassParamMap;
    /**
     * ????????????????????????
     */
    private String mOrgName = "?????????????????????";
    private String mOrgCode;
    private String mHiCode; //????????????????????????????????????
    /**
     * ????????????????????????
     */
    private String mAreaName = "?????????";
    /**
     * ??????????????????
     */
    private AfterHeaderBean mHeaderBean = new AfterHeaderBean();
    /**
     * ????????????????????????????????????
     */
    private List<FeeBillDetailsBean> mFeeBillList = new ArrayList<>();
    /**
     * ?????????????????????????????????
     */
    private static final String NOTICE_MESSAGE = "????????????";
    /**
     * ?????????????????? List ??????
     */
    private List<Object> mItemList = new ArrayList<>();

    private HospitalPickerView mCityPickerView;

    @Override
    protected AfterPayHomePresenter<AfterPayHomeContract.IView> createPresenter() {
        return new AfterPayHomePresenter<>();
    }

    /**
     * current package {@link com.wondersgroup.android.sdk.ui.afterpayhome.view} <br/>
     * bind layout content, initialize some data and views you can do it.
     */
    @Override
    protected void bindView() {
        setContentView(R.layout.wonders_group_activity_after_pay_home);
        findViews();
        initData();
        initListener();
    }

    /**
     * activity lifecycle method onRestart, you can do some operator in it.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.i(TAG, "onRestart()");
        backRefreshPager();
    }

    /**
     * ????????????????????????????????????
     */
    private void backRefreshPager() {
        // ??????????????????????????????
        setNeedPayViewVisible(false);
        // ???????????????&????????????????????????
        requestXy0001();
        // ???????????????????????????????????????????????????????????????????????????
        mHeaderBean.setHospitalName("???????????????");
        mItemList.removeAll(mFeeBillList);
        refreshAdapter();
    }

    private void initListener() {
        mCompositeDisposable.add(
                RxView.clicks(tvPayMoney)
                        .throttleFirst(1, TimeUnit.SECONDS)
                        .subscribe(s -> PaymentDetailsActivity.actionStart(
                                AfterPayHomeActivity.this, mOrgCode, mOrgName, mHiCode, false))
        );
    }

    private void initData() {
        mCityPickerView = new HospitalPickerView(this);
        initHeaderData();
        getIntentAndFindAfterPayState();
    }

    private void initHeaderData() {
        String name = SpUtil.getInstance().getString(SpKey.NAME, "");
        String idNum = SpUtil.getInstance().getString(SpKey.ID_NUM, "");
        mHeaderBean.setName(name);
        mHeaderBean.setIdNum(idNum);
        // ???????????????????????????
        mItemList.add(mHeaderBean);
        // ?????????????????????????????????
        mItemList.addAll(mFeeBillList);
        // ???????????????????????????
        mItemList.add(NOTICE_MESSAGE);
        setAdapter();
    }

    private void setAdapter() {
        if (mItemList != null && mItemList.size() > 0) {
            mAdapter = new AfterPayHomeAdapter(this, mItemList);
            recyclerView.setAdapter(mAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    public void refreshAdapter() {
        if (mAdapter != null) {
            mAdapter.refreshAdapter();
        }
    }

    private void getIntentAndFindAfterPayState() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mCompositeDisposable.add(
                Observable.just(intent)
                        .map(Intent::getExtras)
                        .filter(bundle -> bundle != null)
                        .map(bundle -> (SerializableHashMap) bundle.get(IntentExtra.SERIALIZABLE_MAP))
                        .filter(serializableHashMap -> serializableHashMap != null)
                        .map(SerializableHashMap::getMap)
                        .subscribe(stringStringHashMap -> {
                            mPassParamMap = stringStringHashMap;
                            requestXy0001();
                        })
        );
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvMoneyNum = findViewById(R.id.tvMoneyNum);
        tvPayMoney = findViewById(R.id.tvPayMoney);
        llNeedPay = findViewById(R.id.llNeedPay);
    }

    @Override
    public void onXy0001Result(AfterPayStateEntity entity) {
        LogUtil.e(TAG, "onXy0001Result ????????????" + entity.toString());
        String signingStatus = entity.getSigning_status();
        String paymentStatus = entity.getOne_payment_status();
        String phone = entity.getPhone();
        String signDate = entity.getCt_date();
        String feeTotal = entity.getFee_total();
        mOrgCode = entity.getOrg_code();
        mOrgName = entity.getOrg_name();
        mHiCode = entity.getHi_code();

        if (!TextUtils.isEmpty(feeTotal)) {
            tvMoneyNum.setText(feeTotal);
        }

        SpUtil.getInstance().save(SpKey.SIGNING_STATUS, signingStatus);
        SpUtil.getInstance().save(SpKey.PAYMENT_STATUS, paymentStatus);
        SpUtil.getInstance().save(SpKey.PHONE, phone);
        SpUtil.getInstance().save(SpKey.SIGN_DATE, signDate);
        SpUtil.getInstance().save(SpKey.FEE_TOTAL, feeTotal);

        mHeaderBean.setOrgCode(mOrgCode);
        mHeaderBean.setOrgName(mOrgName);
        mHeaderBean.setSigningStatus(signingStatus);
        mHeaderBean.setPaymentStatus(paymentStatus);
        mHeaderBean.setFeeTotal(feeTotal);
        mHeaderBean.setHiCode(mHiCode);
        refreshAdapter();

        requestYd0001();
    }

    @Override
    public void onYd0001Result(final Yd0001Entity entity) {
        mCompositeDisposable.add(
                Observable
                        .just(entity)
                        .doOnNext(this::saveEleCardData)
                        .subscribe(s -> refreshAdapter())
        );
    }

    private void saveEleCardData(Yd0001Entity entity) {
        // ????????????????????????00 ????????? 01 ?????????
        String eleCardStatus = entity.getEleCardStatus();
        LogUtil.i(TAG, "eleCardStatus===" + eleCardStatus);
        mHeaderBean.setEleCardStatus(eleCardStatus);
        SpUtil.getInstance().save(SpKey.ELE_CARD_STATUS, eleCardStatus);
        // ?????????????????????????????????
        if ("01".equals(eleCardStatus)) {
            SpUtil.getInstance().save(SpKey.SIGN_NO, entity.getSignNo());
        } else {
            SpUtil.getInstance().save(SpKey.SIGN_NO, "");
        }
    }

    public void applyElectronicSocialSecurityCard() {
        new ElectronicSocialSecurityCard().enter(this);
    }

    @Override
    public void onYd0003Result(FeeBillEntity entity) {

        // ?????????????????????????????????
        mItemList.removeAll(mFeeBillList);
        if (entity != null) {
            setNeedPayViewVisible(true);
            String feeTotal = entity.getFeeTotal();
            // 00 ????????? 01 ????????????
            String payState = entity.getPayState();
            if ("01".equals(payState)) {
                tvPayMoney.setText("?????????");
                tvPayMoney.setEnabled(false);
            }
            tvMoneyNum.setText(feeTotal);
            mFeeBillList = entity.getDetails();
            mItemList.addAll(1, mFeeBillList);
        } else {
            setNeedPayViewVisible(false);
        }
        refreshAdapter();
    }

    private void setNeedPayViewVisible(boolean visible) {
        llNeedPay.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showLoading(boolean show) {
        showLoadingView(show);
    }

    @Override
    public void onHospitalListResult(HospitalEntity body) {
        mCompositeDisposable.add(
                Observable
                        .just(body)
                        .map(HospitalEntity::getDetails)
                        .filter(detailsBean -> detailsBean != null && detailsBean.size() > 0)
                        .map(detailsBean -> new Gson().toJson(detailsBean))
                        .subscribe(this::showWheelDialog, throwable -> LogUtil.e(TAG, "onError:" + throwable.getMessage()))
        );
    }

    /**
     * ???????????????
     */
    private void showWheelDialog(String json) {
        // ??????????????? iOS ???????????????????????????
        mCityPickerView.init(json);

        CityConfig cityConfig = new CityConfig.Builder()
                .defaultCity(mAreaName)
                .defaultHospital(mOrgName)
                .build();

        mCityPickerView.setConfig(cityConfig);

        mCityPickerView.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(CityBean cityBean, HospitalBean hospitalBean) {
                mAreaName = cityBean.getArea_name();
                mOrgCode = hospitalBean.getOrg_code();
                mOrgName = hospitalBean.getOrg_name();
                mHiCode = hospitalBean.getHi_code();
                mHeaderBean.setHospitalName(mOrgName);
                requestYd0003();
            }

            @Override
            public void onCancel() {
                LogUtil.i(TAG, "onCancel()");
            }
        });

        mCityPickerView.showCityPicker();
    }

    /**
     * ???????????????????????????
     */
    private void requestXy0001() {
        mPresenter.requestXy0001(mPassParamMap);
    }

    /**
     * ?????????????????????????????????
     */
    private void requestYd0001() {
        mPresenter.requestYd0001();
    }

    /**
     * request yd0003 api interface.
     */
    private void requestYd0003() {
        mPresenter.requestYd0003(mOrgCode);
    }

    /**
     * Get hospital list from network, you can pass No.1 param that api version,
     * No.2 param that data type on received data.
     */
    public void getHospitalList() {
        mPresenter.getHospitalList(OrgConfig.GLOBAL_API_VERSION, "01");
    }

    public static void actionStart(Context context, HashMap<String, String> param) {
        if (context == null) {
            return;
        }
        if (param == null || param.isEmpty()) {
            throw new IllegalArgumentException(Exceptions.MAP_SET_NULL);
        }
        // ????????????
        SerializableHashMap sMap = new SerializableHashMap();
        // ??? map ???????????????????????? sMap ???
        sMap.setMap(param);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentExtra.SERIALIZABLE_MAP, sMap);
        Intent intent = new Intent(context, AfterPayHomeActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }
}
