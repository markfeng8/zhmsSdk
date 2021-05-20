package com.wondersgroup.android.sdk.entity;

/**
 * code by markfeng
 * <p>
 * create on 2021-05-20 10:09
 */
public class WondersOutParams {

    private String type;//0：获取渠道信息，渠道号  1：申领社保卡  2：支付验证

    private String socialSecurityNum;//社保卡号
    private String name;//姓名
    private String signNo;//签发号

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSocialSecurityNum() {
        return socialSecurityNum;
    }

    public void setSocialSecurityNum(String socialSecurityNum) {
        this.socialSecurityNum = socialSecurityNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignNo() {
        return signNo;
    }

    public void setSignNo(String signNo) {
        this.signNo = signNo;
    }
}
