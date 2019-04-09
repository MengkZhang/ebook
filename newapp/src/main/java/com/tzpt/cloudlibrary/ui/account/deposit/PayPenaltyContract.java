package com.tzpt.cloudlibrary.ui.account.deposit;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 交罚金
 * Created by tonyjia on 2018/8/28.
 */
public interface PayPenaltyContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void showFinishMsgDialog(int resId);

        void showPayPenaltyDialog(String payPenalty);

        void showMsgDialog(int resId);

        void setPenaltyText(String penaltyText, boolean submitEnable);

        void pleaseLoginTip();

        /**
         * 设置微信请求参数
         *
         * @param appid       APPID
         * @param partnerid   商户ID
         * @param prepayid    预支付ID
         * @param packageName 包名
         * @param noncestr    随机字符串
         * @param timestamp   时间戳
         * @param sign        签名字段
         */
        void wxPayReq(String appid, String partnerid, String prepayid, String packageName,
                      String noncestr, String timestamp, String sign);

        /**
         * 设置支付宝请求参数
         *
         * @param aliPayInfo 支付宝支付信息
         */
        void aliPayReq(String aliPayInfo);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

//        void getUserDepositInfo();

        void handleUserPenalty(String libCode);

        void requestWeChatPay(String userIP);

        void requestAlipay(String userIP);

        /**
         * 查询微信支付结果
         */
        void requestWXPayResult();

        /**
         * 查询支付宝支付结果
         */
        void requestAliPayResult();

        void releaseHandler();
    }
}
