package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * Created by Administrator on 2018/12/17.
 */

public interface SubstitutePayDepositContract {
    interface View extends BaseContract.BaseView {
        void showProgressDialog();

        void dismissProgressDialog();

        void setNoPermissionDialog(int msgId);

        void showDialogTip(int msgId);

        void setBalanceInfo(double balance);

        void wxPayReq(String appid, String partnerid, String prepayid, String packageName,
                      String noncestr, String timestamp, String sign);

        void aliPayReq(String aliPayInfo);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLibBalanceInfo();

        void payDeposit(String readerId, double money);

        /**
         * 请求微信支付信息
         *
         * @param payMoney 支付金额
         * @param userIP   用户IP地址
         */
        void requestWXPayInfo(double payMoney, String userIP, String readerId);

        /**
         * 查询微信支付结果
         */
        void requestWXPayResult(String readerId);

        /**
         * 请求支付宝支付信息
         *
         * @param payMoney 支付金额
         * @param userIP   用户IP地址
         */
        void requestAliPayInfo(double payMoney, String userIP, String readerId);

        /**
         * 查询支付宝支付结果
         */
        void requestAliPayResult(String readerId);
    }
}
