/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wondersgroup.android.sdk.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wondersgroup.android.sdk.R;
import com.wondersgroup.android.sdk.entity.FeeBillDetailsBean;
import com.wondersgroup.android.sdk.entity.SelfPayHeaderBean;
import com.wondersgroup.android.sdk.ui.paymentrecord.view.FeeRecordActivity;
import com.wondersgroup.android.sdk.ui.selfpayfee.view.SelfPayFeeActivity;

import java.util.List;

/**
 * Created by x-sir on 2018/8/24 :)
 * Function:自费卡首页数据的 Adapter
 */
public class SelfPayFeeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SelfPayFeeAdapter";
    /**
     * 头部信息类型
     */
    private static final int TYPE_HEADER = 1;
    /**
     * 未缴清账单类型
     */
    private static final int TYPE_LIST = 2;
    /**
     * 初始化布局加载器
     */
    private LayoutInflater mLayoutInflater;
    /**
     * 当前Item的类型
     */
    private int mCurrentType = -1;
    private Context mContext;
    private List<Object> mItemList;

    public SelfPayFeeAdapter(Context context, List<Object> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 设置并刷新数据
     */
    public void setItemList(List<Object> itemList) {
        this.mItemList = itemList;
        notifyDataSetChanged(); // 刷新适配器
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_HEADER:
                viewHolder = new HeaderViewHolder(mLayoutInflater.inflate(R.layout.wonders_group_item_self_pay_header, parent, false));
                break;
            case TYPE_LIST:
                viewHolder = new ListViewHolder(mLayoutInflater.inflate(R.layout.wonders_group_item_after_pay_list, parent, false));
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.setData((SelfPayHeaderBean) mItemList.get(position));
                break;
            case TYPE_LIST:
                ListViewHolder listViewHolder = (ListViewHolder) holder;
                listViewHolder.setData((FeeBillDetailsBean) mItemList.get(position));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItemList != null && position < mItemList.size()) {
            Object object = mItemList.get(position);
            if (object instanceof SelfPayHeaderBean) {
                mCurrentType = TYPE_HEADER;
            } else if (object instanceof FeeBillDetailsBean) {
                mCurrentType = TYPE_LIST;
            }
        }
        return mCurrentType;
    }

    /**
     * 1.Header 类型
     */
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llPayRecord;
        private TextView tvHospitalName;
        private TextView tvName;
        private TextView tvIcNum;
        private String orgCode;
        private String orgName;

        HeaderViewHolder(View itemView) {
            super(itemView);
            llPayRecord = itemView.findViewById(R.id.llPayRecord);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvName = itemView.findViewById(R.id.tvName);
            tvIcNum = itemView.findViewById(R.id.tvIcNum);
            initListener();
        }

        private void initListener() {
            /*
             * 点击选择医院
             */
            tvHospitalName.setOnClickListener(v -> getHospitalList());
            /*
             * 点击缴费记录
             */
            llPayRecord.setOnClickListener(v -> FeeRecordActivity.actionStart(mContext));
        }

        /**
         * 获取医院列表（资费卡页面直接弹出不做任何判断）
         */
        private void getHospitalList() {
            ((SelfPayFeeActivity) mContext).getHospitalList();
        }

        @SuppressLint("SetTextI18n")
        public void setData(SelfPayHeaderBean afterHeaderBean) {
            if (afterHeaderBean != null) {
                String name = afterHeaderBean.getName();
                String icNum = afterHeaderBean.getIcNum();
                String hospitalName = afterHeaderBean.getHospitalName();
                orgCode = afterHeaderBean.getOrgCode();
                orgName = afterHeaderBean.getOrgName();
                tvName.setText(name);
                tvIcNum.setText(icNum);
                tvHospitalName.setText(hospitalName);
            }
        }
    }

    /**
     * 2.List 数据类型
     */
    class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFeeName;
        private TextView tvDepartment;
        private TextView tvTimestamp;
        private TextView tvMoney;

        ListViewHolder(View itemView) {
            super(itemView);
            tvFeeName = itemView.findViewById(R.id.tvFeeName);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvMoney = itemView.findViewById(R.id.tvMoney);
        }

        public void setData(FeeBillDetailsBean detailsBean) {
            if (detailsBean != null) {
                String orderName = detailsBean.getOrdername();
                String hisOrderTime = detailsBean.getHis_order_time();
                String feeOrder = detailsBean.getFee_order();
                tvFeeName.setText(orderName);
                tvTimestamp.setText(hisOrderTime);
                tvMoney.setText(feeOrder);
            }
        }
    }

}
