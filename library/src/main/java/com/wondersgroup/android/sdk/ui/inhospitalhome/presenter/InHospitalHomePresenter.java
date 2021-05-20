/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wondersgroup.android.sdk.ui.inhospitalhome.presenter;

import com.wondersgroup.android.sdk.base.MvpBasePresenter;
import com.wondersgroup.android.sdk.entity.Cy0001Entity;
import com.wondersgroup.android.sdk.entity.HospitalEntity;
import com.wondersgroup.android.sdk.entity.Yd0001Entity;
import com.wondersgroup.android.sdk.net.callback.HttpRequestCallback;
import com.wondersgroup.android.sdk.ui.afterpayhome.contract.AfterPayHomeContract;
import com.wondersgroup.android.sdk.ui.afterpayhome.model.AfterPayHomeModel;
import com.wondersgroup.android.sdk.ui.inhospitalhome.contract.InHospitalHomeContract;
import com.wondersgroup.android.sdk.ui.inhospitalhome.model.InHospitalHomeModel;
import com.wondersgroup.android.sdk.utils.LogUtil;
import com.wondersgroup.android.sdk.utils.WToastUtil;

/**
 * Created by x-sir on 2018/11/7 :)
 * Function:住院页面的 Presenter
 */
public class InHospitalHomePresenter<T extends InHospitalHomeContract.IView>
        extends MvpBasePresenter<T> implements InHospitalHomeContract.IPresenter {

    private static final String TAG = "InHospitalHomePresenter";
    private InHospitalHomeContract.IModel mInHosModel = new InHospitalHomeModel();
    private AfterPayHomeContract.IModel mModel = new AfterPayHomeModel();

    @Override
    public void getHospitalList(String version, String type) {
        showLoading(true);
        mModel.getHospitalList(version, type, new HttpRequestCallback<HospitalEntity>() {
            @Override
            public void onSuccess(HospitalEntity body) {
                LogUtil.i(TAG, "get defaultHospital list success~");
                showLoading(false);
                if (isNonNull()) {
                    mViewRef.get().onHospitalListResult(body);
                }
            }

            @Override
            public void onFailed(String errCodeDes) {
                LogUtil.e(TAG, "get defaultHospital list failed!");
                showLoading(false);
                WToastUtil.show(errCodeDes);
            }
        });
    }

    @Override
    public void requestCy0001(String orgCode, String inState) {
        showLoading(true);
        mInHosModel.requestCy0001(orgCode, inState, new HttpRequestCallback<Cy0001Entity>() {
            @Override
            public void onSuccess(Cy0001Entity entity) {
                LogUtil.i(TAG, "requestCy0001() -> success~");
                showLoading(false);
                if (isNonNull()) {
                    mViewRef.get().onCy0001Result(entity);
                }
            }

            @Override
            public void onFailed(String errMsg) {
                LogUtil.e(TAG, "requestCy0001() -> failed!");
                showLoading(false);
                WToastUtil.show(errMsg);
                if (isNonNull()) {
                    mViewRef.get().onCy0001Result(null);
                }
            }
        });
    }

    @Override
    public void requestYd0001() {
        showLoading(true);
        mModel.requestYd0001(new HttpRequestCallback<Yd0001Entity>() {
            @Override
            public void onSuccess(Yd0001Entity entity) {
                LogUtil.i(TAG, "requestYd0001() -> onSuccess()");
                showLoading(false);
                if (isNonNull()) {
                    mViewRef.get().onYd0001Result(entity);
                }
            }

            @Override
            public void onFailed(String errCodeDes) {
                LogUtil.e(TAG, "requestYd0001() -> onFailed()===" + errCodeDes);
                showLoading(false);
                WToastUtil.show(errCodeDes);
            }
        });
    }

    private void showLoading(boolean show) {
        if (isNonNull()) {
            mViewRef.get().showLoading(show);
        }
    }
}
