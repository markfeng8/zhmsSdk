/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wondersgroup.android.sdk.R;
import com.wondersgroup.android.sdk.constants.SpKey;
import com.wondersgroup.android.sdk.entity.CombineDetailsBean;
import com.wondersgroup.android.sdk.entity.DetailHeadBean;
import com.wondersgroup.android.sdk.entity.DetailPayBean;
import com.wondersgroup.android.sdk.entity.FeeBillDetailsBean;
import com.wondersgroup.android.sdk.entity.OrderDetailsEntity;
import com.wondersgroup.android.sdk.ui.paymentdetails.view.PaymentDetailsActivity;
import com.wondersgroup.android.sdk.utils.SpUtil;
import com.wondersgroup.android.sdk.utils.StringUtils;
import com.wondersgroup.android.sdk.utils.WToastUtil;
import com.wondersgroup.android.sdk.widget.FeeDetailLayout;
import com.wondersgroup.android.sdk.widget.PayItemLayout;

import java.util.List;

/**
 * Created by x-sir on 2018/9/9 :)
 * Function:缴费详情的 Adapter
 */
public class PaymentDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "PaymentDetailsAdapter";
    /**
     * 头部信息类型
     */
    private static final int TYPE_HEADER = 1;
    /**
     * 未缴清账单类型
     */
    private static final int TYPE_LIST = 2;
    /**
     * 支付视图类型
     */
    private static final int TYPE_PAY = 3;
    private Context mContext;
    private List<Object> mItemList;
    /**
     * 初始化布局加载器
     */
    private LayoutInflater mLayoutInflater;
    /**
     * 当前Item的类型
     */
    private int mCurrentType = -1;

    public PaymentDetailsAdapter(Context context, List<Object> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 刷新适配器
     */
    public void refreshAdapter() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_HEADER:
                viewHolder = new HeaderViewHolder(mLayoutInflater.inflate(
                        R.layout.wonders_group_item_detail_pay_header, parent, false));
                break;
            case TYPE_LIST:
                viewHolder = new ListViewHolder(mLayoutInflater.inflate(
                        R.layout.wonders_group_item_detail_pay_list, parent, false));
                break;
            case TYPE_PAY:
                viewHolder = new PayViewHolder(mLayoutInflater.inflate(
                        R.layout.wonders_group_item_detail_pay_type, parent, false));
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
                headerViewHolder.setData((DetailHeadBean) mItemList.get(position));
                break;
            case TYPE_LIST:
                ListViewHolder listViewHolder = (ListViewHolder) holder;
                listViewHolder.setData((CombineDetailsBean) mItemList.get(position), position);
                break;
            case TYPE_PAY:
                PayViewHolder payViewHolder = (PayViewHolder) holder;
                payViewHolder.setData((DetailPayBean) mItemList.get(position));
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
            if (object instanceof DetailHeadBean) {
                mCurrentType = TYPE_HEADER;
            } else if (object instanceof CombineDetailsBean) {
                mCurrentType = TYPE_LIST;
            } else if (object instanceof DetailPayBean) {
                mCurrentType = TYPE_PAY;
            }
        }
        return mCurrentType;
    }

    /**
     * 1.Header 类型
     */
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvIdNum;
        private TextView tvHospitalName;
        private TextView tvOrderNum;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvIdNum = itemView.findViewById(R.id.tvIdNum);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvOrderNum = itemView.findViewById(R.id.tvOrderNum);
        }

        @SuppressLint("SetTextI18n")
        public void setData(DetailHeadBean headBean) {
            String name = headBean.getName();
            String socialNum = headBean.getSocialNum();
            String hospitalName = headBean.getHospitalName();
            String orderNum = headBean.getOrderNum();

            if (!TextUtils.isEmpty(name)) {
                tvName.setText(name);
            }
            if (!TextUtils.isEmpty(socialNum)) {
                // 如果是门诊显示社保卡号，如果是自费卡显示身份证号
                String idNum = SpUtil.getInstance().getString(SpKey.ID_NUM, "");
                tvIdNum.setText("身份证号：" + StringUtils.getMosaicIdNum(idNum));
            }

            if (!TextUtils.isEmpty(hospitalName)) {
                tvHospitalName.setText(hospitalName);
            }
            if (!TextUtils.isEmpty(orderNum)) {
                tvOrderNum.setVisibility(View.VISIBLE);
                tvOrderNum.setText("订单编号：" + orderNum);
            }
        }
    }

    /**
     * 2.List 数据类型
     */
    class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderName;
        private TextView tvMoney;
        private TextView tvOrderTime;
        private ImageView ivArrow;
        private LinearLayout llItem;
        private LinearLayout llDetails;
        private String hisOrderNo;
        private int position;
        private List<OrderDetailsEntity.DetailsBean> spreadDetails;
        private CombineDetailsBean mCombineDetails;

        ListViewHolder(View itemView) {
            super(itemView);
            tvOrderName = itemView.findViewById(R.id.tvOrderName);
            tvMoney = itemView.findViewById(R.id.tvMoney);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            llItem = itemView.findViewById(R.id.llItem);
            llDetails = itemView.findViewById(R.id.llDetails);
            initListener();
        }

        /**
         * 设置展开详情的监听器
         */
        private void initListener() {
            llItem.setOnClickListener(v -> spreadDetails());
        }

        /**
         * 展开订单详情
         */
        private void spreadDetails() {
            // 1.先判断数据是否已经请求下来了，没有就先去请求
            if (spreadDetails == null) {
                if (mContext instanceof PaymentDetailsActivity) {
                    ((PaymentDetailsActivity) mContext).getOrderDetails(hisOrderNo, position);
                }

            } else { // 如果有数据就判断是显示还是隐藏
                boolean spread = mCombineDetails.isSpread();
                llDetails.setVisibility((spread) ? View.GONE : View.VISIBLE);
                ivArrow.setImageResource(spread ? R.drawable.wonders_group_down_arrow : R.drawable.wonders_group_up_arrow);
                mCombineDetails.setSpread(!spread);
            }
        }

        @SuppressLint("SetTextI18n")
        void setData(CombineDetailsBean combineDetails, int position) {
            this.mCombineDetails = combineDetails;
            this.position = position;
            if (mCombineDetails != null) {
                FeeBillDetailsBean defaultDetails = mCombineDetails.getDefaultDetails();
                spreadDetails = mCombineDetails.getOpenDetails();

                if (defaultDetails != null) {
                    String orderName = defaultDetails.getOrdername();
                    String feeOrder = defaultDetails.getFee_order();
                    String orderTime = defaultDetails.getHis_order_time();
                    hisOrderNo = defaultDetails.getHis_order_no();

                    if (!TextUtils.isEmpty(orderName)) {
                        tvOrderName.setText(orderName);
                    }
                    if (!TextUtils.isEmpty(feeOrder)) {
                        tvMoney.setText(feeOrder);
                    }
                    if (!TextUtils.isEmpty(orderTime)) {
                        tvOrderTime.setText("账单时间：" + orderTime);
                    }
                }

                resetViewState();
                if (mCombineDetails.isSpread()) {
                    spreadDetailsLayout();
                }
            }
        }

        /**
         * 重置 View 的状态，不管如何，在装载是否显示展开数据之前调用，这样可以防止 Item 被复用之前状态的影响
         */
        private void resetViewState() {
            llDetails.setVisibility(View.GONE);
            ivArrow.setImageResource(R.drawable.wonders_group_down_arrow);
        }

        /**
         * 装配展开详情的数据
         */
        private void spreadDetailsLayout() {
            if (spreadDetails != null && spreadDetails.size() > 0) {
                llDetails.setVisibility(View.VISIBLE);
                ivArrow.setImageResource(R.drawable.wonders_group_up_arrow);

                // 先清除就布局中的 Item
                int count = llDetails.getChildCount();
                if (count > 0) {
                    llDetails.removeAllViews();
                }
                for (int i = 0; i < spreadDetails.size(); i++) {
                    StringBuilder stringBuilder = new StringBuilder();
                    OrderDetailsEntity.DetailsBean detailsBean = spreadDetails.get(i);
                    String itemName = detailsBean.getItemname();
                    String price = detailsBean.getPrice();
                    String amount = detailsBean.getAmount();
                    String unit = detailsBean.getUnit();

                    stringBuilder
                            .append(price)
                            .append("*")
                            .append(amount)
                            .append(unit);

                    FeeDetailLayout layout = new FeeDetailLayout(mContext);
                    layout.setFeeName(itemName);
                    layout.setFeeNum(stringBuilder.toString());
                    llDetails.addView(layout);
                }
            }
        }
    }

    /**
     * 3.支付的数据类型
     */
    class PayViewHolder extends RecyclerView.ViewHolder {
        private PayItemLayout plTotalMoney;
        private PayItemLayout plPersonalPay;
        private PayItemLayout plYiBaoPay;
        private TextView tvPayType;
        private LinearLayout llPayType;
        private LinearLayout llYiBaoPayLayout;
        private ToggleButton tbYiBaoEnable;
        private String totalPay;
        private String personalPay;
        private String yiBaoPay;

        PayViewHolder(View itemView) {
            super(itemView);
            plTotalMoney = itemView.findViewById(R.id.plTotalMoney);
            plPersonalPay = itemView.findViewById(R.id.plPersonalPay);
            plYiBaoPay = itemView.findViewById(R.id.plYiBaoPay);
            tvPayType = itemView.findViewById(R.id.tvPayType);
            llPayType = itemView.findViewById(R.id.llPayType);
            llYiBaoPayLayout = itemView.findViewById(R.id.llYiBaoPayLayout);
            tbYiBaoEnable = itemView.findViewById(R.id.tbYiBaoEnable);
            initData();
            initListener();
        }

        private void initData() {
            String mobPayStatus = SpUtil.getInstance().getString(SpKey.ELE_CARD_STATUS, "");
            String cardType = SpUtil.getInstance().getString(SpKey.CARD_TYPE, "");
            // 如果未开通医保移动支付并且不是自费卡，就显示医保移动支付开关
            if (!"01".equals(mobPayStatus) && !"2".equals(cardType)) {
                llYiBaoPayLayout.setVisibility(View.VISIBLE);
            }
        }

        private void initListener() {
            tbYiBaoEnable.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    String mobPayStatus = SpUtil.getInstance().getString(SpKey.ELE_CARD_STATUS, "");
                    if ("01".equals(mobPayStatus)) { // 已开通
                        SpUtil.getInstance().save(SpKey.YIBAO_ENABLE, true);
                    } else { // 未开通
                        SpUtil.getInstance().save(SpKey.YIBAO_ENABLE, false);
                        WToastUtil.show("您未开通医保移动支付，不能进行医保结算！");
                        tbYiBaoEnable.setChecked(false);

                        // TODO: 2018/10/11 跳转到开通医保移动支付页面，当开通完成后回来需要刷新页面，隐藏开关布局
                    }

                } else {
                    SpUtil.getInstance().save(SpKey.YIBAO_ENABLE, false);
                }

                // 处理显示隐藏医保金额布局
                plYiBaoPay.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                // 如果允许医保支付，则个人支付就是个人支付的金额，如果不允许医保支付，则个人支付就是总支付的金额
                plPersonalPay.setFeeNum(isChecked ? personalPay : totalPay);

                // 如果允许医保支付，则个人需要支付个人部分即可，如果不允许，则显示全部支付金额
                ((PaymentDetailsActivity) mContext).setPersonalPayAmount(isChecked ? personalPay : totalPay);
            });

            // 选择支付方式 Layout
            llPayType.setOnClickListener(v -> ((PaymentDetailsActivity) mContext).showSelectPayTypeWindow(type -> {
                if (type == 1) {
                    tvPayType.setText("支付宝");
                } else if (type == 2) {
                    tvPayType.setText("微信");
                } else if (type == 3) {
                    tvPayType.setText("银行卡");
                }
            }));
        }

        public void setData(DetailPayBean payBean) {
            if (payBean != null) {
                totalPay = payBean.getTotalPay();
                personalPay = payBean.getPersonalPay();
                yiBaoPay = payBean.getYibaoPay();

                if (!TextUtils.isEmpty(totalPay)) {
                    plTotalMoney.setVisibility(View.VISIBLE);
                    plTotalMoney.setFeeName("总计金额：");
                    plTotalMoney.setFeeNum(totalPay);
                }
                if (!TextUtils.isEmpty(personalPay)) {
                    plPersonalPay.setVisibility(View.VISIBLE);
                    plPersonalPay.setFeeName("现金部分：");
                    plPersonalPay.setFeeNum(personalPay);
                    // 如果个人支付为 0，隐藏选择支付方式 Layout
                    if (Double.parseDouble(personalPay) == 0) {
                        llPayType.setVisibility(View.GONE);
                    }
                }

                // 如果是门诊才需要显示医保金额，如果是自费卡不需要显示医保金额
                String cardType = SpUtil.getInstance().getString(SpKey.CARD_TYPE, "");
                if ("0".equals(cardType)) {
                    if (!TextUtils.isEmpty(yiBaoPay)) {
                        plYiBaoPay.setVisibility(View.VISIBLE);
                        plYiBaoPay.setFeeName("医保部分：");
                        plYiBaoPay.setFeeNum(yiBaoPay);
                    }
                }
            }
        }
    }

    public interface OnCheckedCallback {
        void onSelected(int type);
    }
}
