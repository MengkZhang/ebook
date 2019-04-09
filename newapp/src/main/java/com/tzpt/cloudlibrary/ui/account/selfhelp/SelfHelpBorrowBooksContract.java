package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.SelfHelpBookInfoBean;

import java.util.List;

/**
 * 自助借书
 */
public interface SelfHelpBorrowBooksContract {

    interface View extends BaseContract.BaseView {

        /**
         * 显示加载对话框
         */
        void showProgressDialog();

        /**
         * 隐藏加载对话快
         */
        void dismissProgressDialog();

        /**
         * 展示借书列表
         *
         * @param bookList 借书列表
         */
        void setSelfBookList(List<SelfHelpBookInfoBean> bookList);

        /**
         * 展示空列表
         */
        void setSelfBookEmpty();

        /**
         * 展示借书合计信息
         *
         * @param sum           数量
         * @param money         金额
         * @param deposit       押金
         * @param isShowDeposit 是否展示押金
         */
        void showBookTotalInfo(String sum, String money, String deposit, boolean isShowDeposit);

        /**
         * 对话框提示
         *
         * @param tips 提示语
         */
        void showDialogTips(String tips, boolean finish);

        /**
         * 对话框提示
         *
         * @param tipsId 提示语资源ID
         */
        void showDialogTips(int tipsId);

        /**
         * 借书成功
         */
        void borrowBooksSuccess();

        /**
         * 登录提示
         */
        void pleaseLoginTip();

        /**
         * 处理罚金提示框
         *
         * @param penaltyInfo 提示语
         */
        void showPenaltyDialogTips(String penaltyInfo, String libCode, boolean finish);

        void showIdCardRegisterInfoDialog(int resId);

        void showRechargeDialog(int msgId);

        void borrowSuccessRefreshBookList(List<SelfHelpBookInfoBean> bookInfoBeanList, int successBookSum, int errorBookSum);

        void showHasOverdueBookTipsDialog();

        void turnToScan(int usableBorrowNum, boolean needDeposit,
                        double penalty, double usableDeposit, int borrowedNum,
                        double totalPrice, double occupyDeposit);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 获取借书列表
         */
        void getReaderAndBookInfo();

        /**
         * 移除缓存列表中的书籍
         *
         * @param position 列表索引值
         */
        void removeDataByIndex(int position);

        /**
         * 确定借书
         */
        void submitBorrowBookList();

        /**
         * 获取书籍信息
         *
         * @param barCode 条码号
         */
        void getBookInfo(String barCode);

        /**
         * 获取用户可用押金信息
         */
        void getReaderDeposit(String barCode);

        void getBorrowInfoTurnToScan();
    }
}
