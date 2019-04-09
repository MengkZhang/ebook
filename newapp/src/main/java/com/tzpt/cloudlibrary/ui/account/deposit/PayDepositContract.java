package com.tzpt.cloudlibrary.ui.account.deposit;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;

import java.util.List;

/**
 * Created by ZhiqiangJia on 2017-10-09.
 */

public interface PayDepositContract {

    interface View extends BaseContract.BaseView {
        /**
         * 展示押金信息
         *
         * @param totalDeposit 押金余额
         * @param list         余额列表
         */
        void showUserDeposit(double penalty, double totalDeposit, double occupyDeposit,
                             List<DepositBalanceBean> list, List<DepositBalanceBean> penaltyList);

        /**
         * 显示全屏加载
         */
        void showLoading();

        /**
         * 加载成功，全屏加载隐藏
         */
        void dismissLoading();

        /**
         * 阻塞加载
         */
        void showProgressLoading();

        /**
         * 隐藏阻塞加载
         */
        void dismissProgressLoading();

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

        /**
         * 显示对话框提示
         *
         * @param resId   提示内容
         * @param refresh 返回上一页面，是否刷新
         */
        void showDialogTip(int resId, boolean refresh);

        /**
         * 限制金额提示
         *
         * @param money 金额
         */
        void showLimitMoneyTip(String money);

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 请求用户押金信息
         */
        void requestUserDeposit();

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
