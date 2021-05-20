/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wondersgroup.android.sdk.ui.selfpayfee.contract;

import com.wondersgroup.android.sdk.entity.FeeBillEntity;
import com.wondersgroup.android.sdk.entity.HospitalEntity;

/**
 * Created by x-sir on 2018/10/31 :)
 * Function:资费卡页面接口的契约类
 */
public interface SelfPayFeeContract {

    interface IModel {

    }

    interface IView {

        void showLoading(boolean show);

        void onHospitalListResult(HospitalEntity body);

        void onYd0003Result(FeeBillEntity entity);
    }

    interface IPresenter {

        void getHospitalList(String version, String type);

        void requestYd0003(String orgCode);
    }
}
