package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * 交押金
 * Created by ZhiqiangJia on 2017-10-24.
 */

public interface PayDepositContract {

    interface View extends BaseContract.BaseView {
        void showProgressLoading();

        void dismissProgressLoading();

        void wxPayReq(String appid, String partnerid, String prepayid, String packageName,
                      String noncestr, String timestamp, String sign);

        void aliPayReq(String aliPayInfo);

        void showDialogTip(int resId, boolean refresh);

        void showNoPermissionDialog(int kickOut);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 请求微信支付信息
         *
         * @param payMoney 支付金额
         * @param userIP   用户IP地址
         */
        void requestWXPayInfo(double payMoney, String userIP);

        /**
         * 查询微信支付结果
         */
        void requestWXPayResult();

        /**
         * 请求支付宝支付信息
         *
         * @param payMoney 支付金额
         * @param userIP   用户IP地址
         */
        void requestAliPayInfo(double payMoney, String userIP);

        /**
         * 查询支付宝支付结果
         */
        void requestAliPayResult();
    }
}
